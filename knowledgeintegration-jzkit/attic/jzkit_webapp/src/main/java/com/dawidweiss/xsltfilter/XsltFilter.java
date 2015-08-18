package com.dawidweiss.xsltfilter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A filter which applies XSLT stylesheets to the result of a request.
 * 
 * The filter is activated when:
 * <ol>
 *  <li>the content has a mime type equal to <code>text/xml</code>,</li>
 *  <li>the content has a correct <code>xml-stylesheet</code> directive,</li>
 *  <li>processing has not been suppressed by setting {@link XsltFilterConstants#NO_XSLT_PROCESSING} in the
 *  request context.</li>  
 * </ol>
 * 
 * Filter configuration is given through the web application descriptor file (<code>web.xml</code>).
 * 
 * @author Dawid Weiss
 */
public class XsltFilter implements Filter {
    private final static Logger logger = Logger.getLogger(XsltFilter.class.toString());

    /**
     * Init parameter for the filter: if <code>true</code>, the parsed XSLT stylesheets will be cached
     * and reused. This is useful to speedup your application, but annoying during development, so you
     * can set this to <code>false</code> when you want the stylesheet reloaded for each request.
     *  
     * Default value: <code>true</code>
     */
    private final static String PARAM_TEMPLATE_CACHING = "template.caching";

    /**
     * A pool of cached stylesheets.
     */
    private TemplatesPool pool;

    /**
     * Servlet context of this filter.
     */
    private ServletContext context;

    /**
     * Place this filter into service.
     * 
     * @param filterConfig
     *            The filter configuration object
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        if (filterConfig == null) {
            throw new IllegalArgumentException("FilterConfig must not be null.");
        }
        
        this.context = filterConfig.getServletContext();
        
        final boolean templateCaching = getBooleanInit(filterConfig, PARAM_TEMPLATE_CACHING, true);

        try {
            pool = new TemplatesPool(templateCaching);
        } catch (Exception e) {
            final String message = "Could not initialize XSLT transformers pool.";
            logger.log(Level.SEVERE, message, e);
            throw new ServletException(message, e);
        }
    }

    /**
     * Returns init parameter or the default value.
     */
    private boolean getBooleanInit(FilterConfig config, String name, boolean defaultValue) {
        if (config.getInitParameter(name) != null) {
            return Boolean.valueOf(config.getInitParameter(name)).booleanValue();
        } else return defaultValue;
    }

    /**
     * Take this filter out of service.
     */
    public void destroy() {
        this.pool = null;
    }

    /**
     * Apply the XSLT stylesheet to the response and pass the result to the next filter.
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response; 

        // Generate the stream and process it with the stylesheet.
        final XsltFilterServletResponse wrappedResponse = 
            new XsltFilterServletResponse(httpResponse, httpRequest, context, pool);
        try {
            chain.doFilter(httpRequest, wrappedResponse);
        } catch (Throwable t) {
            wrappedResponse.filterError("An exception occurred.", t);
        } finally {
            wrappedResponse.finishResponse();
        }
    }
}