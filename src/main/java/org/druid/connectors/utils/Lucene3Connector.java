package org.druid.connectors.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.druid.currency.Item;
import org.druid.exceptions.ConnectorException;
import org.druid.persistence.lucene.ItemConverter;

/**
 * This is a helper class that adds connector methods for using LUCENE3.x indexes.
 * @author Ian Lawson <a href="mailto:ian.lawson@redhat.com">ian.lawson@redhat.com</a>
 */
public class Lucene3Connector
{
  private IndexReader _indexerDeletionHandle = null;
  private IndexWriter _indexerHandle = null;

  /**
   * Simple reporting mechanism that builds a map of report component->String.
   * @param location physical location to report on
   * @return Map representing report components
   */
  public Map<String,String> report( String location ) throws ConnectorException
  {
    try
    {
      IndexReader infoReader = this.setupIndexReader( location );

      int entityCount = infoReader.numDocs();
      long lastModified = this.getLastModified(location);
      boolean locked = this.isLocked( location );

      HashMap<String,String> map = new HashMap<String,String>();
      
      map.put(new String("itemCount"), (new Integer(entityCount)).toString() );
      map.put("locked", ( locked ? "true" : "false" ));
      map.put("lastModified", ( new Long(lastModified).toString()));
            
      return map;
    }
    catch( Exception exc )
    {
      throw new ConnectorException( "Unable to build report components for " + location + " due to " + exc.toString());
    }
  }

  /**
   * Internal method for calculating last modified, used by the report() mechanism.
   * @param indexDefinition index to get last modified for
   * @return millisecond time of last modified
   * @throws IndexerActionException if the lookup fails
   */
  private long getLastModified( String location ) throws ConnectorException
  {
    try
    {
      FSDirectory fileDirectory = FSDirectory.open(new File(location));

      return IndexReader.lastModified(fileDirectory);
    }
    catch( Exception exc )
    {
      throw new ConnectorException( "Unable to create index reader due to " + exc.toString());
    }
  }

  /**
   * Method to setup the IndexReader for an existing LUCENE3 index.
   * @param location physical location of FSDirectory index
   * @return valid IndexReader for working with this index
   * @throws ConnectorException if index does not exist at this point
   */
  private IndexReader setupIndexReader( String location ) throws ConnectorException
  {
    File target = new File( location );
    IndexReader reader = null;

    if( !( target.exists() ) || !( target.isDirectory()))
    {
      throw new ConnectorException( "Target " + location + " is not a valid file or directory for LUCENE3");
    }

    try
    {
      FSDirectory fileDirectory = FSDirectory.open(new File(location));
      reader = IndexReader.open(fileDirectory, false);

      return reader;
    }
    catch( Exception exc )
    {
      throw new ConnectorException( "Unable to create index reader due to " + exc.toString());
    }
  }

  /**
   * Method to determine if the target LUCENE3 index is locked.
   * @param location physical location of index
   * @return true if the index at this point is locked
   * @throws ConnectorException if accessing the index is not possible at this point
   */
  private boolean isLocked( String location ) throws ConnectorException
  {
    try
    {
      FSDirectory fileDirectory = FSDirectory.open(new File( location ));

      return IndexWriter.isLocked(fileDirectory);
    }
    catch( Exception exc )
    {
      throw new ConnectorException( "Unable to check lock status on " + location + " due to " + exc.toString());
    }
  }

  /**
   * Lazy load method for setting up the index writer for a target physical FSDirectory location.
   * @param location physical location for the FSDirectory
   * @return validated IndexWriter for pushing Items into the index
   * @throws ConnectorException if IndexWriter cannot be initialised
   */
  private IndexWriter setupIndexWriter( String location ) throws ConnectorException
  {
    File target = new File( location );
    IndexWriter writer = null;

    if( !( target.exists() ) || !( target.isDirectory()))
    {
      throw new ConnectorException( "Target " + location + " is not a valid file or directory");
    }

    try
    {
      FSDirectory fileDirectory = FSDirectory.open(target);

      writer = new IndexWriter(fileDirectory, new StandardAnalyzer( Version.LUCENE_30,Collections.emptySet()), IndexWriter.MaxFieldLength.UNLIMITED);
      writer.setUseCompoundFile(false);

      return writer;
    }
    catch( Exception exc )
    {
      throw new ConnectorException( "Unable to create index reader due to " + exc.toString());
    }
  }

