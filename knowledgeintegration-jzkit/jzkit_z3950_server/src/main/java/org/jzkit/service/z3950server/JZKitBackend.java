package org.jzkit.service.z3950server;

import org.jzkit.z3950.server.*;

import org.jzkit.search.*;
import org.jzkit.search.provider.iface.*;
import org.jzkit.search.util.QueryModel.QueryModel;
import org.jzkit.search.util.RecordModel.*;
import org.jzkit.search.util.ResultSet.*;
import org.jzkit.search.landscape.SimpleLandscapeSpecification;
import org.jzkit.search.util.QueryModel.PrefixString.*;
import org.jzkit.search.provider.iface.SearchException;
import org.springframework.context.*;
import org.springframework.context.ApplicationContextAware;
import java.util.Collection;
import org.jzkit.z3950.gen.v3.Z39_50_APDU_1995.*;
import java.util.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.jzkit.search.impl.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * JZKitBackend is an implementation of the Z3950NonBlockingBackend which allows the generic z3950 protocol layer
 * to bind itself onto the jzkit meta search service. This enables the z3950 server to expose all collections
 * and protocols searchable by a given jzkit installation.
 */
public class JZKitBackend implements Z3950NonBlockingBackend, ApplicationContextAware {

  public static Log log = LogFactory.getLog(JZKitBackend.class);

  ApplicationContext ctx = null;

  private Map result_sets = new HashMap();
  private String impl_name = "JZKit Meta Search Service";

  private static RecordFormatSpecification def_request_spec = new ArchetypeRecordFormatSpecification("F");

  public JZKitBackend() {
    log.debug("New JZKitBackend "+this.hashCode());
  }

  public String getVersion() {
    String result = "ERROR";
    try {
      String classContainer = JZKitBackend.class.getProtectionDomain().getCodeSource().getLocation().toString();
      java.net.URL manifestUrl = new java.net.URL("jar:" + classContainer + "!/META-INF/MANIFEST.MF");
      java.util.jar.Manifest manifest = new java.util.jar.Manifest(manifestUrl.openStream());
      java.util.jar.Attributes atts = manifest.getMainAttributes();
      result = atts.getValue("Implementation-Version");
      // String CREATED_BY = attr.getValue("Created-by");
      // String SOFTWARE_VERSION = attr.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
      // String SOFTWARE_VENDOR = attr.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
      // String SOFTWARE_VENDOR_TITLE = attr.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
    }
    catch ( Exception e ) {
      log.error("Problem extracting server version from manifest",e);
    }
    finally {
    }
    return result;
  }

  public String getImplName() {
    return impl_name;
  }

  public void setImplName(String impl_name) {
    this.impl_name = impl_name;
  }

  public void search(BackendSearchDTO bsr) {

    log.debug("JZKitBackend::search "+this.hashCode());

    // QueryModel qm = new PrefixString("@attrset bib-1 @attr 1=4 science");
    QueryModel qm = new org.jzkit.z3950.QueryModel.Type1QueryModel((RPNQuery_type)(bsr.query.o));

    try {
      StatelessQueryService sqs = (StatelessQueryService) ctx.getBean("StatelessQueryService");
      String query_id = null;
      LandscapeSpecification landscape = new SimpleLandscapeSpecification(bsr.database_names);
      ExplicitRecordFormatSpecification exp = null;

      StatelessSearchResultsPageDTO res = sqs.getResultsPageFor(query_id,
                                                                qm,
                                                                landscape,
                                                                0,
                                                                0, 
                                                                def_request_spec,
                                                                exp,
                                                                null);

      log.debug("Search status = "+res.getSearchStatus());

      if ( res.getSearchStatus() == 8 ) {
        // Search failure
        bsr.search_status = false;
        bsr.result_count = 0;
      }
      else {
        bsr.search_status = true;
        bsr.result_count = res.getRecordCount();
      }

      bsr.result_set_status = 1;

      bsr.status_report = createStatusReport(res.result_set_info);
  
      log.debug("Search: result set name :"+bsr.result_set_name);

      if ( ( bsr.result_set_name != null ) && ( bsr.result_set_name.length() > 0 ) ) {
        result_sets.put(bsr.result_set_name, new ZSetInfo(query_id, qm, landscape));
      }
      else {
        result_sets.put("Default", new ZSetInfo(query_id, qm, landscape));
      }
      log.debug("jzkit_backend result sets size now "+result_sets.size()+" in backend "+this.hashCode());
    }
    catch (SearchException se) {
      log.error("Problem",se);
      bsr.search_status = false;
      bsr.diagnostic_code = JZKitToDiag1(se.error_code);
      bsr.result_set_status = 3;
      if ( bsr.diagnostic_code > 0 ) {
        // bsr.diagnostic_addinfo = getDiag1AddinfoString(bsr.diagnostic_code);
        if ( se.getMessage() != null ) {
          bsr.diagnostic_addinfo = se.getMessage();
        }
        bsr.diagnostic_data = se.diagnostic_data;
      }
    }
    catch ( org.jzkit.search.util.ResultSet.IRResultSetException irrse ) {
      log.error("Problem",irrse);
      bsr.search_status = false;
    }
    catch ( org.jzkit.search.util.QueryModel.InvalidQueryException iqe ) {
      log.error("Problem",iqe);
      bsr.search_status = false;
    }
    catch ( Exception e ) {
      log.error("Problem",e);
      bsr.search_status = false;
    }

    bsr.assoc.notifySearchResult(bsr);
  }

