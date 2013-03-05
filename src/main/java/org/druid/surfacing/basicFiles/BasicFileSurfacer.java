package org.druid.surfacing.basicFiles;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.druid.currency.Item;
import org.druid.currency.utils.ItemNameTools;
import org.druid.exceptions.SurfacingException;
import org.druid.surfacing.ISurfacer;

public class BasicFileSurfacer implements ISurfacer
{
  private String _location = null;
  private List<String> _fileList = null;
  private List<String> _dataDictionary = null;
  
  private void cacheFileList() throws SurfacingException
  {
    _fileList = new ArrayList<String>();
    
    // Given a single directory build a list of .elu files. This is a snapshot 
    // and is effectively temporally stamped
    File location = new File( _location );
    
    // Basic checks
    if( !location.exists())
    {
      throw new SurfacingException( "Target host does not exist." );
    }
    
    if( !location.isDirectory())
    {
      throw new SurfacingException( "Target host is not a directory." );
    }
    
    File[] files = location.listFiles();
    
    for( File file : files )
    {
      if( file.exists() && !file.isDirectory())
      {
        try
        {
          if( file.getCanonicalPath().endsWith(".elu"))
          {
            _fileList.add(file.getCanonicalPath());
          }
        }
        catch( Exception exc )
        {
          // TODO Add logging
          System.out.println( "Failed to get canonical name for file." );
        }
      }
    }
  }
  
  /**
   * Private readFile method for extracting and rebuilding an Item from a file.
   * @param target target file to convert back into an item
   * @return the converted item
   * @throws SurfacingException if the conversion process fails
   */
  private static Item readFile( String target ) throws SurfacingException
  {
    // Read and quickly convert the contents
    try
    {   
      Scanner fileRead = new Scanner( new File( target ) );
      
      // Order - Creation UTC then all contents, all delimited by ":::"
      String data = fileRead.nextLine();
      
      String[] components = BasicFileSurfacer.quickStrip(data);
      
      if( !components[0].equals( "CREATED"))
      {
        fileRead.close();
        throw new SurfacingException( "Invalid file format, missing mandatory CREATED component - " + target );
      }
      
      Item workingItem = new Item( Long.parseLong(components[1]));
      
      // Order - read the comparitors and store them in the item shell
      data = fileRead.nextLine();
      
      components = BasicFileSurfacer.quickStrip(data);
      
      String[] comparitors = components[1].split( "[,]");
      
      workingItem.setComparitors(Arrays.asList(comparitors));
      
      // Now process the contents
      while( fileRead.hasNextLine())
      {
        data = fileRead.nextLine();
        
        if( data.indexOf( ":::") != -1 )
        {
          components = BasicFileSurfacer.quickStrip(data);
        
          workingItem.addString(components[0], components[1]);
        }
      }
      
      fileRead.close();
      
      return workingItem;
    }
    catch( SurfacingException sexc )
    {
      throw sexc;
    }
    catch( Exception exc )
    {
      throw new SurfacingException( "Unable to read file due to " + exc.toString() );
    }
  }
  
  /**
   * Quick and dirty re-converter that expands the name:::value data to two components *but*
   * also deals with empty components (that need to be retain for volumetric analysis.
   * @param data data to process
   * @return two component array of [0]=name [1]=value
   * @throws SurfacingException if the data does not convert
   */
  private static String[] quickStrip( String data ) throws SurfacingException
  {
    String[] components = data.split( ":{3}");
    
    if( components.length == 1 )
    {
      // Fully populated empty items case
      String emptyComponents[] = new String[2];
      emptyComponents[0] = components[0];
      emptyComponents[1] = new String( "" );
      
      return emptyComponents;
    }
    if( components.length != 2 )
    {
      throw new SurfacingException( "Incorrect number of components in provided data.");
    }
    else
    {
      return components;
    }
  }
  
  /**
   * Internal Basic File based cache data dictionary method. This is to satisfy the interface requirements
   * for being able to surface the data dictionary of the cache.
   * @throws SurfacingException if data dictionary caching fails
   */
  private void cacheDataDictionary() throws SurfacingException
  {
    if( _fileList == null )
    {
      this.cacheFileList();      
    }
    
    _dataDictionary = new ArrayList<String>();
    
    // For each file read it, extract the field identifiers (last component of the name) and build a unique list
    // of them for reference
    int fileCount = 0;
    
    for( String target : _fileList )
    {
      // BAD UTH - for Debug
      fileCount++;
      System.out.print( ( fileCount % 1000 == 0 ? fileCount : ".") );
      
      try
      {
        Item item = BasicFileSurfacer.readFile(target);
        
        Map<String,Object> contents = item.getContents();
        Set<String> aspects = contents.keySet();
        
        for( String aspect : aspects )
        {
          String fieldComponents = ItemNameTools.getFieldComponents(aspect);
          
          if( !( _dataDictionary.contains(fieldComponents)))
          {
            _dataDictionary.add(fieldComponents);
          }
        }
      }
      catch( Exception exc )
      {
        // BAD UTH - better logging needed here
        System.out.println( "Failed to process file " + target + " due to " + exc.toString());
      }
    }
    
    System.out.println( "" );
  }
  
  @Override
  /**
   * This overridden method sets the host directory for persisted files (the data cache)
   * @param location directory to store the persisted files in 
   */
  public void setLocation(String location)
  {
    _location = location;    
  }

  @Override
  public void setParameter(String name, String value)
  {
    
  }

  @Override
  public List<Item> surface(String name, String value, boolean exactMatch) throws SurfacingException
  {
    // If we haven't cached the files, build the cache now
    if( _fileList == null )
    {
      this.cacheFileList();
    }

    return null;
  }

  @Override
  public List<Item> surface(Map<String, String> tokens)
      throws SurfacingException
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean match(Item item, String cacheName) throws SurfacingException
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public List<String> surfaceDataDictionary() throws SurfacingException
  {
    return _dataDictionary;
  }
  
  /**
   * Public test handle for testing the private methods used internally, will be removed.
   * @param fileLocation qualified file name to test
   * @return constructed item from the file contents
   */
  public static Item testFileRead( String fileLocation ) throws SurfacingException
  {
    return BasicFileSurfacer.readFile( fileLocation );
  }

  @Override
  /**
   * Optional method for surfacers that allow re-caching of the contents (i.e. ones that
   * cache the data rather than reading data from an external source).
   * 
   * This should re-cache the contents if needed and the data dictionary if applicable.
   */
  public void synchroniseCache() throws SurfacingException
  {
    this.cacheDataDictionary();
  }
}
