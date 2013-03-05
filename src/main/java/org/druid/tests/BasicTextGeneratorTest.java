package org.druid.tests;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.druid.currency.Item;
import org.druid.generation.GeneratorFactory;
import org.druid.generation.IGenerator;

public class BasicTextGeneratorTest
{
  public static void main( String[] args )
  {
    if( args.length != 2 )
    {
      System.out.println( "Usage: java BasicTextGenerationTest type(url|file|string) source");
      System.exit(0);
    }

    String command = args[0].toLowerCase();
    
    if( "urlfilestring".indexOf( command ) == -1 )
    {
      System.out.println( "Command must be url, file or string");
      System.exit(0);
    }
    
    new BasicTextGeneratorTest( command, args[1] );
  }
  
  public BasicTextGeneratorTest( String command, String source )
  {
    List<Item> results = null;
    
    try
    {
      IGenerator generator = GeneratorFactory.getGenerator("org.druid.generation.basic.BasicTextGenerator");
      
      long generateStart = System.currentTimeMillis();  
    
      switch( command )
      {
        case "string" : results = generator.generate(source, "string");
                        break;
                        
        case "file"   : results = generator.generate( new File( source ), source);
                        break;
                        
        case "url"    : results = generator.generate( new URL( source ), source );
                        break;
                        
      }
      
      long generateEnd = System.currentTimeMillis();
      
      System.out.println( "[" + this.getClass().getSimpleName() + "] Completed generate in " + ( generateEnd - generateStart ) + "ms.");
      System.out.println( "[" + this.getClass().getSimpleName() + "] Generated " + results.size() + " items.");

      for( Item item : results )
      {
        for( String key : item.getContents().keySet())
        {
          System.out.println( key + ":" + (String)item.getContents().get(key));
        }
        
        List<String> comparitors = item.getComparitors();
        
        System.out.print( "Comparitors: ");
        for( String comparitor : comparitors )
        {
          System.out.print( comparitor + " " );
        }
      }
      
      System.out.println( "" );
    }
    catch( Exception exc )
    {
      System.out.println( "Generation failure due to " + exc.toString());
      exc.printStackTrace();
    }      
  }
}
