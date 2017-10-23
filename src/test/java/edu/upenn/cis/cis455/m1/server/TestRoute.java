package edu.upenn.cis.cis455.m1.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;
import edu.upenn.cis.cis455.ServiceFactory;
import edu.upenn.cis.cis455.TestHelper;
import edu.upenn.cis.cis455.exceptions.HaltException;
import edu.upenn.cis.cis455.m1.server.HttpIoHandler;
import edu.upenn.cis.cis455.m1.server.implementations.HttpRequest;
import edu.upenn.cis.cis455.m1.server.implementations.HttpResponse;
import edu.upenn.cis.cis455.m1.server.implementations.RequestHandler;
import edu.upenn.cis.cis455.util.HttpParsing;

import org.apache.logging.log4j.Level;

public class TestRoute {
    
    String sampleGetRequest = 
        "GET /test HTTP/1.1\r\n";
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    Socket s;
    
    @Before
    public void setUp() throws IOException {
        s = TestHelper.getMockSocket(
            sampleGetRequest, 
            byteArrayOutputStream);
    } 
    
    @Test
    public void testGetRoute() throws IOException {         
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "GET");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/test", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        HttpServer server = new HttpServer(0, null, 0);
        server.addRoute("GET", "/test", (req, res) -> {
            res.type("text/plain");
            res.body("Test");
            return "test";
        });
        requestHandler.setServer(server);
        
        requestHandler.handle(request, response);  
        
        assertEquals("Wrong status code", 200, response.status());
        assertEquals("Wrong type", "text/plain", response.type());
        assertEquals("Wrong content length", 4, response.contentLength());
        assertEquals("Wrong body", "Test", response.body());
    }
    
    @Test
    public void testPostRoute() throws IOException {         
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "POST");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/test", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        HttpServer server = new HttpServer(0, null, 0);
        server.addRoute("POST", "/test", (req, res) -> {
            res.type("text/plain");
            res.body("Test");
            return "test";
        });
        requestHandler.setServer(server);
        
        requestHandler.handle(request, response);  
        
        assertEquals("Wrong status code", 200, response.status());
        assertEquals("Wrong type", "text/plain", response.type());
        assertEquals("Wrong content length", 4, response.contentLength());
        assertEquals("Wrong body", "Test", response.body());
    }
    
    @Test
    public void testSimpleWildcard() throws IOException {         
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "GET");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/a/c/b", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        HttpServer server = new HttpServer(0, null, 0);
        server.addRoute("GET", "/a/*/b", (req, res) -> {
            res.type("text/plain");
            res.body("Test");
            return "test";
        });
        requestHandler.setServer(server);
        
        requestHandler.handle(request, response);  
        
        assertEquals("Wrong status code", 200, response.status());
        assertEquals("Wrong type", "text/plain", response.type());
        assertEquals("Wrong content length", 4, response.contentLength());
        assertEquals("Wrong body", "Test", response.body());
    }
    
    @Test
    public void testMulticharWildcard() throws IOException {         
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "GET");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/a/test/b", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        HttpServer server = new HttpServer(0, null, 0);
        server.addRoute("GET", "/a/*/b", (req, res) -> {
            res.type("text/plain");
            res.body("Test");
            return "test";
        });
        requestHandler.setServer(server);
        
        requestHandler.handle(request, response);  
        
        assertEquals("Wrong status code", 200, response.status());
        assertEquals("Wrong type", "text/plain", response.type());
        assertEquals("Wrong content length", 4, response.contentLength());
        assertEquals("Wrong body", "Test", response.body());
    }
    
    @Test
    public void testMulticharWildcard2() throws IOException {         
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "GET");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/a/test/b", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        HttpServer server = new HttpServer(0, null, 0);
        server.addRoute("GET", "/a/:name/b", (req, res) -> {
            res.type("text/plain");
            res.body("Test");
            return "test";
        });
        requestHandler.setServer(server);
        
        requestHandler.handle(request, response);  
        
        assertEquals("Wrong status code", 200, response.status());
        assertEquals("Wrong type", "text/plain", response.type());
        assertEquals("Wrong content length", 4, response.contentLength());
        assertEquals("Wrong body", "Test", response.body());
    }
    
    @Test
    public void testEmptyWildcard() throws IOException {         
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "GET");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/a/", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        HttpServer server = new HttpServer(0, null, 0);
        server.addRoute("GET", "/a/:name", (req, res) -> {
            res.type("text/plain");
            res.body("Test");
            return "test";
        });
        requestHandler.setServer(server);
        
        requestHandler.handle(request, response);  
        
        assertEquals("Wrong status code", 200, response.status());
        assertEquals("Wrong type", "text/plain", response.type());
        assertEquals("Wrong content length", 4, response.contentLength());
        assertEquals("Wrong body", "Test", response.body());
    }
    
    @Test
    public void testMultipleRoutes() throws IOException {         
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "POST");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/test/b", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        HttpServer server = new HttpServer(0, null, 0);
        server.addRoute("POST", "/a/b", (req, res) -> {
            res.type("text/html");
            res.body("<>");
            return "test";
        });
        server.addRoute("POST", "/:name/b", (req, res) -> {
            res.type("text/plain");
            res.body("Test");
            return "test";
        });
        server.addRoute("POST", "/a", (req, res) -> {
            res.type("text/html");
            res.body("<>");
            return "test";
        });
        requestHandler.setServer(server);
        
        requestHandler.handle(request, response);  
        
        assertEquals("Wrong status code", 200, response.status());
        assertEquals("Wrong type", "text/plain", response.type());
        assertEquals("Wrong content length", 4, response.contentLength());
        assertEquals("Wrong body", "Test", response.body());
    }
    
    @Test
    public void testDoubleWildcard() throws IOException {         
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "GET");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/a/c/d/b", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        HttpServer server = new HttpServer(0, null, 0);
        server.addRoute("GET", "/a/*/b", (req, res) -> {
            res.type("text/plain");
            res.body("Test");
            return "test";
        });
        requestHandler.setServer(server);
    
        HaltException exception;
        try {
            requestHandler.handle(request, response);
            exception = new HaltException(888);
        } catch (HaltException except) {
            exception = except;
        }        
        assertEquals("Wrong status code", 404, exception.statusCode());
    }
    
    @Test
    public void testShortWildcard() throws IOException {         
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "GET");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/a/b", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        HttpServer server = new HttpServer(0, null, 0);
        server.addRoute("GET", "/a/*/b", (req, res) -> {
            res.type("text/plain");
            res.body("Test");
            return "test";
        });
        requestHandler.setServer(server);
    
        HaltException exception;
        try {
            requestHandler.handle(request, response);
            exception = new HaltException(888);
        } catch (HaltException except) {
            exception = except;
        }        
        assertEquals("Wrong status code", 404, exception.statusCode());
    }
    
    @Test
    public void testShortEmptyWildcard() throws IOException {         
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "GET");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/a", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        HttpServer server = new HttpServer(0, null, 0);
        server.addRoute("GET", "/a/*", (req, res) -> {
            res.type("text/plain");
            res.body("Test");
            return "test";
        });
        requestHandler.setServer(server);
    
        HaltException exception;
        try {
            requestHandler.handle(request, response);
            exception = new HaltException(888);
        } catch (HaltException except) {
            exception = except;
        }        
        assertEquals("Wrong status code", 404, exception.statusCode());
    }

    
    @After
    public void tearDown() {}
}
