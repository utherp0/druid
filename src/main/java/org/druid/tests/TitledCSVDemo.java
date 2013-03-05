package org.druid.tests;

import java.io.File;
import java.util.List;

import org.druid.currency.Item;
import org.druid.currency.utils.ItemNameTools;
import org.druid.currency.utils.ItemQualifier;
import org.druid.generation.GeneratorFactory;
import org.druid.generation.IGenerator;
import org.druid.persistence.IPersister;
import org.druid.persistence.PersisterFactory;

/**
 * The TitledCSVDemo provides an end to end demo of converting Titled CSV into an Item set, then persisting the aspects
 * of the Item set to a Basic File persistence mechanism.
 * @author Ian Lawson <a href="mailto:ian.lawson@redhat.com">ian.lawson@redhat.com</a>
 *
 */
public class TitledCSVDemo
{
  public static void main( String[] args )
  {
    if( args.length != 3 )
    {
      System.out.println( "Usage: java TitledCSVDemo persistenceRoot inputCSV cacheName");
      System.exit(0);
    }
    
    new TitledCSVDemo( args[0], args[1], args[2]);
  }
  
  public TitledCSVDemo( String location, String inputFile, String cacheName )
  {
    // Should be simple calls wrapped in a try/catch and show the simplicity of the API
    try
    {
      // Get a generator
      IGenerator generator = GeneratorFactory.getGenerator("org.druid.generation.basic.BasicTitledCSVGenerator");
      
      // Convert the stream to Items
      long generateStart = System.currentTimeMillis();
      List<Item> items = generator.generate( new File( inputFile ), inputFile );
      long generateEnd = System.currentTimeMillis();
      
      // Inefficiently persist each item
      IPersister persister = PersisterFactory.getPersister("org.druid.persistence.basicFiles.BasicFilePersister");
      persister.setLocation( location );
      
      long persistStart = System.currentTimeMillis();
      
      int persistCount = 0;
      
      // For each item fully qualify it and persist it
      for( Item item : items )
      {
        // Generate a UUID for the item (with no UTC)
        String uuid = ItemNameTools.generateUUID(false);
        
        // Fully qualify the item
        Item qualifiedItem = ItemQualifier.qualify(item, cacheName, uuid);
        
        // Persist the item
        persister.setParameter("uuid", uuid);
        persister.persistItem(qualifiedItem, false);
        
        persistCount++;
      }
      
      long persistEnd = System.currentTimeMillis();
      
      // Report
      System.out.println( "Success - generated items in " + ( generateEnd - generateStart ) + "ms." );
      System.out.println( "Success - persisted " + persistCount + " items in " + ( persistEnd - persistStart ) + "ms.");
    }
    catch( Exception exc )
    {
      System.out.println( "Demonstrator failed due to " + exc.toString());
      exc.printStackTrace();
    }
  }
}
