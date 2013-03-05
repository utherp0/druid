package org.druid.currency;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.druid.exceptions.ContentNotUniqueException;
import org.druid.exceptions.InvalidObjectTypeException;
import org.druid.exceptions.NoSuchAspectException;

/**
 * This class represents a single lowest common denominator Item for a DRUID system.
 * @author Ian Lawson (<a href="mailto:ian.lawson@redhat.com">ian.lawson@redhat.com</a>).
 */
public class Item implements Serializable
{
  // Required static for Serializable interface
  private static final long serialVersionUID = 1L;

  // Class Properties
  private final Hashtable<String,Object> _contents = new Hashtable<>();
  private List<String> _comparitors = new Vector<String>(); 
  private long _creationUTC = 0;
  
  /**
   * Parameterless constructor, empty item
   */
  public Item()
  {
    _creationUTC = System.currentTimeMillis();
  }
  
  /**
   * Basic constructor for retaining previous creation UTC (for fully qualifying vanilla Items).
   * @param creationUTC pre-created creation time
   */
  public Item( long creationUTC )
  {
    _creationUTC = creationUTC;
  }
    
  /**
   * Deep copy constructor with provided contents
   * @param contents existing contents for creation of a new item
   * @param comparitors item comparitors for uniqueness checks
   */
  public Item( Hashtable<String,Object> contents, List<String> comparitors )
  {
	  this(contents, comparitors, System.currentTimeMillis());
  }
  
  /**
   * Dependency injection constructor with time.
   * @param contents contents to store
   * @param comparitors comparitors to store
   * @param originalCreationUTC original UTC creation time to store
   */
  public Item( Hashtable<String,Object> contents, List<String> comparitors, long originalCreationUTC )
  {
    this._contents.putAll(contents);
    this._comparitors.addAll(comparitors);
    this._creationUTC = originalCreationUTC;
  }
  
  /**
   * Deep item copy constructor.
   * @param original original item to deep copy
   */
  public Item( Item original )
  {
	  this(original.getContents(), original.getComparitors(), original.getCreationUTC());
  }
  
  /**
   * Comparitors mutator. This allows dependency injection of the comparitors list
   * when re-building an item from storage.
   * @param comparitors comparitor list to store
   */
  public void setComparitors( List<String> comparitors )
  {
    _comparitors = comparitors;
  }
  
  /**
   * Contents accessor.
   * @return the contents of this item
   */
  public Hashtable<String,Object> getContents()
  {
    return _contents;
  }
  
  /**
   * Add comparitor mutator. Note that *ALL* comparitors are substring matches
   * of the last component of an Aspect name, i.e. "myData.myId.(extras).comparitor".
   * This is to allow comparisons across items in different data caches.
   * @param comparitor comparitor to add
   */
  public void addComparitor( String comparitor )
  {
    if( !( _comparitors.contains(comparitor)))
    {
      _comparitors.add(comparitor);
    }
  }
  
  /**
   * Remove comparitor mutator.
   * @param comparitor comparitor to remove from comparitors
   */
  public void removeComparitor( String comparitor )
  {
    if( _comparitors.contains(comparitor))
    {
      _comparitors.remove(comparitor);
    }
  }
  
  /**
   * Aspect matching method for use in comparitor checking. *NOTE* this
   * uses endnames (blah.blah.blah.ENDNAME) for comparison and does an
   * object type and an object content check.
   * @param targetEndname endname to compare
   * @param value value to compare
   * @return true if the item contains an Aspect with the endname and the same object value
   */
  public boolean aspectMatches( String targetEndname, Object value )
  {
    for( String key : _contents.keySet())
    {
      if( key.endsWith("." + targetEndname))
      {
        Object contents = _contents.get(key);
        
        if( value.getClass() != contents.getClass())
        {
          return false;
        }
        
        return value.getClass() == contents.getClass();
      }
    }
    
    return false;
  }
  
  /**
   * Comparitors accessor.
   * @return the comparitors for this item
   */
  public List<String> getComparitors()
  {
    return _comparitors;
  }
  
  /**
   * Creation UTC accessor.
   * @return the stored creation UTC
   */
  public long getCreationUTC()
  {
    return _creationUTC;
  }
  
  /**
   * Contents aggregator. This method returns the entire textual contents as a list of Strings,
   * deduping the contents depending on the parameter.
   * @param dedupe true if the returned aggregated contents need to be deduped
   * @return
   */
  public List<String> getContents( boolean dedupe )
  {
    ArrayList<String> workingList = new ArrayList<String>();
    
    for( String key : _contents.keySet() )
    {
      Object value = _contents.get(key);
       
      if( value.getClass() == String.class)
      {
        String content = (String)value;
        
        if( dedupe )
        {
          if( !(workingList.contains(content)))
          {
            workingList.add(content);
          }
        }
        else
        {
          workingList.add(content);
        }
      }      
    }
    
    return workingList;
  }
  
  /**
   * Add object to item mutator.
   * @param name name of the object to add
   * @param value the object to add
   */
  public void addObject( String name, Object value )
  {    
    _contents.put(name, value); 
  }
  
  /**
   * Simple add string method for adding an Aspect to the item.
   * @param identifier name to store the aspect under
   * @param value value to store for the aspect
   */
  public void addString(String identifier, String value) 
  {
	_contents.put(identifier, value);
  } 
  
