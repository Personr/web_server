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

public class TestFilter {
    
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
    public void testBefore() throws IOException {         
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "GET");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/test", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        HttpServer server = new HttpServer(0, null, 0);
        server.addBeforeFilter("", "", (req, res) -> {
            throw new HaltException(111);
        });
        requestHandler.setServer(server);
        
        HaltException exception;
        try {
            requestHandler.handle(request, response);
            exception = new HaltException(888);
        } catch (HaltException except) {
            exception = except;
        }        
        assertEquals("Wrong status code", 111, exception.statusCode());
    }
    
    @Test
    public void testAfter() throws IOException {         
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "GET");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/index.html", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        HttpServer server = new HttpServer(0, null, 0);
        server.addAfterFilter("", "", (req, res) -> {
            res.status(111);
            res.type("image/gif");
        });
        requestHandler.setServer(server);
        requestHandler.handle(request, response);  
        
        assertEquals("Wrong status code", 111, response.status());
        assertEquals("Wrong type", "image/gif", response.type());
        assertEquals("Wrong content length", 202, response.contentLength());
        assertTrue("Wrong body", response.body().startsWith("<!DOCTYPE html>"));
    }
    
    @Test
    public void testGetBefore() throws IOException {         
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "GET");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/test", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        HttpServer server = new HttpServer(0, null, 0);
        server.addBeforeFilter("", "GET", (req, res) -> {
            throw new HaltException(111);
        });
        requestHandler.setServer(server);
        
        HaltException exception;
        try {
            requestHandler.handle(request, response);
            exception = new HaltException(888);
        } catch (HaltException except) {
            exception = except;
        }        
        assertEquals("Wrong status code", 111, exception.statusCode());
    }
    
    @Test
    public void testGetAfter() throws IOException {         
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "GET");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/index.html", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        HttpServer server = new HttpServer(0, null, 0);
        server.addAfterFilter("", "GET", (req, res) -> {
            res.status(111);
            res.type("image/gif");
        });
        requestHandler.setServer(server);
        requestHandler.handle(request, response);  
        
        assertEquals("Wrong status code", 111, response.status());
        assertEquals("Wrong type", "image/gif", response.type());
        assertEquals("Wrong content length", 202, response.contentLength());
        assertTrue("Wrong body", response.body().startsWith("<!DOCTYPE html>"));
    }
    
    @Test
    public void testPostBefore() throws IOException {         
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "POST");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/test", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        HttpServer server = new HttpServer(0, null, 0);
        server.addBeforeFilter("", "POST", (req, res) -> {
            throw new HaltException(111);
        });
        requestHandler.setServer(server);
        
        HaltException exception;
        try {
            requestHandler.handle(request, response);
            exception = new HaltException(888);
        } catch (HaltException except) {
            exception = except;
        }        
        assertEquals("Wrong status code", 111, exception.statusCode());
    }
    
    @Test
    public void testPostAfter() throws IOException {         
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "POST");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/index.html", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        HttpServer server = new HttpServer(0, null, 0);
        server.addAfterFilter("", "POST", (req, res) -> {
            res.status(111);
            res.type("image/gif");
        });
        requestHandler.setServer(server);
        requestHandler.handle(request, response);  
        
        assertEquals("Wrong status code", 111, response.status());
        assertEquals("Wrong type", "image/gif", response.type());
    }
    
    @Test
    public void testMultipleBefore() throws IOException {         
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "POST");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/test", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        HttpServer server = new HttpServer(0, null, 0);
        server.addBeforeFilter("", "", (req, res) -> {
            res.contentLength(1000);
        });
        server.addBeforeFilter("", "POST", (req, res) -> {
            res.body("test");
        });
        server.addBeforeFilter("", "POST", (req, res) -> {
            res.type("image/gif");
        });
        requestHandler.setServer(server);
        requestHandler.handle(request, response);  
        
        assertEquals("Wrong status code", 200, response.status());
        assertEquals("Wrong type", "image/gif", response.type());
        assertEquals("Wrong content length", 1000, response.contentLength());
        assertEquals("Wrong body", "test", response.body());
    }
    
    @Test
    public void testMultipleAfter() throws IOException {         
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "POST");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/index.html", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        HttpServer server = new HttpServer(0, null, 0);
        server.addAfterFilter("", "", (req, res) -> {
            res.status(111);
        });
        server.addAfterFilter("", "", (req, res) -> {
            res.contentLength(1000);
        });
        server.addAfterFilter("", "POST", (req, res) -> {
            res.type("image/gif");
        }); 
        server.addAfterFilter("", "POST", (req, res) -> {
            res.body("test");
        });
        
        requestHandler.setServer(server);
        requestHandler.handle(request, response);  
        
        assertEquals("Wrong status code", 111, response.status());
        assertEquals("Wrong type", "image/gif", response.type());
        assertEquals("Wrong content length", 1000, response.contentLength());
        assertEquals("Wrong body", "test", response.body());
    }
    
    @Test
    public void testMultipleTypesBefore() throws IOException {         
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "POST");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/test", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        HttpServer server = new HttpServer(0, null, 0);
        server.addBeforeFilter("", "", (req, res) -> {
            res.contentLength(1000);
        });
        server.addBeforeFilter("", "POST", (req, res) -> {
            res.body("test");
        });
        server.addBeforeFilter("", "GET", (req, res) -> {
            res.type("image/gif");
        });
        requestHandler.setServer(server);
        requestHandler.handle(request, response);  
        
        assertEquals("Wrong status code", 200, response.status());
        assertEquals("Wrong type", "text/plain", response.type());
        assertEquals("Wrong content length", 1000, response.contentLength());
        assertEquals("Wrong body", "test", response.body());
    }
    
    @Test
    public void testMultipleTypesAfter() throws IOException {         
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "POST");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/index.html", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        HttpServer server = new HttpServer(0, null, 0);
        server.addAfterFilter("", "", (req, res) -> {
            res.status(111);
        });
        server.addAfterFilter("", "", (req, res) -> {
            res.contentLength(1000);
        });
        server.addAfterFilter("", "GET", (req, res) -> {
            res.type("image/gif");
        }); 
        server.addAfterFilter("", "POST", (req, res) -> {
            res.body("test");
        });
        
        requestHandler.setServer(server);
        requestHandler.handle(request, response);  
        
        assertEquals("Wrong status code", 111, response.status());
        assertEquals("Wrong type", "text/plain", response.type());
        assertEquals("Wrong content length", 1000, response.contentLength());
        assertEquals("Wrong body", "test", response.body());
    }
    
    @Test
    public void testBeforeAfter() throws IOException {         
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "POST");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/index.html", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        HttpServer server = new HttpServer(0, null, 0);
        server.addAfterFilter("", "", (req, res) -> {
            res.status(111);
        });
        server.addBeforeFilter("", "", (req, res) -> {
            res.contentLength(1000);
        });
        server.addBeforeFilter("", "POST", (req, res) -> {
            res.type("image/gif");
        }); 
        server.addAfterFilter("", "POST", (req, res) -> {
            res.body("test");
        });
        
        requestHandler.setServer(server);
        requestHandler.handle(request, response);  
        
        assertEquals("Wrong status code", 111, response.status());
        assertEquals("Wrong type", "image/gif", response.type());
        assertEquals("Wrong content length", 1000, response.contentLength());
        assertEquals("Wrong body", "test", response.body());
    }

    
    @After
    public void tearDown() {}
}