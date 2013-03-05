package org.druid.exceptions;

/**
 * This class provides an exception for when a data format conversion failure occurs.
 * @author Ian Lawson (<a href="mailto:ian.lawson@redhat.com">ian.lawson@redhat.com</a>).
 */
public class PersistenceException extends Exception
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Standard no parameter constructor - calls the super() method from {@link java.lang.Exception java.lang.Exception}.
   */
  public PersistenceException()
  {
    super();
  }

  /**
   * Standard message parameter constructor - calls the super() method from {@link java.lang.Exception java.lang.Exception}.
   * @param message message to add to the exception object
   */
  public PersistenceException( String message )
  {
    super( message );
  }

  public PersistenceException(String message, Throwable cause) {
    super(message, cause);
  }
}
