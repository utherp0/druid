package org.druid.surfacing;

/**
 * This is the static factory for generating Persisters using the interface.
 * @author Ian Lawson (<a href="mailto:ian.lawson@redhat.com">ian.lawson@redhat.com</a>).
 */
public class SurfacerFactory
{
  /**
   * Private constructor.
   */
  private SurfacerFactory()
  {

  }

  @SuppressWarnings("unchecked")
  /**
   * Static factory for generating Surfacers
   * @param className class name of Surfacer to create (must conform to {@link org.druid.surfacing.ISurfacer ISurfacer} interface. 
   * @return an instantiated object for the Surfacer
   * @throws ClassCastException if the target class cannot be cast to ISurfacer
   * @throws ClassNotFoundException if the target class is not in the ClassLoader classpath
   * @throws InstantiationException if the instantiation fails
   * @throws IllegalAccessException if the class is unreachable within the ClassLoader
   */
  public static  <T extends ISurfacer> T getSurfacer( String className ) throws ClassCastException,ClassNotFoundException, InstantiationException, IllegalAccessException
  {
    return (T)Class.forName(className).newInstance();
  }
}
