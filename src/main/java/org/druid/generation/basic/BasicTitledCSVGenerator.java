package org.druid.generation.basic;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import org.druid.currency.Item;
import org.druid.exceptions.GenerationException;
import org.druid.generation.Generator;
import org.druid.generation.IGenerator;

/**
 * This generator produces multiple Items from a singular source of titled CSV. A titled
 * CSV is a file that contains the column headings as part of the file itself (the first
 * line).
 * @author Ian Lawson <a href="mailto:ian.lawson@redhat.com">ian.lawson@redhat.com</a>
 *
 */
public class BasicTitledCSVGenerator extends Generator implements IGenerator
{
  private String _separator = ",";

  @Override
  /**
   * This method reads the contents of the InputStream into a String and then calls the appropriate
   * generate method with the String.
   * @param inputStream inputstream to process
   * @return list of discovered items from the inputstream
   * @throws GenerationException if generation fails
   */
  public List<Item> generate(InputStream inputStream, String source ) throws GenerationException
  {
    StringBuffer fileContents = new StringBuffer();
    
    int content = 0;
    
    try
    {
      while((content = inputStream.read()) != -1 )
      {
        fileContents.append((char)content);
      }
      
      return this.generate( fileContents.toString(), source );
    }
    catch( Exception exc )
    {
      exc.printStackTrace();

      throw new GenerationException( "Failed to generate from InputStream in BasicTitledCSVGenerator due to " + exc.getMessage());      
    }
  }

  @Override
  /**
   * This method generates a set of items from a target file.
   * @param inputFile Titled CSV file to read and process.
   * @return list of constructed Items from the file
   * @throws GenerationException if it fails to generate Items
   */
  public List<Item> generate(File inputFile, String source ) throws GenerationException
  {
    try
    {
      // Simple checks
      if( inputFile == null ) throw new GenerationException( "File is null at BasicTitledCSVGenerator");
      
      if( !inputFile.exists()) throw new GenerationException( "No such file " + inputFile.getCanonicalPath() + " in BasicTitledCSVGenerator");
      
      if( inputFile.isDirectory()) throw new GenerationException ( "Target file is directory " + inputFile.getCanonicalPath() + " in BasicTitledCSVGenerator");

      // Otherwise
      return this.generate( new FileInputStream( inputFile ), source );      
    }
    catch( Exception exc )
    {
      exc.printStackTrace();

      throw new GenerationException( "Failed to generate item in BasicTitledCSVGenerator due to " + exc.getMessage());
    }
  }

  @Override
  /**
   * This method generates Items from a provided String.
   * @param inputString string version of the Titled CSV file contents
   * @return list of constructed Items from the file
   * @throws GenerationException if it fails to generate Items.
   */
  public List<Item> generate(String inputString, String source ) throws GenerationException
  {
    ArrayList<Item> workingItems = new ArrayList<Item>();
    
    // Split the string using linebreaks
    String[] lines = inputString.split("\\r?\\n");
    
    // First string/line is the column definition
    String[] headings = BasicTitledCSVGenerator.split(lines[0], _separator );
    
    List<String> headingsList = Arrays.asList(headings);
    
    // Now for each remaining line we need to construct the item based on the column
    // headings and value.
    for( int line = 1; line < lines.length; line++ )
    {
      String[] values = BasicTitledCSVGenerator.split( lines[line], _separator);
      
      // Mismatch check
      if( headings.length != values.length )
      {
        // Temporary output - bad Uth
        System.out.println( "Line " + line + " failed to generate due to mismatch (expected " + headings.length + " received " + values.length );
      }
      else
      {
        Hashtable<String,Object> contents = new Hashtable<String,Object>();
        
        for( int loop = 0; loop < headings.length; loop++ )
        {
          contents.put(headings[loop], (Object)values[loop] );
        }
        
        Item item = new Item( contents, headingsList );
        
        // Comparitor is maximal
        item.setComparitors(headingsList);
        item = this.setMandatoryAspects(item, source);
        
        workingItems.add(item);
      }
    }
    
    return workingItems;
  }

  @Override
  public List<Item> generate(URL url, String source ) throws GenerationException
  {
    // Not currently supported
    return null;
  }

  @Override
  /**
   * Method for dependancy injection. In the case of Titled CSV this is the separator
   * to use for determination of tokens within the file.
   * @param name parameter nanme, must be 'separator' for this class
   * @value separator value to store
   * @throws GenerationException if the parameter name is not 'separator'
   */
  public void setParameter(String name, String value) throws GenerationException
  {
    if( !( name.equals("separator")))
    {
      throw new GenerationException( "Only parameter supported by BasicTitledCSVGenerator is separator");
    }
    
    _separator = value;
  }

  /**
   * Helper method for splitting a string appropriately (handles embedded separators in speechmarks).
   * @param original original string to split
   * @param separator separator to use for splitting
   * @return array of components after intelligent splitting
   */
  private static String[] split( String original, String separator )
  {
    int position = 0;
    String[] output = new String[4096];

    char separatorChar = separator.charAt(0);

    // Handle pipe case
    if( separator.equals( "\\|")) separatorChar = '|';

    boolean withinSpeechMarks = false;
    StringBuilder temporary = new StringBuilder();

    for( int loop = 0; loop < original.length();loop++ )
    {
      if( original.charAt(loop) == separatorChar && !withinSpeechMarks )
      {
        output[position] = temporary.toString();
        position++;
        temporary = new StringBuilder();
      }
      else if( original.charAt(loop) == '"' && !withinSpeechMarks )
      {
        temporary.append(original.charAt(loop));
        withinSpeechMarks = true;
      }
      else if( original.charAt(loop) == '"' && withinSpeechMarks )
      {
        temporary.append(original.charAt(loop));
        withinSpeechMarks = false;
      }
      else
      {
        temporary.append(original.charAt(loop));
      }
    }

    output[position] = temporary.toString();

    String[] concatenatedOutput = new String[position + 1];

    for( int loop = 0; loop <= position; loop++ )
    {
      concatenatedOutput[loop] = output[loop];
    }
    
    return concatenatedOutput;
  }
  
}
