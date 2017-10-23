package edu.upenn.cis.cis455.m1.server.implementations;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.upenn.cis.cis455.m1.server.WebServiceController;
import edu.upenn.cis.cis455.m2.server.interfaces.Request;
import edu.upenn.cis.cis455.m2.server.interfaces.Session;
import edu.upenn.cis.cis455.util.HttpParsing;

public class HttpRequest extends Request {
    
    final static Logger logger = LogManager.getLogger(HttpRequest.class);
    
    private int port;
    private String queryString;
    private String uri;
    private String contentType;
    private String body;
    private String host;
    private int contentLength;
    private Map<String, String> headers;
    private Map<String, List<String>> parms;
    private Map<String, String> cookies;
    private Map<String, Object> attributes;
    
    public HttpRequest(Socket socket,
                       String uri,
                       boolean keepAlive,
                       Map<String, String> headers,
                       Map<String, List<String>> parms) {
        port = socket.getLocalPort();
        host = socket.getLocalAddress().toString();
        this.uri = uri;
        parseUri();
        this.persistentConnection(keepAlive);
        this.headers = headers;
        this.parms = parms;
        this.attributes = new HashMap<String, Object>();
    }
    
    public HttpRequest(Socket socket) {
        try {
            InputStream in = socket.getInputStream();
            String remoteIp = socket.getRemoteSocketAddress().toString();
            this.attributes = new HashMap<String, Object>();

            port = socket.getLocalPort();
            host = socket.getLocalAddress().toString();
            this.persistentConnection(false);
            headers = new HashMap<String, String>();
            parms = new HashMap<String, List<String>>();
            uri = HttpParsing.parseRequest(remoteIp, in, headers, parms);
            parseUri();
        } catch (IOException e) {
            logger.error(e);
        }           
    }
    
    /*
     * Puts the query string from uri in querryString
     */
    private void parseUri() {
        logger.debug("First uri: " + uri);
        if (uri.startsWith("http://")) {
            uri = uri.replaceAll("http://[^/]*/", "/");
        }
        logger.debug("Second uri: " + uri);
        if (uri.contains("?")) {
            String[] parts = uri.split("\\?");
            uri = parts[0];
            queryString = "?" + parts[1];
        } else {
            queryString = "";
        }   
    }

    @Override
    public String requestMethod() {
        return headers("Method");
    }

    @Override
    public String host() {
        if (headers("host") != null) {
            return headers("host");
        }
        return host;
    }

    @Override
    public String userAgent() {
        return headers("user-agent");
    }

    @Override
    public int port() {
        return port;
    }

    @Override
    public String pathInfo() {
        return uri;
    }

    @Override
    public String url() {
        String url = "http://" + host() + ":" + port() + pathInfo() + queryString;
        return url.replace("///", "//");
    }

    @Override
    public String uri() {
        String uri =  "http://" + host() + ":" + port() + pathInfo();
        return uri.replace("///", "//");
    }

    @Override
    public String protocol() {
        return headers("protocolVersion");
    }

    @Override
    public String contentType() {
        return contentType;
    }

    @Override
    public String ip() {
        return headers("http-client-ip");
    }

    @Override
    public String body() {
        //TODO
        return body;
    }

    @Override
    public int contentLength() {
        //TODO
        return contentLength;
    }

    @Override
    public String headers(String name) {
        return headers.get(name);
    }

    @Override
    public Set<String> headers() {
        return headers.keySet();
    }

    @Override
    public Session session() {
        String id = cookie("JSESSIONID");
        if (id != null) {
            HttpSession session = WebServiceController.getServer().getSession(id);
            if (session.isValid()) {
                session.access();
                return session;
            }
            WebServiceController.getServer().removeSession(id);
        }
        return null;
    }

    @Override
    public Session session(boolean create) {
        if (create) {
            HttpSession session = new HttpSession();
            String id = session.id();
            WebServiceController.getServer().addSession(id, session);
            return session;
        }
        return session();
    }

    @Override
    public Map<String, String> params() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String queryParams(String param) {
        return parms.get(param).get(0);
    }

    @Override
    public List<String> queryParamsValues(String param) {
        return parms.get(param);
    }

    @Override
    public Set<String> queryParams() {
        return parms.keySet();
    }

    @Override
    public String queryString() {
        return queryString;
    }

    @Override
    public void attribute(String attrib, Object val) {
        attributes.put(attrib, val);
        
    }

    @Override
    public Object attribute(String attrib) {
        return attributes.get(attrib);
    }

    @Override
    public Set<String> attributes() {
        return attributes.keySet();
    }

    @Override
    public Map<String, String> cookies() {
        if (cookies == null) {
            cookies = new HashMap<String, String>();
            String cookieStr = headers("Cookie");
            if (cookieStr != null) {
                String[] parts = cookieStr.split(";");
                for (String part : parts) {
                    String[] cookieParts = part.split("=");
                    cookies.put(cookieParts[0], cookieParts[1]);
                }
                
            }
        }
        return cookies;
    }
    
}
