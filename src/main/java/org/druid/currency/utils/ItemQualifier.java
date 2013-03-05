package org.druid.currency.utils;

import java.util.Hashtable;

import org.druid.currency.Item;

/**
 * This class provides a mechanism to fully qualify a vanilla item.
 * @author Ian Lawson <a href="mailto:ian.lawson@redhat.com">ian.lawson@redhat.com</a>
 *
 */
public class ItemQualifier
{
  // Private constructor, class methods are static
  private ItemQualifier()
  {
    
  }
  
  /**
   * Simple qualify method that adds the cache name and uuid to generate the inferable name for Items aspects.
   * @param name aspect name
   * @param cacheName data cache name/label
   * @param uuid unique (within cache) ID for item
   * @return composite fully qualified name
   */
  public static String qualify( String name, String cacheName, String uuid )
  {
    return ( cacheName == null ? "" : cacheName ) + "." + uuid + "." + name;
  }
  
  /**
   * Complex qualify method that adds fully qualified names from an existing item.
   * @param item existing vanilla item
   * @param cacheName data cache name to mark each aspect with
   * @param uuid uuid to mark each aspect with
   * @return converted item
   */
  public static Item qualify( Item item, String cacheName, String uuid )
  {
    Item newItem = new Item(item.getCreationUTC());
    
    // Copy the comparitor list
    newItem.setComparitors(item.getComparitors());
    
    // Translate the existing aspects
    Hashtable<String,Object> contents = item.getContents();
    
    for( String key : contents.keySet())
    {
      Object value = contents.get(key);
      
      // Fully qualify the name
      String qualifiedName = ItemQualifier.qualify(key, cacheName, uuid);
      
      if( value.getClass().getCanonicalName().equals( "java.util.String"))
      {
        newItem.addString(qualifiedName, (String)value );
      }
      else
      {
        try
        {
          newItem.addObject(qualifiedName, value);
        }
        catch( Exception exc )
        {
          // Bad Uth - will never get this exception because we are building from an existing item
        }
      }
    }
    
    return newItem;
  }
}