  /**
   * Finalise method that tidies up the accessors, should *always* be called when the indexer is disposed of.
   * @param indexDefinition index definition to clean up
   * @throws IndexerActionException unable to clean up the indexer
   */
  public void finish( String location ) throws ConnectorException
  {
    IndexReader reader = (IndexReader)this.attainIndexerDeletionHandle(location);
    IndexWriter writer = (IndexWriter)this.attainIndexerHandle(location);

    try
    {
      reader.flush();
      reader.close();

      writer.close(true);
    }
    catch( Exception exc )
    {
      throw new ConnectorException( "Failed to clean the indexer " + location + " due to " + exc.toString());
    }
  }

  /**
   * Simple blunt force deletion method for an index.
   * @param location physical location to delete
   * @throws ConnectorException if the deletion fails
   */
  public void deleteIndex( String location ) throws ConnectorException
  {
    // First - sanity checks to see if the index is valid
    File target = new File( location );

    if( !( target.exists()) || !( target.isDirectory()))
    {
      throw new ConnectorException( "Unable to delete index " + location + " - not a valid file or directory");
    }

    // Ensure we haven't locked the files
    try
    {
      IndexReader reader = this.setupIndexReader( location );
      IndexWriter writer = this.setupIndexWriter( location );

      reader.flush();
      reader.close();

      writer.close();
    }
    catch( Exception exc )
    {
      // Do nothing, this is just a cleansing approach
    }

    // Remove each file in the index directory
    for( File file : target.listFiles())
    {
      try
      {
        if( !file.delete())
        {
          file.deleteOnExit();
          //throw new IndexerActionException( "Unable to delete " + file.getCanonicalPath());
        }
      }
      catch( Exception exc )
      {
        try
        {
          throw new ConnectorException( "Failed to delete " + file.getCanonicalPath() + " due to " + exc.toString());
        }
        catch( IOException exc2 )
        {
          throw new ConnectorException( "Unable to delete unreachable file due to " + exc2.toString());
        }
      }
    }
  }

  /**
   * Blunt force create index method.
   * @param location physical location for index
   * @throws ConnectorException if the index cannot be created
   */
  public void createIndex( String location ) throws ConnectorException
  {
    // If the target exists and is not a directory, throw an exception
    File target = new File( location );

    if( target.exists() && !( target.isDirectory()))
    {
      throw new ConnectorException( "Cannot create a directory at " + location + " - exists and is a file");
    }

    // Resiliance - create the directory if it doesn't exist
    if( !target.exists())
    {
      try
      {
        if( !( target.mkdir()))
        {
          throw new ConnectorException( "Unable to create the index directory " + location );
        }
      }
      catch( Exception exc )
      {
        throw new ConnectorException( "Failed to create the directory " + location );
      }
    }

    // Attempt to create a writer
    IndexWriter test = this.setupIndexWriter( location );

    try
    {
      test.close();
    }
    catch( Exception exc )
    {
      throw new ConnectorException( "Failed to setup index due to " + exc.toString() );
    }
  }

  /**
   * Lazy load instantiator for an indexer handle.
   * @param location physical location for index
   * @return LUCENE3 index writer for index access 
   * @throws ConnectorException if the index writer cannot be attained
   */
  public IndexWriter attainIndexerHandle( String location ) throws ConnectorException
  {
    _indexerHandle = this.setupIndexWriter( location );

    return _indexerHandle;
  }

  /**
   * Wrapper to attain an index deletion handle. Unfortunately in LUCENE3 this is the badly 
   * named IndexReader which is the only way (!!!) to delete an item from an index.  
   * @param location physical location of target index
   * @return IndexReader for deletion purposes.
   * @throws ConnectorException if the IndexReader cannot be attained
   */
  public IndexReader attainIndexerDeletionHandle( String location ) throws ConnectorException
  {
    _indexerDeletionHandle = this.setupIndexReader( location );

    return _indexerDeletionHandle;
  }

