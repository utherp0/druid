package org.druid.persistence.lucene;

import java.util.Hashtable;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.druid.currency.Item;

/**
 * This class provides a converter that takes a generic DRUID Item and produces a
 * LUCENE 3.0.2 Document that matches it. This document can then be used in the persistence
 * of the Item to a searchable LUCENE directory.
 * @author Ian Lawson <@link 
 *
 */
public class ItemConverter
{
  /**
   * private constructor, class provides static methods only.
   */
  private ItemConverter()
  {
    
  }
  /**
   * Convert method that takes an DRUID Item and produces a LUCENE 3.0.2 document.
   * @param item item to convert to a LUCENE document
   * @return fully constructed LUCENE document
   */
  @SuppressWarnings("unused")
  public static Document convert( Item item )
  {
    // Instantiate the document
    Document workingDocument = new Document();
    
    Hashtable<String,Object> contents = item.getContents();
    
    for( String key : contents.keySet())
    {
      Object value = contents.get(key);
      
      if( value.getClass().getCanonicalName().equals( "java.lang.String"))
      {
        String actualValue = (String)value;
        
        if( actualValue == null ) 
        {
          actualValue = "null";
        }
        
        // Create a matching indexed field within the document for each string component
        workingDocument.add( new Field( key, actualValue, Field.Store.YES, Field.Index.ANALYZED));
      }
    }
    
    // Store the comparitor list unindexed
    StringBuffer concatenatedComparitors = null;
    
    for( String comparitorContent : item.getComparitors() )
    {
      if( concatenatedComparitors == null )
      {
        concatenatedComparitors = new StringBuffer( comparitorContent );
      }
      else
      {
        concatenatedComparitors.append( "," + comparitorContent );
      }
    }
    
    workingDocument.add( new Field( "comparitorList", concatenatedComparitors.toString(), Field.Store.YES, Field.Index.NO));
    
    // Build a uniqueness indicator by combining all the values of the comparitor fields into a single field
    StringBuffer comparitor = new StringBuffer();
    
    for( String comparitorKey : item.getComparitors() )
    {
      String comparitorValue = (String)contents.get( comparitorKey );
      comparitor.append( comparitorValue );
    }
    
    workingDocument.add( new Field( "comparitor", comparitor.toString(), Field.Store.YES, Field.Index.ANALYZED));
    
    String convertedDate = Long.toString(System.currentTimeMillis());
    
    workingDocument.add( new Field( "converter.date", convertedDate, Field.Store.YES, Field.Index.ANALYZED));

    // Finally add the composite field for item level searching
    List<String> tokens = item.getContents(false);
    StringBuffer tokenOutput = null;
    
    for( String token : tokens )
    {
      if( tokenOutput == null )
      {
        tokenOutput = new StringBuffer( token );
      }
      else
      {
        tokenOutput.append( " " + token );
      }
    }
    
    if( tokenOutput != null )
    {
      workingDocument.add( new Field( "luceneSearchContents", tokenOutput.toString(), Field.Store.YES, Field.Index.ANALYZED ));
    }
      
    // Store the created date to retain referencing
    Long creation = new Long( item.getCreationUTC());
    workingDocument.add( new Field( "creator.date", creation.toString(), Field.Store.YES, Field.Index.ANALYZED));
    
    return workingDocument;
  }
}
