package org.druid.hashing;

import org.druid.hashing.IHash;

public class HashFactory
{
  private HashFactory()
  {

  }

  @SuppressWarnings("unchecked")
  /**
   * Static factory for generating Hashing objects.
   * @param className class name of Hash to create (must conform to {@link org.druid.hashing.IHash IHash} interface. 
   * @return an instantiated object for the Hash
   * @throws ClassCastException if the target class cannot be cast to IHash
   * @throws ClassNotFoundException if the target class is not in the ClassLoader classpath
   * @throws InstantiationException if the instantiation fails
   * @throws IllegalAccessException if the class is unreachable within the ClassLoader
   */
  public static  <T extends IHash> T getHash( String className ) throws ClassCastException,ClassNotFoundException, InstantiationException, IllegalAccessException
  {
    return (T)Class.forName(className).newInstance();
  }
}
