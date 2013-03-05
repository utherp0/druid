package org.druid.generation;

import org.druid.currency.Item;
import org.druid.currency.StandardAspects;

public abstract class Generator implements IGenerator
{
  protected Item setMandatoryAspects( Item item, String source )
  {
    item.addString(StandardAspects.GENERATOR, this.getClass().getCanonicalName());
    item.addString(StandardAspects.SOURCE, source );
    item.addString(StandardAspects.CREATED, Long.toString(item.getCreationUTC()));
    
    return item;
  }
}
