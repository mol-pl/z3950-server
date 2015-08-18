<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %> 
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %> 
<%@ page import="org.hibernate.*" %>
<%@ page import="org.hibernate.type.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.k_int.lom.datamodel.*" %>
<%
  String product_code = (String) request.getAttribute("ProductCode");
  String section = (String) request.getAttribute("Section");

  // String stylesheet="/webservices/public/xsl/learningobject_to_overview.xsl";
  // stylesheet="/webservices/public/xsl/learningobject_to_educational.xsl";
  // stylesheet="/webservices/public/xsl/learningobject_to_technical.xsl";
  // String search_url = base_dir+"/search?landscape=Products&operation=SearchRetrieve&query=dc.identifier%3d"+product_code+"&maximumRecords=1&recordSchema=learningobject";

  String base_dir = request.getContextPath();
  String page_name = null;

 // if ( session.getAttribute("qry") != null ) { ...display.... }

  System.err.println("Looking up product "+product_code);

  if ( ( product_code != null ) && ( product_code.length() > 0 ) ) {

    org.hibernate.Session sess = null;

    try {
      org.springframework.web.context.WebApplicationContext ctx =
        org.springframework.web.context.support.WebApplicationContextUtils.getRequiredWebApplicationContext(application);

      org.hibernate.SessionFactory factory = (org.hibernate.SessionFactory) ctx.getBean("COLWSSessionFactory");
      sess = factory.openSession();

      LearningObjectHDO lo = LearningObjectHDO.lookupByIdentifier(sess, product_code);
      if ( lo != null ) {
        request.setAttribute("lom",lo);
%>

<%
  boolean evaluated = false;
  if ( ( lo.getIsEvaluated() != null ) && ( lo.getIsEvaluated().equals(Boolean.TRUE) ) ) {
    evaluated = true;
  }
  if ( section.equalsIgnoreCase("educational") ) {
    page_name = "educational";
  } else if ( section.equalsIgnoreCase("technical") ) {
    page_name = "technical";
  } else if ( section.equalsIgnoreCase("evaluations") ) {
    page_name = "evaluations";
  } else { 
    page_name = "general";
  }
%>
<div class="clearing">&nbsp;</div>
  <h2><%=lo.getTitle()%></h2>
  <div id="pagelist">
    <ul>
      <% if (page_name.equals("general")) { %> <li class="selected">  <% } else { %> <li> <% } %>
      <a href="<%=base_dir%>/product/<%=lo.getIdentifier()%>/html/overview">Overview</a></li>
      <% if (page_name.equals("educational")) { %> <li class="selected">  <% } else { %> <li> <% } %>
      <a href="<%=base_dir%>/product/<%=lo.getIdentifier()%>/html/educational">Educational Info</a></li>
      <% if (page_name.equals("technical")) { %> <li class="selected">  <% } else { %> <li> <% } %>
      <a href="<%=base_dir%>/product/<%=lo.getIdentifier()%>/html/technical">Technical Details</a></li>
      <% if (evaluated) { %>
        <% if (page_name.equals("evaluations")) { %> <li class="selected">  <% } else { %> <li> <% } %>
        <a href="<%=base_dir%>/product/<%=lo.getIdentifier()%>/html/evaluations">Evaluations / Reviews</a></li>
      <% } %>
    </ul>
  </div>
  <div id="datacontainer" class="clearleft">
    <div id="databox">
      <%
        if (page_name.equals("educational")) {
          %><tiles:insert definition="resource.educational.panel"/><%
        }
        else if (page_name.equals("technical")) {
          %><tiles:insert definition="resource.technical.panel"/><%
        }
        else if (page_name.equals("evaluations")) {
          %><tiles:insert definition="resource.evaluations.panel"/><%
        }
        else {
          %><tiles:insert definition="resource.general.panel"/><%
        }
      %>

    <div id="pricing">
      <h3>Price</h3> <%
      if ( ( lo.getPriced() != null ) && ( lo.getPriced().equals(Boolean.TRUE) ) ) {
        for ( java.util.Iterator i = lo.getCosts().iterator(); i.hasNext(); ) {
          com.k_int.lom.datamodel.CostPriceDTO cost = (com.k_int.lom.datamodel.CostPriceDTO) i.next();
          if ( cost.getLicenseModel() != null ) { %><%=cost.getLicenseModel()%> : <%}
          if ( cost.getOtherModel() != null ) { %> <%=cost.getOtherModel()%> : <% }
          if ( cost.getAmount() != null ) { %> <%=cost.getAmount()%> <% }
          if ( cost.getTaxIncluded() != null ) {
            if ( cost.getTaxIncluded().equals("no") ) {
              %> Ex VAT <%
            }
            else {
              %> Inc VAT <%
            }
          }
 
          if ( i.hasNext() ) { %><hr/><% }
        }

        if ( lo.getTechSupportCost() != null ) {
          if ( lo.getTechSupportCost().getTerm().equals("included") ) {   // included, yes, no
            %><hr/>Cost Includes Support<%
          }
          else if ( lo.getTechSupportCost().getTerm().equals("yes") ) {
            %><hr/>Tech Support Extra<%
          }
          else {
            %><hr/>Tech Support Not Available<%
          }
        }

        if ( lo.getTeacherSupport() != null ) {
          if ( lo.getTeacherSupport().getTerm().equals("yes") ) {
            %><hr/>Teacher Support Materials Available<%
          }
          else {
            %><hr/>Teacher Support Materials Not Available<%
          }
        }
      }
      else {
        %> This resource is free <%
      }

      // Availability...
      if ( ( lo.getLocations() != null ) && ( lo.getLocations().size() > 0 ) ) {
        %> <hr/>Buy now from <%
        for ( java.util.Iterator i = lo.getLocations().iterator(); i.hasNext(); ) {
          com.k_int.lom.datamodel.ProductLocationDTO loc = (com.k_int.lom.datamodel.ProductLocationDTO) i.next();
          if ( loc.getSupplier() != null ) {
            if ( loc.getSupplier().getName() != null ) {
              %> <a href="<%=loc.getLocation()%>"><%=loc.getSupplier().getName()%></a> <%
            }
            else {
              %> <a href="<%=loc.getLocation()%>"><%=loc.getSupplier().getIdentifier()%></a> <%
            }
          }
          else {
            %> <a href="<%=loc.getLocation()%>"><%=loc.getLocation()%></a> <%
          }
          if ( i.hasNext() ) { %><br/><% }
        }
      }
      %>
    </div>
    </div>
    <div class="clearing">&#160;</div>
  </div>
<%
      }
      else {
        System.err.println("No Learning object for ID "+product_code);
      }

      sess.flush();
    }
    finally {
      try {
        if ( sess != null )
          sess.close();
      }
      catch ( Exception e ) {
        e.printStackTrace();
      }
    }
  }
  else {
  }
%>
