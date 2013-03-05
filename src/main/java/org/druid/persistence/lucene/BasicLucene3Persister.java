package org.druid.persistence.lucene;

import java.util.List;
import java.util.Map;

import org.druid.connectors.utils.Lucene3Connector;
import org.druid.currency.Item;
import org.druid.exceptions.PersistenceException;
import org.druid.persistence.IPersister;

public class BasicLucene3Persister implements IPersister
{
  private String _physicalLocation = null;
  private Lucene3Connector _connector = new Lucene3Connector();

  @Override
  public void setLocation(String location) throws PersistenceException
  {
    this.initialise( location );
  }

  @Override
  public void setParameter(String name, String value)
  {

  }

  @Override
  public boolean removeItem(Item item) throws PersistenceException
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean persistItem(Item item, boolean overwrite) throws PersistenceException
  {
    

    return false;
  }

  @Override
  public boolean contains(Item item) throws PersistenceException
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public List<Item> persistItems(List<Item> items, boolean overwrite)
      throws PersistenceException
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void initialise(String location) throws PersistenceException
  {
    // Lazy load initialisation - if the location does not contain a LUCENE3 index one is
    // created there.
    
    // First check if an index exists by generating a report on it.
    try
    {
      @SuppressWarnings("unused")
      Map<String,String> report = _connector.report(location);
    }
    catch( Exception exc )
    {
      // No report provided, no index at location so create one
      try
      {
        _connector.createIndex(location);
        _physicalLocation = location;
      }
      catch( Exception innerExc )
      {
        throw new PersistenceException( "Failed to create index at " + location + " due to " + innerExc.toString());
      }
    }
  }

  @Override
  public void finalise() throws PersistenceException
  {
    if( _physicalLocation == null )
    {
      throw new PersistenceException( "Persister has not been correctly initialised.");
    }
    
    try
    {
      _connector.finish( _physicalLocation );      
    }
    catch( Exception exc )
    {
      throw new PersistenceException( "Unable to finalise the persister due to " + exc.toString());
    }
  }

  @Override
  public Map<String, String> report() throws PersistenceException
  {
    // TODO Auto-generated method stub
    return null;
  }
  
}
