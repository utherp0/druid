package org.druid.tests;

import java.util.List;

import org.druid.surfacing.ISurfacer;
import org.druid.surfacing.SurfacerFactory;

/**
 * This class provides tests for flexing the data dictionary aspects of a BasicFilePersisted DRUID cache.
 * @author Ian Lawson <a href="ian.lawson@redhat.com">ian.lawson@redhat.com</a>
 *
 */
public class BasicFileSurfacerDataDictionaryTest
{
  public static void main( String args[] )
  {
    if( args.length != 1 )
    {
      System.out.println( "Usage: java BasicFileSurfacerDataDictionaryTest cacheLocationDirectory" );
      System.exit(0);
    }
    
    new BasicFileSurfacerDataDictionaryTest( args[0]);
  }
  
  public BasicFileSurfacerDataDictionaryTest( String targetLocation )
  {
    try
    {
      ISurfacer surfacer = SurfacerFactory.getSurfacer("org.druid.surfacing.basicFiles.BasicFileSurfacer");
      
      long surfacerStart = System.currentTimeMillis();
      surfacer.setLocation(targetLocation);
      surfacer.synchroniseCache();
      long surfacerEnd = System.currentTimeMillis();
      
      System.out.println( "Surfacer cached in " + ( surfacerEnd - surfacerStart ) + "ms.");
      
      List<String> dataDictionary = surfacer.surfaceDataDictionary();
      
      System.out.println( "Discovered " + dataDictionary.size() + "unique data dictionary entries.");
      
      for( String entry : dataDictionary )
      {
        System.out.println( "   " + entry );
      }
    }
    catch( Exception exc )
    {
      System.out.println( "Test failed due to " + exc.toString());
      exc.printStackTrace();
    }
  }

}