  private BackendStatusReportDTO createStatusReport(IRResultSetInfo rsi) {
    BackendStatusReportDTO result = new BackendStatusReportDTO();
    result.source_id = rsi.name;
    // result.search_status = rsi.status;
    result.result_set_status = rsi.status;
    result.result_count = rsi.total_fragment_count;
    result.status_string = rsi.last_message;
    if ( rsi.record_sources != null ) {
      result.child_reports = new ArrayList<BackendStatusReportDTO>();
      for ( java.util.Iterator i = rsi.record_sources.iterator(); i.hasNext(); ) {
        result.child_reports.add(createStatusReport((IRResultSetInfo)i.next()));
      }
    }
    return result;
  }
                                                                                                                                          
  /**
   * Implementor must call assoc.notifyPresentResult(result);
   */
  public void present(BackendPresentDTO bpr) {
    try {
      log.debug("JZKitBackend::present - "+this.hashCode());
      log.debug("looking for archetype: "+bpr.archetype+", explicit="+bpr.explicit);
      log.debug("looking for result set: "+bpr.result_set_name);
      log.debug("jzkit_backend result sets size "+result_sets.size()+" in backend "+this.hashCode());

      ZSetInfo zsi = (ZSetInfo) result_sets.get(bpr.result_set_name);


      if ( zsi != null ) {
        // Check that it's not out of range
        StatelessQueryService sqs = (StatelessQueryService) ctx.getBean("StatelessQueryService");
        log.debug("Located result set info");
        StatelessSearchResultsPageDTO res = sqs.getResultsPageFor(zsi.getSetname(),
                                                                  zsi.getQueryModel(),
                                                                  zsi.getLandscape(),
                                                                  bpr.start,
                                                                  bpr.count,
                                                                  def_request_spec,
                                                                  bpr.explicit,
                                                                  null);
        bpr.total_hits = res.total_hit_count;

        bpr.result_records = res.getRecords();
        if ( bpr.start > bpr.count ) {
          log.debug("Present out of range.. set error code");
        }
      }
      else {
        log.debug("Unable to locate result set");
      }

      bpr.next_result_set_position = bpr.start + bpr.count;
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    finally {
    }

    log.debug("calling notifyPresentResult");
    bpr.assoc.notifyPresentResult(bpr);
  }
                                                                                                                                          
  /**
   * Implementor must call assoc.notifyDeleteResultSetResult(result);
   */
  public void deleteResultSet(BackendDeleteDTO del_req) {
    log.debug("JZKitBackend::deleteResultSet");
    del_req.assoc.notifyDeleteResult(del_req);
  }

  public void setApplicationContext(ApplicationContext ctx) {
    this.ctx = ctx;
  }

  private static int JZKitToDiag1(int code) {
    int result = 0;
    switch ( code ) {
      case SearchException.INTERNAL_ERROR:      result=2;    break;   // Diag-1 : Temporary System Error
      case SearchException.UNKOWN_COLLECTION:   result=235;  break;   // Diag-1 : Specified Database Does Not Exist
      case SearchException.UNSUPPORTED_ACCESS_POINT:   result=114;  break;   // Diag-1 : Unsupported use attribute
      case SearchException.UNSUPPORTED_ATTR_NAMESPACE:   result=113;  break;   // Diag-1 : Unsupported attribute type
      default: break;
    }
    return result;
  }

  // Return a message format string to be used to format diagnostic data
  public static String getDiag1AddinfoString(long bib1_diag_code) {
    return "{1}";
  }
}
