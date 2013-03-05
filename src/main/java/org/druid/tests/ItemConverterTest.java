package org.druid.tests;

import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable; 
import org.druid.currency.Item;
import org.druid.persistence.lucene.ItemConverter;

public class ItemConverterTest
{
  public static void main( String[] args )
  {
    new ItemConverterTest();
  }
  
  public ItemConverterTest()
  {
    Item item = new Item();
    
    // Add some dummy data
    try
    {
      item.addString("field1", "test field 1", false);
      item.addString("field2", "test field 2", false);
      item.addString("field3", "test field 3", false);
      item.addString("field4", "test field 4", false);
      item.addString("field5", "test field 5", false);
      item.addString("field6", "test field 6", false);
      
      Document testDocument = ItemConverter.convert(item);
      
      List<Fieldable> fields = testDocument.getFields();
      
      System.out.println( "Found " + fields.size() + " fields in test document.");
      
      for( Fieldable field : fields )
      {
        System.out.println( "  " + field.name() + ":" + field.stringValue());
      }
    }
    catch( Exception exc )
    {
      System.out.println( "Test failed due to " + exc.toString());
      
      exc.printStackTrace();
    }    
  }
}
