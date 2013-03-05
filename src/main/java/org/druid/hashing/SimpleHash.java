package org.druid.hashing;

import org.druid.exceptions.HashingException;

/**
 * Dumb simple hash which simply removes all the spaces.
 * @author Ian Lawson <a href="mailto:ian.lawson@redhat.com">ian.lawson@redhat.com</a>
 */
public class SimpleHash implements IHash
{
  @Override
  public String hash(String input) throws HashingException
  {
    return input.replaceAll("[ ]", "");
  }

  @Override
  public byte[] hash(byte[] input) throws HashingException
  {
    String workingString = new String(input);
    
    return( workingString.getBytes() );
  }
}
