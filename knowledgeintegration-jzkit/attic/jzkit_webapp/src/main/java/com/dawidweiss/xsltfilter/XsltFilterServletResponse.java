package com.dawidweiss.xsltfilter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * A wrapper around a {@link HttpServletResponse} which attempts to detect
 * the type of output acquired from the servlet chain and apply a stylesheet to it
 * if all conditions mentioned in {@link com.dawidweiss.xsltfilter.XsltFilter} are
 * met.
 * 
 * @author Dawid Weiss.
 */
final class XsltFilterServletResponse extends HttpServletResponseWrapper {
    private static final Logger log = Logger.getLogger(XsltFilterServletResponse.class.toString());

    /**
     * If true, the stream will be passed verbatim to the next filter. This
     * usually happens when the output has a mime type different than
     * <code>text/xml</code>.
     */
    private boolean passthrough;

    /** 
     * The actual {@link HttpServletResponse}. 
     */
    private HttpServletResponse origResponse = null;

    /**
     * The actual {@link HttpServletRequest}. 
     */
    private HttpServletRequest origRequest;

    /**
     * The {@link ServletOutputStream} returned from {@link #getOutputStream()} or
     * <code>null</code>.
     */
    private ServletOutputStream stream = null;

    /**
     * The {@link PrintWriter} returned frmo {@link #getWriter()} or <code>null</code>.
     */
    private PrintWriter writer = null;

    /** 
     * Original response's content type. 
     */
    private String contentType = null;

    /** 
     * A pool of stylesheets used for XSLT processing.
     */
    private TemplatesPool transformers;

    /**
     * Servlet context for resolving local paths.
     */
    private ServletContext context;

    /**
     * Creates an XSLT filter servlet response for a single request, 
     * wrapping a given {@link HttpServletResponse}.
     * 
     * @param response The original chain's {@link HttpServletResponse}.
     * @param request  The original chain's {@link HttpServletRequest}.
     * @param transformers A pool of transformers to be used with this request.
     */
    public XsltFilterServletResponse(HttpServletResponse response, HttpServletRequest request,
            ServletContext context, TemplatesPool transformers)
    {
        super(response);
        this.origResponse = response;
        this.transformers = transformers;
        this.origRequest = request;
        this.context = context;
    }

    /**
     * We override this method to detect XML data streams.
     */
    public void setContentType(String contentType) {
        // This should be parsed and the desired 
        // character encoding should be extracted (for getWriter()).
        this.contentType = contentType;

        // Check if XSLT processing has been suppressed for this request.
        final boolean processingSuppressed = 
            (origRequest.getAttribute(XsltFilterConstants.NO_XSLT_PROCESSING) != null);

        if (processingSuppressed) {
            // Processing is suppressed.
            log.log(Level.FINE, "XSLT processing disabled for the request.");
        }

        if (!processingSuppressed && 
                (contentType.startsWith("text/xml") || contentType.startsWith("application/xml"))) {
            // We have an XML data stream. Set the real response to proper content
            // type.
            // Should we make the encoding a configurable setting?
            // The output mime type should be taken from the XSLT stylesheet (with
            //       some default value perhaps).
            origResponse.setContentType("text/html; charset=UTF-8");
        } else {
            // The input is something we won't process anyway, so simply passthrough
            // all data directly to the output stream.
            if (!processingSuppressed) {
                log.log(Level.WARNING, "Content type is not text/xml or application/xml (" + contentType + "), passthrough.");
            }

            origResponse.setContentType(contentType);
            passthrough = true;

            // If the output stream is already initialized, passthrough everything.
            if (stream != null && stream instanceof DeferredOutputStream) {
                try {
                    ((DeferredOutputStream) stream).passthrough(
                            origResponse.getOutputStream());
                } catch (IOException e) {
                    ((DeferredOutputStream) stream).setExceptionOnNext(e);
                }
            }
        }
    }

    /**
     * We do not delegate content length because it will most likely change.
     */
    public void setContentLength(final int length) {
        log.log(Level.FINER, "Original content length (ignored): " + length);
    }
    
    /**
     * Flush the internal buffers. This only works if XSLT transformation is
     * suppressed.
     */
    public void flushBuffer() throws IOException {
        this.stream.flush();
    }

