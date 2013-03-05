package org.druid.exceptions;

/**
 * This class provides an exception for when a hashing action fails.
 * @author Ian Lawson (<a href="mailto:ian.lawson@redhat.com">ian.lawson@redhat.com</a>).
 */
public class HashingException extends Exception
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Standard no parameter constructor - calls the super() method from {@link java.lang.Exception java.lang.Exception}.
   */
  public HashingException()
  {
    super();
  }

  /**
   * Standard message parameter constructor - calls the super() method from {@link java.lang.Exception java.lang.Exception}.
   * @param message message to add to the exception object
   */
  public HashingException( String message )
  {
    super( message );
  } 
  
  public HashingException(Exception cause) 
  {
	  super(cause); 
  }
}