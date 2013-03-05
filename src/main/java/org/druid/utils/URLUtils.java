package org.druid.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class URLUtils
{
  /**
   * Class containing helper methods for URL usage.
   * @author Ian Lawson <a href="mailto:ian.lawson@redhat.com">ian.lawson@redhat.com</a>
   */
  public URLUtils()
  {
    // TODO - some URL methods will benefit from an object that persists the URL
  }
  
  /**
   * Exists method for URL level checking.
   * @param url url to check for existence
   * @return true if the URL exists and returns a valid 2xx response code, or false if not
   * @throws IOException if an IOException occurs while opening the URL
   */
  public static boolean exists( URL url ) throws IOException
  {
    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
    
    return URLUtils.exists(connection);
  }

  /**
   * Exists method for URL connection level checking.
   * @param connection pre-created connection to check
   * @return true if the connection returns a 2xx response code, false otherwise
   * @throws IOException
   */
  public static boolean exists( HttpURLConnection connection ) throws IOException
  {
    connection.connect();
    
    boolean isValid = URLUtils.validResponse(connection.getResponseCode());
    
    connection.disconnect();
    
    return isValid;
  }

  /**
   * Simple 2xx response code check.
   * @param response code to check
   * @return true if it is in the 2xx range
   */
  public static boolean validResponse( int response )
  {
    // TODO - other responses could be valid, check
    return ( response >= 200 && response <= 299 );
  }
}
