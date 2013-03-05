package org.druid.tests;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class URLTest
{
  private String _alias = "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB; rv:1.9.2.8) Gecko/20100722 Firefox/3.6.8";
    
  public static void main( String[] args )
  {
    if( args.length != 1 )
    {
      System.out.println( "Usage: java URLTest targetURL");
      System.exit(0);
    }
    
    new URLTest( args[0] );
  }
  
  public URLTest( String targetURL )
  {
    try
    {
      URL url = new URL( targetURL );
      
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      
      connection.setRequestProperty( "User-Agent", _alias );
      connection.connect();

      int responseCode = connection.getResponseCode();
      
      if( !( responseCode >= 200 && responseCode <= 299 ) )
      {
        System.out.println( "Invalid page request, response " + responseCode );
        System.exit(0);
      }
      
      System.out.println( "Now: " + System.currentTimeMillis());
      System.out.println( "URL: " + connection.getURL().toString());
      System.out.println( "Host: " + connection.getURL().getHost());
      System.out.println( "Port: " + connection.getURL().getPort());
      System.out.println( "Path: " + connection.getURL().getPath());
      System.out.println( "Reported Mimetype: " + connection.getContentType());
      System.out.println( "Reported Timeout: " + connection.getDate());
      System.out.println( "Reported Encoding: " + connection.getContentEncoding());
      System.out.println( "Reported Length: " + connection.getContentLength());
      System.out.println( "Reported Date: " + connection.getDate());
      System.out.println( "Reported Expiration: " + connection.getExpiration());
      System.out.println( "Reported Last Modified: " + connection.getLastModified());
      
      Map<String, List<String>> headers = connection.getHeaderFields();
      
      for( String key : headers.keySet() )
      {
        System.out.println( "  Header Key: " + key );
      }
      
      connection.disconnect();      
    }
    catch( Exception exc )
    {
      System.out.println( "[" + this.getClass().getSimpleName() + "] failed due to " + exc.toString());
      exc.printStackTrace();
    }
  }
}
