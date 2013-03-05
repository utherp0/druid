package org.druid.exceptions;

/**
 * This class provides an exception for when a connector action fails.
 * @author Ian Lawson (<a href="mailto:ian.lawson@redhat.com">ian.lawson@redhat.com</a>).
 */
public class ConnectorException extends Exception
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Standard no parameter constructor - calls the super() method from {@link java.lang.Exception java.lang.Exception}.
   */
  public ConnectorException()
  {
    super();
  }

  /**
   * Standard message parameter constructor - calls the super() method from {@link java.lang.Exception java.lang.Exception}.
   * @param message message to add to the exception object
   */
  public ConnectorException( String message )
  {
    super( message );
  } 
  
  public ConnectorException(Exception cause) 
  {
	  super(cause); 
  }
}