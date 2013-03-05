package org.druid.persistence.basicFiles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.druid.currency.Item;
import org.druid.currency.utils.ItemNameTools;
import org.druid.exceptions.PersistenceException;
import org.druid.persistence.IPersister;

/**
 * Persister for storing the Items in Basic Files.
 * @author Ian Lawson (<a href="mailto:ian.lawson@redhat.com">ian.lawson@redhat.com</a>).
 *
 */
public class BasicFilePersister implements IPersister
{
  private File _targetDirectory = null;
  private String _uuid = null;

  @Override
  /**
   * Implementation of the set location method. This stores the physical location of the file to use.
   */
  public void setLocation(String location) throws PersistenceException
  {
    if( _targetDirectory != null ) return;
    
    _targetDirectory = new File( location );
    
    if( !( _targetDirectory.exists() ) )
    {
      throw new PersistenceException( "No such target directory in BasicFilePersister " + location );
    }

    if( !( _targetDirectory.isDirectory() ) )
    {
      throw new PersistenceException( "Target directory is not a directory in BasicFilePersister " + location );
    }
  }

  /**
   * Implementation of the set parameter method. In the case of the Basic File Persister this is the
   * uuid to be used for the item/file.
   * @param name Parameter name to store.
   * @param value uuid to store.
   * @throws PersistenceException is the parameter name is not uuid
   */
  @Override
  public void setParameter(String name, String value) throws PersistenceException
  {
    switch( name.toLowerCase() )
    {
      case "uuid" : _uuid = value;
        break;
        
      default: throw new PersistenceException( "Unknown parameter " + name );
    }
  }

  /**
   * Helper method for returning the target file name fully qualified
   * @return the fully qualified name
   * @throws PersistenceException if any of the required parameters have not been set
   */
  private String getTargetLocation() throws PersistenceException
  {
    if( _uuid == null && _targetDirectory == null )
    {
      throw new PersistenceException( "Persister not initialised (missing parameters).");
    }
    
    return _targetDirectory + File.separator + _uuid + ".elu";
  }
  
  /**
   * Helper method for determining if an item has already been persisted.
   * @param item item to check for persistence
   * @return true if a file exists for the item in the target location
   * @throws PersistenceException if initialisation parameters have not been set
   */
  private boolean itemExists( Item item ) throws PersistenceException
  {
    if( _uuid == null && _targetDirectory == null )
    {
      throw new PersistenceException( "Persister not initialised (missing parameters).");
    }
    
    String location = this.getTargetLocation();
    
    File testFile = new File( location );
    
    return testFile.exists();
  }
  
  /**
   * Implementation of the remove item method. In the case of the Basic File Persister this
   * deletes the physical file.
   * @param item item to remove. This is associated to the uuid which is set separately.
   * @return true if the file is removed correctly
   * @throws PersistenceException not thrown but inherited via interface
   */
  @Override
  public boolean removeItem(Item item) throws PersistenceException
  {
    String targetLocation = getTargetLocation();
    
    File targetFile = new File( targetLocation );

    return targetFile.delete();
  }

  /**
   * 
   */
  @Override
  public boolean persistItem(Item item, boolean overwrite) throws PersistenceException
  {
    // Caveat - this method uses the *provided* uuid as an indicator of file location. This
    // abstracts the item contents from the target storage indicator which could be an overhead, 
    // but it allows the system to persist vanilla items (non-qualified items) as well as fully qualified.
    //
    // Also for sake of simplicity the persister just uses String objects from the aspects - the non-String
    // objects are discarded **BAD UTH**
    //
    // For fully qualified items simply call the item name utils to get the uuid and use that as 
    // the provided parameter to the object.
    
    // First check parameters have been setup correctly
    if( _uuid == null && _targetDirectory == null )
    {
      throw new PersistenceException( "Persister not initialised (missing parameters).");
    }
    
    // Prepare the target file
    String filename = this.getTargetLocation();
    
    // If overwrite then remove the file if it exists
    if( overwrite )
    {
      @SuppressWarnings("unused")
      boolean success = this.removeItem(item);
    }
    else
    {
      if( this.itemExists(item))
      {
        throw new PersistenceException( "Overwrite disabled and item is already persisted.");
      }
    }
    
    // Debatable behaviour model - open file now, write aspects as extracted
    try
    {
      PrintWriter out = new PrintWriter( new FileOutputStream( filename ) );
      
      // Manually map the created date as a separate field
      out.print( "CREATED:::" + Long.toString( item.getCreationUTC()) +"\n" );
      
      // Manually store the comparitors for re-constituting the item
      List<String> comparitors = item.getComparitors();
      
      StringBuffer compBuffer = null;
      
      for( String comparitor : comparitors )
      {
        if( compBuffer == null )
        {
          compBuffer = new StringBuffer( "COMPARITORS:::" + comparitor );
        }
        else
        {
          compBuffer.append( "," + comparitor );
        }
      }
      
      out.print( compBuffer.toString() + "\n");
    
      // Now split the aspects and discard the non-java.util.String ones (for now, **BAD UTH**)
      Map<String,Object> contents = item.getContents();
    
      for( String key : contents.keySet() )
      {
        Object value = contents.get( key );
      
        if( value.getClass().getCanonicalName().equals( "java.lang.String"))
        {
          String payload = (String)value;
        
          // Convert the data to store-able
          String output = key + ":::" + payload + "\n";
          out.print( output );      
        }
      }
    
      out.close();
    }
    catch( Exception exc )
    {
      throw new PersistenceException( "File output failure due to " + exc.toString() );
    }
        
    return false;
  }

