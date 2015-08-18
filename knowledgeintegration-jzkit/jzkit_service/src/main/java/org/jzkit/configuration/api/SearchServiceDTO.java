package org.jzkit.configuration.api;

public class SearchServiceDTO {
  public String svc_code;
  public String svc_name;
  public String svc_class;
  public java.util.Map<String,String> properties = new java.util.HashMap<String,String>();
  public java.util.Map<String,String> archetypes = new java.util.HashMap<String,String>();
  public java.util.List<CollectionDefDTO> collections = new java.util.ArrayList<CollectionDefDTO>();
  public java.util.Map<String,java.util.Set> valid_indexes = new java.util.HashMap<String,java.util.Set>();
  public java.util.Map<String,String> translations = new java.util.HashMap<String,String>();
}
