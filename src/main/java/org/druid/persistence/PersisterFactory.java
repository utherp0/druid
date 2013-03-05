package org.druid.persistence;

/**
 * This is the static factory for generating Persisters using the interface.
 * @author Ian Lawson (<a href="mailto:ian.lawson@redhat.com">ian.lawson@redhat.com</a>).
 */
public class PersisterFactory
{
  /**
   * Private constructor.
   */
  private PersisterFactory()
  {

  }

  @SuppressWarnings("unchecked")
  /**
   * Static factory for generating Persisters.
   * @param className class name of Persister to create (must conform to {@link org.druid.persistence.IPersister IPersister} interface. 
   * @return an instantiated object for the Persister
   * @throws ClassCastException if the target class cannot be cast to IPersister
   * @throws ClassNotFoundException if the target class is not in the ClassLoader classpath
   * @throws InstantiationException if the instantiation fails
   * @throws IllegalAccessException if the class is unreachable within the ClassLoader
   */
  public static  <T extends IPersister> T getPersister( String className ) throws ClassCastException,ClassNotFoundException, InstantiationException, IllegalAccessException
  {
    return (T)Class.forName(className).newInstance();
  }
}
