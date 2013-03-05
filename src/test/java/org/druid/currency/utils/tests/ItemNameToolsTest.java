package org.druid.currency.utils.tests;

import static org.junit.Assert.*;

import org.druid.currency.utils.ItemNameTools;
import org.junit.Ignore;
import org.junit.Test;

public class ItemNameToolsTest
{
  private static final String EXPECTED_UUID = "1111-2222-3333";  
  private static final String VALID_NAME = "myDataCache.1111-2222-3333.myField";
  @Test 
  public void testGetUUID() 
  {
    String testUUID = null;
    
    try
    {
      testUUID = ItemNameTools.getUUID(VALID_NAME);
      
      assertTrue( testUUID.equals( EXPECTED_UUID ) );      
    }
    catch( Exception exc )
    {
      fail("Exception occured : " + exc.toString());
    }
  }

  @Test 
  @Ignore
  public void testExtractUUIDEmbeddedUTC()
  {
  }

  @Test
  @Ignore
  public void testGenerateUUID()
  {
  }

  @Test
  @Ignore
  public void testGetCacheName()
  {
  }

  @Test
  @Ignore
  public void testGetFieldComponents()
  {
  }

}
