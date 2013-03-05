package org.druid.generation;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This is the static factory for generating Persisters using the interface.
 * @author Ian Lawson (<a href="mailto:ian.lawson@redhat.com">ian.lawson@redhat.com</a>).
 */
public class GeneratorFactory
{
  private IGenerator defaultGenerator; 
  private HashSet<Class<? extends IGenerator>> registeredGenerators = new HashSet<Class<? extends IGenerator>>();
  private HashMap<String, Class<? extends IGenerator>> fileAssociations = new HashMap<String, Class<? extends IGenerator>>(); 
       
  public void registerGenerator(Class<? extends IGenerator> classGenerator) {
	  registeredGenerators.add(classGenerator);
  }    
  
  public void registerGeneratorFiletype(Class<? extends IGenerator> classGenerator, String filetype) {
	  registerGenerator(classGenerator);
	  
	  this.fileAssociations.put(filetype, classGenerator); 
  }
  
  @SuppressWarnings("unchecked")
  /**
   * Static factory for generating Generators.
   * @param className class name of Generator to create (must conform to {@link org.druid.generation.IGenerator IGenerator} interface. 
   * @return an instantiated object for the Generator
   * @throws ClassCastException if the target class cannot be cast to IGenerator
   * @throws ClassNotFoundException if the target class is not in the ClassLoader classpath
   * @throws InstantiationException if the instantiation fails
   * @throws IllegalAccessException if the class is unreachable within the ClassLoader
   */
  public static <T extends IGenerator> T getGenerator( String className ) throws ClassCastException,ClassNotFoundException, InstantiationException, IllegalAccessException
  {
    return (T)Class.forName(className).newInstance();
  }
  
  // TODO: Move me to somewhere far more sensible
  private String getExtension(File f) {
	  int dot = f.getName().lastIndexOf(".");
	  String extension = f.getName().substring(dot +1, f.getName().length()); 
 	  
	  System.out.println(f.getName() + " = " + extension); 
	  
	  return extension;
  }

  // TODO: Move me to somewhere far more sensible
  public IGenerator getGeneratorForFile(File f) {   
	  String extension = getExtension(f);
	 
	  if (fileAssociations.containsKey(extension)) {
		  try { 
			  return fileAssociations.get(extension).newInstance();
		  } catch (Exception e) {
			  e.printStackTrace();
			  return defaultGenerator; 
		  } 
	  } else {
		  return defaultGenerator;     
	  }  
  }  
     
  public void setDefaultFallbackGenerator(IGenerator defaultGenerator) {
	  this.defaultGenerator = defaultGenerator;
  }
}
