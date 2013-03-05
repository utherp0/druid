package org.druid.currency.utils;

import java.util.UUID;

import org.druid.exceptions.ItemNameFormatException;

/**
 * Class containing item name component utilities for extraction and ease of use.
 * @author Ian Lawson <a href="mailto:ian.lawson@redhat.com">ian.lawson@redhat.com</a>.
 */
public class ItemNameTools
{
  // Private constructor, class contains static methods.
  private ItemNameTools()
  {
  }

  /**
   * Helper constant for indicating no UTC in UUID.
   */
  public static final long NO_UTC = -1l;
  
  /**
   * Helper method for extracting the UUID from a DRUID field name. The UUID
   * is *always* the second component, i.e. cacheName.UUID.fieldname(.s)
   * @param name DRUID field name to process
   * @return the extracted UUID
   * @throws ItemNameFormatException if the name is the incorrect format
   */
  public static String getUUID( String name ) throws ItemNameFormatException
  {
    // Basic check, name must be a string
    if( name == null )
    {
      throw new ItemNameFormatException( "No name to process." );
    }
    
    // Basic check, item name must have at *least* three components
    String[] components = name.split( "[.]" );
    
    if( components.length < 3 )
    {
      throw new ItemNameFormatException( "Name provided has too few components" );
    }
    
    // Other wise the UUID is *always* the second component
    return components[1];
  }

  /**
   * Helper method for extracting the embedded UTC in time-stamped UUID. This format of 
   * UUID for use in temporal stamped DRUID objects uses UUID_UTC as a format.
   * @param UUID UUID to extract UTC from
   * @return UTC long value or {@link org.druid.currency.utils.ItemNameTools.NO_UTC NO_UTC} if no UTC is embedded
   * @throws ItemNameFormatException
   * @throws NumberFormatException
   */
  public static long extractUUIDEmbeddedUTC( String UUID ) throws ItemNameFormatException, NumberFormatException
  {
    // Basic check, no UTC embedded in UUID
    if( UUID.indexOf( "_" ) == -1 ) return NO_UTC;
    
    String[] components = UUID.split( "[_]" );
    
    // Basic check, UTC embedded UUID will *only* have two components
    if( components.length == 1 )
    {
      // No UTC
      return NO_UTC;
    }
    else if( components.length != 2 )
    {
      throw new ItemNameFormatException( "UTC Embedded UUID should be of format UUID_UTC" );
    }

    // Attempt to cast the second component into a long
    return Long.parseLong( components[1] );
  }
  
  /**
   * Quick and dirty UUID generator. This uses the 'randomUUID' method of UUID and appends
   * "_" and the UTC system time of the host if needed.
   * @param addUTC if true then the system time is appended.
   * @return UUID (and _systemTime if required)
   */
  public static String generateUUID( boolean addUTC )
  {
    UUID uniqueID = UUID.randomUUID();
    
    return ( addUTC ? uniqueID.toString() + "_" + System.currentTimeMillis() : uniqueID.toString() );
  }
  
  /**
   * Helper method to get the cache name from a DRUID item.
   * @param name DRUID Item name to process
   * @return the cache name if present
   * @throws ItemNameFormatException if the Item Name does not conform to the DRUID naming structure
   */
  public static String getCacheName( String name ) throws ItemNameFormatException
  {
    // Basic check, name must be a string
    if( name == null )
    {
      throw new ItemNameFormatException( "No name to process." );
    }
    
    // Basic check, item name must have at *least* three components
    String[] components = name.split( "[.]" );
    
    if( components.length < 3 )
    {
      throw new ItemNameFormatException( "Name provided has too few components" );
    }
    
    // Other wise the cache name is *always* the first component
    return components[0];
  }
  
  /**
   * This helper method returns all field components of a DRUID Item Name. This entails
   * all components *except* the cache name and UUID.
   * @param name name to process
   * @return all field components except the cache name and UUID
   * @throws ItemNameFormatException if the Item Name does not conform to the DRUID naming conventions.
   */
  public static String getFieldComponents( String name ) throws ItemNameFormatException
  {
    // Basic check, name must be a string
    if( name == null )
    {
      throw new ItemNameFormatException( "No name to process." );
    }
    
    // Basic check, item name must have at *least* three components
    String[] components = name.split( "[.]" );
    
    if( components.length < 3 )
    {
      throw new ItemNameFormatException( "Name provided has too few components" );
    }
    
    StringBuffer workingFields = null;
    
    for( int loop = 2; loop < components.length; loop++ )
    {
      if( workingFields == null )
      {
        workingFields = new StringBuffer( components[loop] );
      }
      else
      {
        workingFields.append( "." + components[loop]);
      }
    }
    
    return workingFields.toString();
  }
}
