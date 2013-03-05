package org.druid.tests;

import java.util.Map;

import org.druid.connectors.utils.Lucene3Connector;

/**
 * Simple test for flexing the LUCENE3 connector methods.
 * @author ian Lawson <a href="mailto:ian.lawson@redhat.com">ian.lawson@redhat.com</a>
 */
public class LUCENE3ConnectorTest
{
  public static void main( String[] args )
  {
    if( args.length != 3 )
    {
      System.out.println( "Usage: java LUCENE3ConnectorTest action(new|append|delete|report) location '.elu file drop'");
      System.exit(0);
    }
    
    if( !("new".equals( args[0] ) ) && 
        !("append".equals( args[0] ) ) &&
        !("delete".equals( args[0] ) ) &&
        !("report".equals( args[0] ) ) )
    {
      System.out.println( "Unknown action (must be 'new' | 'append' | 'delete' | 'report')");
      System.exit(0);
    }
    
    new LUCENE3ConnectorTest( args[0], args[1], args[2] );
  }
  
  public LUCENE3ConnectorTest( String action, String location, String fileLocation )
  {
    Lucene3Connector handle = new Lucene3Connector();
    
    // Simple tests first
    // If delete attempt to delete then exit
    if( "delete".equals( action ))
    {
      try
      {
        handle.deleteIndex(location);
        
        System.out.println( "Index at location " + location + " deleted successfully.");
        System.exit(0);
      }
      catch( Exception exc )
      {
        System.out.println( "Test failed on index delete due to " + exc.toString());
        exc.printStackTrace();
      }
    }
    
    // If report then generate the report aspects and then exit
    if( "report".equals( action ))
    {
      try
      {
        Map<String,String> report = handle.report(location);
        
        System.out.println( "Report completed:");
        
        for( String key : report.keySet() )
        {
          System.out.println( "  " + key + ":" + report.get(key));
        }
      }
      catch( Exception exc )
      {
        System.out.println( "Test failed due to " + exc.toString());
      }
    }
    
    // Test 1 - if 'new' create the index
    if( "new".equals( action ))
    {
      try
      {
        handle.createIndex( location );
        
        System.out.println( "LUCENE index created at " + location );
      }
      catch( Exception exc )
      {
        System.out.println( "Test failed due to " + exc.toString());
        exc.printStackTrace();
      }
    }
    
    // Now convert and store the items into the Persister
    
  }
}
