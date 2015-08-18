package org.jzkit.webapp.filters;
 
import java.io.IOException;
import java.util.*;
 
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
 
public class AcceptFilter implements Filter {

    private HashMap<String,String> map = new HashMap<String,String>();

    @SuppressWarnings("unchecked")
    public void init(FilterConfig config) throws ServletException {
        Enumeration<String> names = config.getInitParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            map.put("."+name,config.getInitParameter(name));
        }
    }
 
    public void destroy() {}

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new Wrapper((HttpServletRequest)request),response);
    }
 
    private class Wrapper extends HttpServletRequestWrapper {
        public Wrapper(HttpServletRequest request) {
            super(request);
        }
        private String getExtension(String path) {
            int index = path.lastIndexOf('.');
            if (index == -1) {
                return "";
            }
            String extension = path.substring(index);
            if (map.get(extension) == null) {
                return "";
            }
            return extension;
        }
 
        @Override
        public String getRequestURI() {
            String uri = super.getRequestURI();
            return uri.substring(0,uri.length() - getExtension(uri).length());
        }
 
        @Override
        @SuppressWarnings("unchecked")
        public Enumeration getHeaders(String name) {
            if ("accept".equalsIgnoreCase(name)) {
                String type = map.get(getExtension(super.getRequestURI()));
                if (type != null) {
                    Vector<String> values = new Vector<String>();
                    values.add(type);
                    return values.elements();
                }
            }
            return super.getHeaders(name);
        }
    }
}

