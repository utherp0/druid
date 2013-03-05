package org.druid.persistence;

import java.util.List;
import java.util.Map;

import org.druid.currency.Item;
import org.druid.exceptions.PersistenceException;

/**
 * Persister Interface for defining functionality across all persisters.
 * @author Ian Lawson (<a href="mailto:ian.lawson@redhat.com">ian.lawson@redhat.com</a>).
 *
 */
public interface IPersister
{
  public void setLocation( String location ) throws PersistenceException;
  public void setParameter( String name, String value ) throws PersistenceException;
  public boolean removeItem( Item item ) throws PersistenceException;
  public boolean persistItem( Item item, boolean overwrite ) throws PersistenceException;
  public List<Item> persistItems( List<Item> items, boolean overwrite ) throws PersistenceException;
  public boolean contains( Item item ) throws PersistenceException;
  public void initialise( String location ) throws PersistenceException;
  public void finalise() throws PersistenceException;
  public Map<String,String> report() throws PersistenceException;
}
