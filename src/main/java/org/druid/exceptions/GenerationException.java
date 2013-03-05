package org.druid.exceptions;

/**
 * This class provides an exception for when Item generation fails.
 * @author Ian Lawson (<a href="mailto:ian.lawson@redhat.com">ian.lawson@redhat.com</a>).
 */
public class GenerationException extends Exception
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Standard no parameter constructor - calls the super() method from {@link java.lang.Exception java.lang.Exception}.
   */
  public GenerationException()
  {
    super();
  }

  /**
   * Standard message parameter constructor - calls the super() method from {@link java.lang.Exception java.lang.Exception}.
   * @param message message to add to the exception object
   */
  public GenerationException( String message )
  {
    super( message );
  } 
  
  public GenerationException(Exception cause) {
	  super(cause); 
  }

}