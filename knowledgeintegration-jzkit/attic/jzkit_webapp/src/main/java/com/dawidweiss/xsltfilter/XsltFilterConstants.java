package com.dawidweiss.xsltfilter;


/**
 * A public class with several constants used in the XSLT filter.
 * 
 * @author Dawid Weiss
 */
public final class XsltFilterConstants {
    /**
     * To disable XSLT filtering for a given request (useful to return an XML verbatim, for example),
     * set any object under this key in the request context, for example:
     * <pre>
     * request.setAttribute(XsltFilter.NO_XSLT_PROCESSING, Boolean.TRUE);
     * </pre>
     */
    public static final String NO_XSLT_PROCESSING = "disable.xslt.filter";
}
