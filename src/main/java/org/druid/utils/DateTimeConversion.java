package org.druid.utils;

import java.text.DateFormat;

public final class DateTimeConversion
{
  /**
   * Private Constructor to force static method usage.
   */
  private DateTimeConversion()
  {    
  }
  
  /**
   * Generate consistent textual date/time for Druid.
   * @param utc millisecond UTC time to convert
   * @return consistently formatted textual date for use within Druid
   */
  public static String getTextualDataTime( long utc )
  {
    DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

    return dateFormat.format(utc);    
  }

  /**
   * Shortcut helper to get a consistent textual date/time for current system time.
   * @return consistently formatted textual current system date for use within Druid
   */
  public static String getTextualCurrentDateTime()
  {
    return DateTimeConversion.getTextualDataTime(System.currentTimeMillis());
  }
}