    /**
     * Return the byte output stream for this response. This is either
     * the original stream or a buffered stream. 
     * 
     * @exception IllegalStateException Thrown when character stream has been already
     *            initialized ({@link #getWriter()}).
     */
    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null) {
            throw new IllegalStateException(
                    "Character stream has been already initialized. Use streams consequently.");
        }

        if (stream != null) {
            return stream;
        }

        if (passthrough) {
            stream = origResponse.getOutputStream();
        } else {
            stream = new DeferredOutputStream();
        }

        return stream;
    }

    /**
     * Return the character output stream for this response. This is either
     * the original stream or a buffered stream. 
     * 
     * @exception IllegalStateException Thrown when byte stream has been already
     *            initialized ({@link #getOutputStream()}).
     */
    public PrintWriter getWriter() throws IOException {
        if (stream != null) {
            throw new IllegalStateException(
                "Byte stream has been already initialized. Use streams consequently.");
        }

        if (writer != null) {
            return writer;
        }

        if (passthrough) {
            writer = this.origResponse.getWriter();
            return writer;
        }

        // This is a bug. The character encoding should be extracted in
        // {@link #setContentType()}, saved somewhere locally and used here.
        // The response's character encoding may be different (depends on the 
        // stylesheet).
        final String charEnc = origResponse.getCharacterEncoding();

        this.stream = new DeferredOutputStream();
        if (charEnc != null) {
            writer = new PrintWriter(new OutputStreamWriter(stream, charEnc));
        } else {
            writer = new PrintWriter(stream);
        }

        return writer;
    }

    /**
     * This method must be invoked at the end of processing. The streams are closed and
     * their content is analyzed. Actual XSLT processing takes place here.
     */
    void finishResponse() throws IOException {
        if (writer != null) {
            writer.close();
        } else {
            if (stream != null) stream.close();
        }

        // If we're not in passthrough mode, then we need to
        // finalize XSLT transformation.
        if (false == passthrough) {
            if (stream != null) {
                final byte[] bytes = ((DeferredOutputStream) stream).getBytes();
                final boolean processingSuppressed = (origRequest.getAttribute(XsltFilterConstants.NO_XSLT_PROCESSING) != null);

                if (processingSuppressed) {
                    System.err.println("processing suppressed");
                    // Just copy the buffered data to the output
                    // directly.
                    final OutputStream os = origResponse.getOutputStream();
                    os.write(bytes);
                    os.close();
                } else {
                    System.err.println("processing performed");
                    // Otherwise apply XSLT transformation to it.
                    try {
                        processWithXslt(bytes, origResponse);
                    } catch (TransformerException e) {
                        filterError("Error applying stylesheet.", e);
                    }
                }
            }
        }
    }

    /**
     * Process the byte array (input XML) with the XSLT stylesheet and push the 
     * result to the output stream.
     */
    private void processWithXslt(byte[] bytes, final HttpServletResponse response)
            throws TransformerConfigurationException, TransformerException
    {
        final TransformingDocumentHandler docHandler;
        try {
            final XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setFeature("http://xml.org/sax/features/namespaces",true);
            reader.setFeature("http://xml.org/sax/features/namespace-prefixes",true);

            final String baseApplicationURL = 
                origRequest.getScheme() + "://" + origRequest.getServerName() + ":" + origRequest.getServerPort();

            docHandler = new TransformingDocumentHandler(
                    baseApplicationURL, origRequest.getContextPath(), context,
                    transformers);
            docHandler.setContentTypeListener(new ContentTypeListener() {
                public void setContentType(String contentType, String encoding) {
                    if (encoding == null) {
                        response.setContentType(contentType);
                    } else {
                        response.setContentType(contentType + "; charset=" + encoding);
                    }
                    try {
                        docHandler.setTransformationResult(new StreamResult(response.getOutputStream()));
                    } catch (IOException e) {
                        throw new RuntimeException("Could not open output stream.");
                    }
                }
            });

            reader.setContentHandler(docHandler);
            // docHandler.setParams(props);

            try {
                reader.parse(new InputSource(new ByteArrayInputStream(bytes)));
            } finally {
                docHandler.cleanup();
            }
        } catch (SAXException e) {
            throw new TransformerException("Parsing exception: " + e.toString());
        } catch (IOException e) {
            throw new TransformerException("IO Exception when reading input: " + e.toString());
        }
    }
    
    /**
     * Attempts to send an internal server error HTTP error, if possible.
     * Otherwise simply pushes the exception message to the output stream. 
     *
     * @param message Message to be printed to the logger and to the output stream.
     * 
     * @param t Exception that caused the error.
     */
    protected void filterError(String message, Throwable t) {
        log.log(Level.WARNING, "XSLT filter error: " + message, t);
        if (false == origResponse.isCommitted()) {
            // Reset the buffer and previous status code.
            origResponse.reset();
            origResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            origResponse.setContentType("text/html; charset=UTF-8");
        }

        // Response committed. Just push the error to the output stream.
        try {
            final OutputStream os = origResponse.getOutputStream();
            final PrintWriter osw = new PrintWriter(new OutputStreamWriter(os, "iso8859-1"));
            osw.write("<h1 style=\"color: red; margin-top: 1em;\">");
            osw.write("Internal server exception");
            osw.write("</h1>");
            osw.write("<b>URI</b>: " + origRequest.getRequestURI() + "\n<br/><br/>");
            serializeException(osw, t);
            if (t instanceof ServletException && ((ServletException) t).getRootCause() != null) {
                osw.write("<br/><br/><h2>ServletException root cause:</h2>");
                serializeException(osw, ((ServletException) t).getRootCause());
            }
            osw.flush();
        } catch (IOException e) {
            // not much to do in such case (connection broken most likely).
            log.log(Level.FINE, "Filter error could not be returned to client.");
        }
    }

    /**
     * Utility method to serialize an exception and its stack trace to simple HTML.
     */
    private final void serializeException(PrintWriter osw, Throwable t) {
        osw.write("<b>Exception</b>: " + t.toString() + "\n<br/><br/>");
        osw.write("<b>Stack trace:</b>");
        osw.write("<pre style=\"margin: 1px solid red; padding: 3px; font-family: sans-serif; font-size: small;\">");
        t.printStackTrace(osw);
        osw.write("</pre>");
    }
}
