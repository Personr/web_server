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

public class TestRequestHandler {
    
    String sampleGetRequest = 
        "GET /index.html HTTP/1.1\r\n" +
        "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\r\n" +
        "Host: www.cis.upenn.edu\r\n" +
        "Accept-Language: en-us\r\n" +
        "Accept-Encoding: gzip, deflate\r\n";
        
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    Socket s;
    
    @Before
    public void setUp() throws IOException {
        s = TestHelper.getMockSocket(
            sampleGetRequest, 
            byteArrayOutputStream);
    }    
    
    @Test
    public void testHtml() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "GET");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/index.html", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        
        requestHandler.handle(request, response);
            
        int expectedCode = 200;
        String expectedType = "text/html";
        
        assertEquals("Wrong status code", expectedCode, response.status());
        assertEquals("Wrong type", expectedType, response.type());
        assertEquals("Wrong content length", 202, response.contentLength());
        assertTrue("Wrong body", response.body().startsWith("<!DOCTYPE html>"));
    }
    
    @Test
    public void testDirectory(){
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "GET");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/folder/pennEng.gif", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        
        requestHandler.handle(request, response);
            
        int expectedCode = 200;
        String expectedType = "image/gif";
        
        assertEquals("Wrong status code", expectedCode, response.status());
        assertEquals("Wrong type", expectedType, response.type());
        assertEquals("Wrong content length", 13804, response.contentLength());
        assertTrue("Wrong body", response.body().startsWith("GIF"));
    }
    
    @Test
    public void testHead() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "HEAD");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/folder/pennEng.gif", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        
        requestHandler.handle(request, response);
            
        int expectedCode = 200;
        String expectedType = "image/gif";
        
        assertEquals("Wrong status code", expectedCode, response.status());
        assertEquals("Wrong type", expectedType, response.type());
        assertEquals("Wrong content length", 13804, response.contentLength());
        assertEquals("Body inappropriately found", 0, response.bodyLength());
    }

    @After
    public void tearDown() {}
}