  /**
   * Add String method for aspect aggregation - this allows multiple values (String) to be stored against the same single name aspect.
   * @param name name to store
   * @param value value to store (or aggregate if it already exists *and* the boolean aggregate is set to true
   * @param aggregate whether to aggregate or not. If set to not then a duplicate field exception is thrown
   * @throws ContentNotUniqueException name already exists in Item *and* Aggregate is set to false
   * @throws InvalidObjectTypeException if the name already exists *and* aggregate is set to true but the existing object is not a string
   */
  public void addString( String name, String value, boolean aggregate ) throws ContentNotUniqueException, InvalidObjectTypeException
  {
    if( _contents.containsKey(name) && !aggregate )
    {
      throw new ContentNotUniqueException( "Duplicate string field addition attempt with non-aggregation using name " + name );
    }
    else if( _contents.containsKey(name))
    {
      Object currentObject = _contents.get(name);
      
      if( currentObject.getClass() != String.class)
      {
        throw new InvalidObjectTypeException( "Expected String, found " + currentObject.getClass().getCanonicalName() + " for field " + name );
      } 
            
      String valueToAdd = (String)_contents.get(name);
      
      value = valueToAdd + " " + value;
    }
    
    _contents.put(name,value);
  }
  
  /**
   * Equivalency method. This method returns true *if* the comparitor fields
   * are identical *and* the contents of the comparitor fields in this item match
   * the contents of the comparitor fields in the item to compare.
   * 
   * *NOTE* all comparitors work on the endname and not the full field name. This is
   * to allow items with different IDs and cache names to be compared.
   * @param comparisonItem an item to compare against this one
   * @return true if the contents indicated by the comparitors are equal
   */
  public boolean isEqual( Item comparisonItem )
  {
    // First, if the comparitors are different then fail at this point
    for( String comparitor : _comparitors )
    {
      if( !( comparisonItem.getComparitors().contains(comparitor)))
      {
        return false;
      }
    }
    
    // Now check the contents of the comparitor fields against the comparisonItem
    for( String comparitor : _comparitors )
    {
      if( !( this.aspectMatches(comparitor, comparisonItem.getContents().get(comparitor))))
      {
        return false;
      }
    }
    
    return true;
  }
  
  /**
   * Helper method for setting the comparitor hash using a string.
   * @param hash value to store as comparitor hash
   */
  public void setComparitorHash( String hash )
  {
    _contents.put(StandardAspects.HASH_COMPARITOR, hash );
  }
  
  /**
   * Helper method for setting the comparitor hash using a byte array.
   * @param bytes byte array to store as comparitor hash
   */
  public void setComparitorHash( byte[] bytes )
  {
    _contents.put(StandardAspects.HASH_COMPARITOR, new String(bytes));
  }

  /**
   * Accessor method for string version of comparitor hash value.
   * @return the string value stored for the comparitor hash
   * @throws NoSuchAspectException if the comparitor hash aspect does not exist for this item
   */
  public String getComparitorHashString() throws NoSuchAspectException
  {
    if( !_contents.contains(StandardAspects.HASH_COMPARITOR))
    {
      throw new NoSuchAspectException( "Item does not contain hash comparitor aspect.");
    }
    
    return (String)_contents.get(StandardAspects.HASH_COMPARITOR);
  }

  /**
   * Accessor method for byte version of comparitor hash value.
   * @return the byte array containing the comparitor hash
   * @throws NoSuchAspectException if the comparitor hash aspect does not exist for this item
   */
  public byte[] getComparitorHashBytes() throws NoSuchAspectException
  {
    if( !_contents.contains(StandardAspects.HASH_COMPARITOR))
    {
      throw new NoSuchAspectException( "Item does not contain hash comperitor aspect.");
    }
    
    return ((String)_contents.get(StandardAspects.HASH_COMPARITOR)).getBytes();    
  }
  
  /**
   * Helper mutator for the Hash class name. This is a class conforming to the IHash interface that was used to
   * construct the hash value stored in the HASH_COMPARITOR aspect.
   * @param className fully qualified class name of the hashing algorithm implementation
   */
  public void setHashClass( String className )
  {
    _contents.put( StandardAspects.HASH_CLASS, className );
  }

  /**
   * Helper accessor for the Hash Class aspect.
   * @return the fully qualified class name for the Hashing algorithm class
   * @throws NoSuchAspectException if the HASH_CLASS aspect does not exist for this item
   */
  public String getHashClass() throws NoSuchAspectException
  {
    if( !_contents.contains(StandardAspects.HASH_CLASS))
    {
      throw new NoSuchAspectException( "Item does not contain hash comperitor aspect.");
    }
    
    return (String)_contents.get(StandardAspects.HASH_CLASS);        
  }
  
  /**
   * Aspect endname check. This method searches the keys of the contents for 
   * instances of the endname and returns true when it finds one.
   * @param endName endname to check for
   * @return true if the item has an Aspect that ends with the endname, false if it does not
   */
  public boolean hasAspect( String endName )
  {
    for( String key : _contents.keySet())
    {
      if( key.endsWith("." + endName))
      {
        return true;
      }
    }
    
    return false;
  }
  
  /**
   * Validity check on Item. All items *must* have the mandatory fields that
   * define an Item in the system. This method checks the existence of these fields.
   * @return true if the Item contains the fields that are mandatory.
   */
  public boolean isValidItem()
  {
    return this.hasAspect( StandardAspects.SOURCE ) &&
           this.hasAspect( StandardAspects.GENERATOR )  &&
           this.hasAspect( StandardAspects.CREATED );
  }
  
  
  @Override
	public String toString() 
  {
		Hashtable<String, Object> aspects = this.getContents();
	  	String ret = "Item with " + aspects.size() + " aspects\n";
		
		for (String aspectKey : aspects.keySet()) 
		{
			ret += "Aspect: " + aspectKey + " = " + aspects.get(aspectKey) + "\n";
		} 
		 
		return ret;
	}
}
