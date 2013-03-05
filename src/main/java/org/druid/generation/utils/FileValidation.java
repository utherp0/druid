package org.druid.generation.utils;

import java.io.File;
import java.io.IOException;

import org.druid.exceptions.GenerationException;

public class FileValidation
{
  // Private Constructor (static methods)
  private FileValidation()
  {
    
  }
  
  /**
   * Standard checks for any file to be processed by Generators. This checks the file is not null,
   * the file exists and the file is not a directory.
   * @param target pre-created file for checking
   * @throws GenerationException if any of the standard check clauses fail
   * @throws IOException if IO operations on file fail
   */
  public static void standardChecks( File target ) throws GenerationException, IOException
  {
    if( target == null )
    {
      throw new GenerationException( "File is null at generation.");
    }
    
    if( !( target.exists()))
    {
      throw new GenerationException( "No such file (" + target.getCanonicalPath() + ") at Generation.");
    }
      
    if( target.isDirectory())
    {
      throw new GenerationException( "File is incorrect context (directory) [" + target.getCanonicalPath() + "]");
    }
  }
}
