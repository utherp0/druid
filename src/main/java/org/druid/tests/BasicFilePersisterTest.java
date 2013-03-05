package org.druid.tests;

import org.druid.currency.Item;
import org.druid.currency.utils.ItemQualifier;
import org.druid.persistence.IPersister;
import org.druid.persistence.PersisterFactory;

public class BasicFilePersisterTest
{
  public static void main( String[] args )
  {
    if( args.length != 1 )
    {
      System.out.println( "Usage: java BasicFilePersisterTest hostDirectory");
      System.exit(0);
    }
    
    new BasicFilePersisterTest( args[0]);
  }
  
  public BasicFilePersisterTest( String outputRoot )
  {
    // Create an item
    Item testItem = new Item();
    
    testItem.addString( "header", "http://");
    testItem.addString( "server", "localhost");
    testItem.addString( "port", "8080" );
    testItem.addString( "response", "404 Not Found" );
    
    String cacheName = "myDataCache";
    String uuid = "1";
    
    // Qualify the item
    Item qualifiedItem = ItemQualifier.qualify(testItem,cacheName,uuid );
    
    // Persister setup
    IPersister persister = null;
    
    try
    {
      persister = PersisterFactory.getPersister("org.druid.persistence.basicFiles.BasicFilePersister");
    }
    catch( Exception exc )
    {
      System.out.println( "Persister setup failed due to " + exc.toString());
      System.exit(0);
    }
          
    // Now do a persistence test
    try
    {
      persister.setLocation(outputRoot);
      persister.setParameter("uuid", uuid);
      
      persister.persistItem(qualifiedItem, false);
      
      System.out.println( "Item successfully persisted.");
    }
    catch( Exception exc )
    {
      System.out.println( "Test failed due to " + exc.toString());
    }
    
    // Now test the functionality of detection of existing file with overwrite disabled
    try
    {
      persister.persistItem(qualifiedItem, false);
      
      System.out.println( "Test failure, item persisted to existing item when overwrite disabled.");
      System.exit(0);
    }
    catch( Exception exc )
    {
      System.out.println( "Test successful, item failed to persist when overwrite disabled and item already exists.");
    }
  }
}
