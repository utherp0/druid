package org.druid.surfacing;

import java.util.List;
import java.util.Map;

import org.druid.currency.Item;
import org.druid.exceptions.SurfacingException;

public interface ISurfacer
{
  public void setLocation( String location );
  public void setParameter( String name, String value );
  public List<Item> surface( String name, String value, boolean exactMatch ) throws SurfacingException;
  public List<Item> surface( Map<String,String> tokens ) throws SurfacingException;
  public List<String> surfaceDataDictionary() throws SurfacingException;
  public boolean match( Item item, String cacheName ) throws SurfacingException;
  public void synchroniseCache() throws SurfacingException;
}
