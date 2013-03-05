package org.druid.utils;

/**
 * Utilities class for text handling, such as removing extraneous spaces, line feeds, 
 * token cleansing and the like.
 * @author Ian Lawson <a href="mailto:ian.lawson@redhat.com">ian.lawson@redhat.com</a>
 *
 */
public class TextUtils
{
  public TextUtils()
  {
    // TODO - Some of the utils may require the persistence of an object
  }
  
  /**
   * Simplistic strip approach for now - removes all non-displayable characters.
   * @param input string to clean
   * @return cleansed string
   */
  public static String clean( String input )
  {
    char[] inputChars = input.toCharArray();

    StringBuilder reducedString = new StringBuilder();

    for( char inputChar : inputChars )
    {
      if( inputChar != '\'' && inputChar != '\"')
      {
        reducedString.append( inputChar );
      }
    }

    return reducedString.toString().replaceAll("\\p{Cntrl}", " ");
  }

  /**
   * Simplistic method for stripping unnecessary spaces from a textual content.
   * @param input pre-stripped input
   * @return stripped output
   */
  public static String stripExtraneousSpaces( String input )
  {
    String[] tokens = input.split( " " );

    StringBuilder output = null;

    for( int loop = 0; loop < tokens.length; loop++ )
    {
      if( tokens[loop].length() != 0 )
      {
        if( output == null )
        {
          output = new StringBuilder( tokens[loop] );
        }
        else
        {
          output.append( " " + tokens[loop]);
        }
      }
    }

    return( output == null ? null : output.toString());
  }  
}