  @Override
  /**
   * Currently just throws an exception - the nature of the basic file persistence means that
   * the uuid is presented separately to the item, and in this case multiple items cannot be
   * persisted to a single uuid.
   */
  public List<Item> persistItems(List<Item> items, boolean overwrite) throws PersistenceException
  {
    ArrayList<Item> failedItems = new ArrayList<Item>();
    
    for( Item item : items )
    {
      if( !( this.persistItem(item, overwrite)))
      {
        failedItems.add(item);
      }
    }
    
    return failedItems;
  }

  @Override
  /**
   * Implementation of contains method for items for Basic File Persistence.
   * 
   * This works by building the target file name based on a field name from within
   * the item and then checks if the file exists.
   * @param item to perform check for
   * @return true if the file exists (and hence the item is persisted)
   * @throws PersistenceException if the contains check fails
   */
  public boolean contains(Item item) throws PersistenceException
  {
    try
    {
      Map<String,Object> contents = item.getContents();
      
      Set<String> keys = contents.keySet();
      
      if( keys.isEmpty())
      {
        throw new PersistenceException( "Provided item has no content");
      }
      
      String[] keysArray = (String[])keys.toArray();
      
      _uuid = ItemNameTools.getUUID(keysArray[0]);
      
      String targetFileForCheck = this.getTargetLocation();
      
      File checkFile = new File( targetFileForCheck );
      
      return checkFile.exists();      
    }
    catch( Exception exc )
    {
      throw new PersistenceException( "Contains check failed due to " + exc.toString() );
    }
  }

  @Override
  public void initialise( String location ) throws PersistenceException
  {
    _targetDirectory = new File( location );
    
    if( ( _targetDirectory.exists() ) && !( _targetDirectory.isDirectory() ) )
    {
      throw new PersistenceException( "Target directory provided exists and is not a directory : " + location );
    }

    if( !( _targetDirectory.exists() ) )
    {
      // Attempt to create the directory
      _targetDirectory.mkdir();
      
      if( !( _targetDirectory.exists()))
      {
        throw new PersistenceException( "Unable to create directory at location " + location );
      }
    }
  }

  @Override
  public void finalise() throws PersistenceException
  {
  }

  @Override
  public Map<String, String> report() throws PersistenceException
  {
    HashMap<String,String> report = new HashMap<String,String>();
    
    try
    {
      // First - initialisation check
      if( _targetDirectory == null )
      {
        throw new PersistenceException( "Unable to generate report, persister not initialised.");
      }
    
      if( !( _targetDirectory.exists()))
      {
        throw new PersistenceException( "Target directory (" + _targetDirectory.getCanonicalPath() + ") does not exist.");
      }
    
      if( !( _targetDirectory.isDirectory()))
      {
        throw new PersistenceException( "Target directory (" + _targetDirectory.getCanonicalPath() + ") is not a directory.");
      }
    
      // 1: File count
      File[] files = _targetDirectory.listFiles();
      
      report.put( "fileCount", Integer.toString(files.length));
      report.put( "lastModified", Long.toString(_targetDirectory.lastModified()));
      
      return report;
    }
    catch( Exception exc )
    {
      throw new PersistenceException( "Generate report failed due to " + exc.toString() );
    }
    
  }
}
