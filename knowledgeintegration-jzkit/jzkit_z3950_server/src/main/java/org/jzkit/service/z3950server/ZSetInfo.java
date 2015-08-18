package org.jzkit.service.z3950server;

import org.jzkit.z3950.server.*;

import org.jzkit.search.*;
import org.jzkit.search.provider.iface.*;
import org.jzkit.search.util.QueryModel.QueryModel;
import org.jzkit.search.util.RecordModel.*;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.landscape.SimpleLandscapeSpecification;
import org.jzkit.search.util.QueryModel.PrefixString.*;

import org.springframework.context.*;
import org.springframework.context.ApplicationContextAware;
import java.util.Collection;
import org.jzkit.z3950.gen.v3.Z39_50_APDU_1995.*;
import java.util.logging.*;
import java.util.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ZSetInfo {

  public String setname = null;
  public LandscapeSpecification landscape = null;
  public QueryModel query_model = null;

  public ZSetInfo() {
  }

  public ZSetInfo(String setname,
                  QueryModel query_model,
                  LandscapeSpecification landscape) {
    this.setname = setname;
    this.landscape = landscape;
    this.query_model = query_model;
  }

  public String getSetname() {
    return setname;
  }

  public QueryModel getQueryModel() {
    return query_model;
  }

  public LandscapeSpecification getLandscape() {
    return landscape;
  }
}
