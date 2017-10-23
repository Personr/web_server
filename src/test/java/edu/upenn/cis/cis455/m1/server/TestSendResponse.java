package edu.upenn.cis.cis455.m1.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import edu.upenn.cis.cis455.TestHelper;
import edu.upenn.cis.cis455.exceptions.HaltException;
import edu.upenn.cis.cis455.m1.server.HttpIoHandler;
import edu.upenn.cis.cis455.m1.server.implementations.HttpRequest;
import edu.upenn.cis.cis455.m1.server.implementations.HttpResponse;
import edu.upenn.cis.cis455.m1.server.implementations.RequestHandler;

import org.apache.logging.log4j.Level;

public class TestSendResponse {
    @Before
    public void setUp() {
        org.apache.logging.log4j.core.config.Configurator.setLevel("edu.upenn.cis.cis455", Level.DEBUG);
    }
    
    String sampleGetRequest = 
        "GET /index.html HTTP/1.1\r\n" +
        "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\r\n" +
        "Host: www.cis.upenn.edu\r\n" +
        "Accept-Language: en-us\r\n" +
        "Accept-Encoding: gzip, deflate\r\n";
    
    @Test
    public void testSendHtml() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(
            sampleGetRequest, 
            byteArrayOutputStream);
            
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "GET");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/index.html", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        
        requestHandler.handle(request, response);      
        HttpIoHandler.sendResponse(s, request, response);
        
        String result = byteArrayOutputStream.toString("UTF-8").replace("\r", "");
        
        assertTrue("Wrong protocol", result.startsWith("HTTP/1.1 200"));
        assertTrue("Wrong headers", result.contains("Date:") &&
                                    result.contains("Content-Type: text/html") &&
                                    result.contains("Content-Length: 202"));
        assertTrue("Wrong body", result.endsWith("</html>"));
    }
    
    @Test
    public void testSendGif() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(
            sampleGetRequest, 
            byteArrayOutputStream);
            
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "GET");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/folder/pennEng.gif", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        
        requestHandler.handle(request, response);      
        HttpIoHandler.sendResponse(s, request, response);
        
        String result = byteArrayOutputStream.toString("UTF-8").replace("\r", "");
        
        assertTrue("Wrong protocol", result.startsWith("HTTP/1.1 200"));
        assertTrue("Wrong headers", result.contains("Date:") &&
                                    result.contains("Content-Type: image/gif") &&
                                    result.contains("Content-Length: 13804"));
        assertTrue("Wrong body", result.contains("GIF"));
    }
    
    @Test
    public void testHeadGif() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(
            sampleGetRequest, 
            byteArrayOutputStream);
            
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "HEAD");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/folder/pennEng.gif", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        
        requestHandler.handle(request, response);      
        HttpIoHandler.sendResponse(s, request, response);
        
        String result = byteArrayOutputStream.toString("UTF-8").replace("\r", "");
        
        assertTrue("Wrong protocol", result.startsWith("HTTP/1.1 200"));
        assertTrue("Wrong headers", result.contains("Date:") &&
                                    result.contains("Content-Type: image/gif") &&
                                    result.contains("Content-Length: 13804"));
        assertTrue("Body found inappropriately", result.endsWith("13804\n"));
    }
    
    @Test
    public void testHeadHtml() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(
            sampleGetRequest, 
            byteArrayOutputStream);
            
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "HEAD");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/index.html", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        
        requestHandler.handle(request, response);      
        HttpIoHandler.sendResponse(s, request, response);
        
        String result = byteArrayOutputStream.toString("UTF-8").replace("\r", "");
        
        assertTrue("Wrong protocol", result.startsWith("HTTP/1.1 200"));
        assertTrue("Wrong headers", result.contains("Date:") &&
                                    result.contains("Content-Type: text/html") &&
                                    result.contains("Content-Length: 202"));
        assertTrue("Body found inappropriately", result.endsWith("202\n"));
    }
    
    @Test
    public void testCustomHeader() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(
            sampleGetRequest, 
            byteArrayOutputStream);
            
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "HEAD");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/index.html", false, headers, parms);
        HttpResponse response = new HttpResponse();
        response.contentLength(1);
        response.header("Test", "tested");  
        HttpIoHandler.sendResponse(s, request, response);
        
        String result = byteArrayOutputStream.toString("UTF-8").replace("\r", "");
        
        assertTrue("Wrong headers", result.contains("Test: tested"));
    }
    
    @Test
    public void testRedirect() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(
            sampleGetRequest, 
            byteArrayOutputStream);
            
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "HEAD");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/index.html", false, headers, parms);
        HttpResponse response = new HttpResponse();
        response.redirect("http://google.com", 304);
        HttpIoHandler.sendResponse(s, request, response);
        
        String result = byteArrayOutputStream.toString("UTF-8").replace("\r", "");
        assertTrue("Wrong status code", result.startsWith("HTTP/1.1 304"));
        assertTrue("Wrong headers", result.contains("Location: http://google.com") &&
                                    result.contains("Content-Type: text/html") &&
                                    result.contains("Content-Length: 111"));
        assertTrue("Wrong body", result.endsWith("</html>"));
    }
    
    @Test
    public void testCookie() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(
            sampleGetRequest, 
            byteArrayOutputStream);
            
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "HEAD");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/index.html", false, headers, parms);
        HttpResponse response = new HttpResponse();
        response.cookie("test", "123");
        response.contentLength(1);
        HttpIoHandler.sendResponse(s, request, response);
        
        String result = byteArrayOutputStream.toString("UTF-8").replace("\r", "");
        assertTrue("Cookie not found", result.contains("Set-Cookie: test=123\n"));
    }
    
    @Test
    public void testFullCookie() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(
            sampleGetRequest, 
            byteArrayOutputStream);
            
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "HEAD");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/index.html", false, headers, parms);
        HttpResponse response = new HttpResponse();
        response.cookie("/path", "test", "123", 1000, true, true);
        response.contentLength(1);
        HttpIoHandler.sendResponse(s, request, response);
        
        String result = byteArrayOutputStream.toString("UTF-8").replace("\r", "");
        assertTrue("Cookie not found", result.contains("Set-Cookie: test=123; Path=/path; Expires="));
        assertTrue("Cookie not found", result.contains("; Secure; HttpOnly\n"));
    }
    
    @Test
    public void testMultipleCookieMixed() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(
            sampleGetRequest, 
            byteArrayOutputStream);
            
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "HEAD");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/index.html", false, headers, parms);
        HttpResponse response = new HttpResponse();
        response.cookie("test", "123");
        response.cookie("/path", "test", "123", 1000, true, true);
        response.contentLength(1);
        HttpIoHandler.sendResponse(s, request, response);
        
        String result = byteArrayOutputStream.toString("UTF-8").replace("\r", "");
        assertTrue("First cookie not found", result.contains("Set-Cookie: test=123\n"));
        assertTrue("Second cookie not found", result.contains("Set-Cookie: test=123; Path=/path; Expires="));
        assertTrue("Second cookie not found", result.contains("; Secure; HttpOnly\n"));
    }
    
    @Test
    public void testMultipleCookieNoPathEq() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(
            sampleGetRequest, 
            byteArrayOutputStream);
            
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "HEAD");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/index.html", false, headers, parms);
        HttpResponse response = new HttpResponse();
        response.cookie("test", "123");
        response.cookie("test", "123", 1000, true, true);
        response.contentLength(1);
        HttpIoHandler.sendResponse(s, request, response);
        
        String result = byteArrayOutputStream.toString("UTF-8").replace("\r", "");
        assertTrue("First cookie not found", result.contains("Set-Cookie: test=123\n"));
        assertTrue("Second cookie inappropriately found", !result.contains("Set-Cookie: test=123; Expires="));
        assertTrue("Second cookie inappropriately found", !result.contains("; Secure; HttpOnly\n"));
    }
    
    @Test
    public void testMultipleCookieNoPathDiff() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(
            sampleGetRequest, 
            byteArrayOutputStream);
            
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "HEAD");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/index.html", false, headers, parms);
        HttpResponse response = new HttpResponse();
        response.cookie("test", "123");
        response.cookie("t", "123", 1000, true, true);
        response.contentLength(1);
        HttpIoHandler.sendResponse(s, request, response);
        
        String result = byteArrayOutputStream.toString("UTF-8").replace("\r", "");
        assertTrue("First cookie not found", result.contains("Set-Cookie: test=123\n"));
        assertTrue("Second cookie not found", result.contains("Set-Cookie: t=123; Expires="));
        assertTrue("Second cookie not found", result.contains("; Secure; HttpOnly\n"));
    }
    
    @Test
    public void testMultipleCookiePathEq() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(
            sampleGetRequest, 
            byteArrayOutputStream);
            
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "HEAD");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/index.html", false, headers, parms);
        HttpResponse response = new HttpResponse();
        response.cookie("/path","test", "123");
        response.cookie("/path","test", "123", 1000, true, true);
        response.contentLength(1);
        HttpIoHandler.sendResponse(s, request, response);
        
        String result = byteArrayOutputStream.toString("UTF-8").replace("\r", "");
        assertTrue("First cookie not found", result.contains("Set-Cookie: test=123; Path=/path\n"));
        assertTrue("Second cookie inappropriately found", !result.contains("Set-Cookie: test=123; Path=/path; Expires="));
        assertTrue("Second cookie inappropriately found", !result.contains("; Secure; HttpOnly\n"));
    }
    
    @Test
    public void testMultipleCookiePathDiff() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(
            sampleGetRequest, 
            byteArrayOutputStream);
            
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "HEAD");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/index.html", false, headers, parms);
        HttpResponse response = new HttpResponse();
        response.cookie("/path","test", "123");
        response.cookie("/p","test", "123", 1000, true, true);
        response.contentLength(1);
        HttpIoHandler.sendResponse(s, request, response);
        
        String result = byteArrayOutputStream.toString("UTF-8").replace("\r", "");
        assertTrue("First cookie not found", result.contains("Set-Cookie: test=123; Path=/path\n"));
        assertTrue("Second cookie not found", result.contains("Set-Cookie: test=123; Path=/p; Expires="));
        assertTrue("Second cookie not found", result.contains("; Secure; HttpOnly\n"));
    }
    
    @Test
    public void testMultipleCookiePathDiffName() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(
            sampleGetRequest, 
            byteArrayOutputStream);
            
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "HEAD");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/index.html", false, headers, parms);
        HttpResponse response = new HttpResponse();
        response.cookie("/path","test", "123");
        response.cookie("/path","t", "123", 1000, true, true);
        response.contentLength(1);
        HttpIoHandler.sendResponse(s, request, response);
        
        String result = byteArrayOutputStream.toString("UTF-8").replace("\r", "");
        assertTrue("First cookie not found", result.contains("Set-Cookie: test=123; Path=/path\n"));
        assertTrue("Second cookie not found", result.contains("Set-Cookie: t=123; Path=/path; Expires="));
        assertTrue("Second cookie not found", result.contains("; Secure; HttpOnly\n"));
    }

    
    @After
    public void tearDown() {}
}