  /**
   * Single Item add method.
   * @param item the DRUID item to add to the LUCENE3 index
   * @param location the physical location of the index
   * @param overwrite whether or not to overwrite the item (TODO)
   * @throws ConnectorException if the operation of adding the item fails
   */
  public void add( Item item, String location, boolean overwrite ) throws ConnectorException
  {
    // Convert the item to a LUCENE format document
    Document document = ItemConverter.convert(item);
    
    try
    {
      // First - if overwrite is set remove the existing element
      if( overwrite )
      {
        this.delete( item, location);
      }

      // Thirdly attain the indexer handle and add the document - also optimise and close XXX May be performance bottleneck
      IndexWriter writer = this.attainIndexerHandle( location );

      writer.addDocument( document );
      writer.optimize();
      writer.close();
    }
    catch( Exception exc )
    {
      exc.printStackTrace();
      throw new ConnectorException( "Failed to add item due to " + exc.toString());
    }
  }

  /**
   * Multiple item addition method. This converts and adds a list of Items to a target physical FSDirectory index.
   * @param items list of Items to add to the persistence directory
   * @param location physical location of the LUCENE index
   * @param overwrite whether or not to overwrite existing copies (TBD)
   * @return items that have failed to be added
   * @throws ConnectorException if a serious exception occurs that causes a hard failure of the method
   */
  public List<Item> add( List<Item> items, String location, boolean overwrite ) throws ConnectorException
  {
    ArrayList<Item> failedItems = new ArrayList<Item>();

    // If overwrite is active grab the index reader
    if( overwrite )
    {
      for( Item item : items )
      {
        this.delete( item, location);
      }
    }

    // Now process the entities
    IndexWriter indexWriter = this.attainIndexerHandle( location );

    for( Item item : items )
    {
      try
      {
        // Convert to document format
        Document document = ItemConverter.convert(item);

        indexWriter.addDocument(document);
      }
      catch( Exception exc )
      {
        // XXX Log the failure
        System.err.println( "Failed to add document due to " + exc.toString() );

        failedItems.add( item );
      }
    }

    // XXX Should be controlled
    try
    {
      indexWriter.optimize();
    }
    catch( Exception exc )
    {
      // XXX Log the failure
      System.err.println( "Failed to optimize index at " + location + " due to " + exc.toString() );
    }
    finally
    {
      try
      {
        indexWriter.close();
      }
      catch( Exception exc )
      {
        // XXX Log the failure
        System.err.println( "Failed to close index at " + location + " due to " + exc.toString() );
      }
    }

    return failedItems;
  }

  /**
   * TBD deletion method. Need to work out how to uniquely identify a record. This will *probably*
   * be using a hash constructed of the values of the comparitor fields and stored within the document itself.
   * @param item item to store
   * @param location physical location to check for duplicate
   * @throws ConnectorException if the deletion mechanism fails
   */
  public void delete( Item item, String location ) throws ConnectorException
  {
    //IndexReader deletionHandle = null;

    try
    {
      //deletionHandle = (IndexReader)attainIndexerDeletionHandle( location );
    }
    catch( Exception exc )
    {
      throw new ConnectorException( "Unable to delete item due to " + exc.toString() );
    }

    try
    {
      // TBD - need to associate an Item with a persisted one, needs thought
    }
    catch( Exception exc )
    {
      throw new ConnectorException( "Unable to delete item due to " + exc.toString());
    }
  }

  /**
   * Implementation of the deleteAll method. This removes all entities from an index *but* leaves the index intact,
   * used for maintenance purposes.
   * @param indexDefinition index to clean down.
   * @throws IndexerActionException if unable to delete all entities from index
   */
  public void deleteAll( String location ) throws ConnectorException
  {
    try
    {
      IndexWriter handle = (IndexWriter)this.attainIndexerHandle( location );

      handle.deleteAll();
      handle.close();
    }
    catch( Exception exc )
    {
      throw new ConnectorException( "Unable to remove all documents in location " + location + " due to " + exc.getMessage());
    }
  }
}
