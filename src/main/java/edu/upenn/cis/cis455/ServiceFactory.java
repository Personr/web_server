package edu.upenn.cis.cis455;

import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.upenn.cis.cis455.m1.server.WebServiceController;
import edu.upenn.cis.cis455.m1.server.implementations.HttpRequest;
import edu.upenn.cis.cis455.m1.server.implementations.HttpResponse;
import edu.upenn.cis.cis455.m1.server.implementations.HttpSession;
import edu.upenn.cis.cis455.m1.server.implementations.RequestHandler;
import edu.upenn.cis.cis455.m1.server.interfaces.WebService;
import edu.upenn.cis.cis455.m1.server.interfaces.HttpRequestHandler;
import edu.upenn.cis.cis455.m1.server.interfaces.Request;
import edu.upenn.cis.cis455.m1.server.interfaces.Response;
import edu.upenn.cis.cis455.m2.server.interfaces.Session;


public class ServiceFactory {

    /**
     * Get the HTTP server associated with port 8080
     */
    public static WebService getServerInstance() {
        return WebServiceController.getSingleton();
    }
    
    /**
     * Create an HTTP request given an incoming socket
     */
    public static Request createRequest(Socket socket,
                         String uri,
                         boolean keepAlive,
                         Map<String, String> headers,
                         Map<String, List<String>> parms) {
        return new HttpRequest(socket, uri, keepAlive, headers, parms);
    }
    
    /**
     * Gets a request handler for files (i.e., static content) or dynamic content
     */
    public static HttpRequestHandler createRequestHandlerInstance(Path serverRoot) {
        return new RequestHandler(serverRoot);
    }

    /**
     * Gets a new HTTP Response object
     */
    public static Response createResponse() {
        return new HttpResponse();
    }

    /**
     * Creates a blank session ID and registers a Session object for the request
     */
    public static String createSession() {
        HttpSession session = new HttpSession();
        String id = session.id();
        WebServiceController.getServer().addSession(id, session);
        return id;
    }
    
    /**
     * Looks up a session by ID and updates / returns it
     */
    public static Session getSession(String id) {        
        HttpSession session = WebServiceController.getServer().getSession(id);
        session.access();
        return session;
    }
}