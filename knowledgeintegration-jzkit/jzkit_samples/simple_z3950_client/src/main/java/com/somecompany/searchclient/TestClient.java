package com.somecompany.searchclient;

import org.jzkit.search.provider.iface.SearchableFactory;
import org.jzkit.search.provider.iface.Searchable;

public class TestClient {
  
  public static void main(String[] args) {
    System.err.println("Test Client");
    SearchableFactory sf = app_ctx.getBean("SearchableFactory");
    Searchable z3950_target = sf.create(new SearchServiceDescriptor("proto=z3950,addr=www.lcweb.gov:210,code=LC,shortname=\"Library of congress\",longname="\"Library of congress\""));
  }
}

