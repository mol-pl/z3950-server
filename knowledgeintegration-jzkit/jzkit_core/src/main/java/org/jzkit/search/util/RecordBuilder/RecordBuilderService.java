/**
 * Title:       QueryFormatter
 * Copyright:   Copyright (C) 2001- Knowledge Integration Ltd.
 * @author:     Ian Ibbotson (ian.ibbotson@k-int.com)
 * Company:     Knowledge Integration Ltd.
 */

package org.jzkit.search.util.RecordBuilder;

import org.jzkit.search.util.RecordModel.*;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Document;
import org.springframework.context.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

public class RecordBuilderService implements ApplicationContextAware, org.springframework.context.ApplicationListener {

  private static Log log = LogFactory.getLog(RecordBuilderService.class);
  private ApplicationContext ctx = null;

  public RecordBuilderService() {
    log.debug("new RecordBuilderService");
  }

  public void init() {
    log.debug("RecordBuilderService::init");
  }

  public InformationFragment createFrom(long hitno,
                                        String source_repository,
                                        String source_collection_name,
                                        Object handle,
                                        Document source,
                                        ExplicitRecordFormatSpecification spec) throws RecordBuilderException {
    InformationFragment result = null;
    log.debug("createFragment of "+spec);
    String factory_bean_name = "org.jzkit.recordbuilder."+spec.getEncoding();
    if ( ctx.containsBean(factory_bean_name) ) {
      RecordBuilder rb = (RecordBuilder) ctx.getBean(factory_bean_name);
      String element_set_name = null;
      if ( spec.getSetname() != null )
        element_set_name = spec.getSetname().toString();
      Object native_object = rb.createFrom(source, element_set_name);
      result = new InformationFragmentImpl(hitno,source_repository,source_collection_name,handle,native_object,spec);
    }
    else {
      log.warn("Cannot locate record builder "+factory_bean_name);
      result = new InformationFragmentImpl(hitno,
                                           source_repository,
                                           source_collection_name,
                                           handle,
                                           "Unable to locate record factory "+factory_bean_name,
                                           new ExplicitRecordFormatSpecification("string:diag:F"));
    }
    return result;
  }

  public Document getCanonicalXML(InformationFragment f) throws RecordBuilderException {
    Document result = null;
    log.debug("getCanonicalXML type="+f.getFormatSpecification());
    String factory_name = "org.jzkit.recordbuilder."+f.getFormatSpecification().getEncoding();

    if ( ctx.containsBean(factory_name) ) {
      RecordBuilder rb = (RecordBuilder) ctx.getBean(factory_name);
      result = rb.getCanonicalXML(f.getOriginalObject());
    }
    else {
      log.debug("No RecordBuilder for "+factory_name);
    }
    return result;
  }
  
  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

  public void onApplicationEvent(ApplicationEvent evt) {
    log.debug("onApplicationEvent "+evt);
  }

}
