package org.jzkit.ServiceDirectory;

import java.util.Iterator;

public interface ServiceDirectory
{
  public CollectionDescriptionDBO lookupCollectionDescription(String collection_id);
  public SearchServiceDescriptionDBO lookupSearchService(String service_id);
  public Iterator enumerateVisibleCollections();
}
