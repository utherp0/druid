package org.druid.generation;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

import org.druid.currency.Item;
import org.druid.currency.StandardAspects;
import org.druid.utils.DateTimeConversion;
import org.druid.utils.URLUtils;

/**
 * Static class containing Aspect enrichment methods for specific aspect types.
 * This allows for, for instance, Aspect enhancement for file based sources, URL sources
 * etc.
 * @author Ian Lawson (<a href="mailto:ian.lawson@redhat.com">ian.lawson@redhat.com</a>).
 *
 */
public final class AspectEnrichment
{
  // Private constructor.
  private AspectEnrichment()
  {
    
  }
  
  public static Item urlEnrichment( Item input, HttpURLConnection connection ) throws IOException
  {
    connection.connect();
    
    // Assumption is that this method is called with a valid URL
    // TODO check that the URL is actually valid
    try
    {
      input.addString( StandardAspects.URL_RESPONSE_CODE, Integer.toString( connection.getResponseCode()));

      System.out.println( "Response code: " + connection.getResponseCode());
      if( URLUtils.validResponse(connection.getResponseCode()))
      {
        input.addString( StandardAspects.URL_URL, connection.getURL().toString());
        input.addString( StandardAspects.URL_HOST, connection.getURL().getHost());
        input.addString( StandardAspects.URL_PORT, Integer.toString(connection.getURL().getPort()));
        input.addString( StandardAspects.URL_PATH, connection.getURL().getPath());
        input.addString( StandardAspects.URL_MIMETYPE, connection.getContentType());
        input.addString( StandardAspects.URL_CONTENT_LENGTH, Long.toString(connection.getContentLengthLong()));
        input.addString( StandardAspects.URL_MODIFIED_UTC, Long.toString(connection.getLastModified()));
        input.addString( StandardAspects.URL_MODIFIED_TEXT, DateTimeConversion.getTextualDataTime(connection.getLastModified()));        
      }
      else
      {
        input.addString( StandardAspects.URL_URL, "Invalid URL");
      }      
    }
    catch( Exception exc )
    {
    }
    
    return input;
  }

  public static Item fileEnrichment( Item input, File targetFile )
  {
    // Add Aspects for file specific information.
    try
    {
      input.addString(StandardAspects.FILE_FILENAME, targetFile.getCanonicalPath());
      input.addString(StandardAspects.FILE_SIZE_BYTES, Long.toString(targetFile.getTotalSpace()));
      input.addString(StandardAspects.FILE_MODIFIED_UTC, Long.toString(targetFile.lastModified()));
      input.addString(StandardAspects.FILE_MODIFIED_TEXT, DateTimeConversion.getTextualDataTime(targetFile.lastModified()));
    }
    catch( Exception exc )
    {
      input.addString(StandardAspects.FILE_FILENAME, "Invalid File");
    }
    
    return input;
  }
}
