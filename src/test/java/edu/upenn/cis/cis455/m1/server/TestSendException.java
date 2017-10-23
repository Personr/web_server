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

public class TestSendException {
    @Before
    public void setUp() {
        org.apache.logging.log4j.core.config.Configurator.setLevel("edu.upenn.cis.cis455", Level.DEBUG);
    }
    
    String sampleGetRequest = 
        "GET /a/b/hello.htm?q=x&v=12%200 HTTP/1.1\r\n" +
        "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\r\n" +
        "Host: www.cis.upenn.edu\r\n" +
        "Accept-Language: en-us\r\n" +
        "Accept-Encoding: gzip, deflate\r\n" +
        "Cookie: name1=value1;name2=value2;name3=value3\r\n" +
        "Connection: Keep-Alive\r\n\r\n";
    
    @Test
    public void testSendException() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(
            sampleGetRequest, 
            byteArrayOutputStream);
        
        HaltException halt = new HaltException(404, "Not found");
        
        HttpIoHandler.sendException(s, null, halt);
        String result = byteArrayOutputStream.toString("UTF-8").replace("\r", "");
        
        assertTrue("Wrong exception", result.startsWith("HTTP/1.1 404"));
    }
    
    @Test
    public void testDirectory() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(
            sampleGetRequest, 
            byteArrayOutputStream);
        
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "GET");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/folder", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        
        try {
            requestHandler.handle(request, response);
            HttpIoHandler.sendResponse(s, request, response);
        } catch (HaltException except) {
            HttpIoHandler.sendException(s, request, except);
        }
        String result = byteArrayOutputStream.toString("UTF-8").replace("\r", "");
        
        assertTrue("Wrong exception for dir", result.startsWith("HTTP/1.1 404"));
    }
    
    @Test
    public void testWrongFile() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(
            sampleGetRequest, 
            byteArrayOutputStream);
        
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "GET");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/sdgs.txt", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        
        try {
            requestHandler.handle(request, response);
            HttpIoHandler.sendResponse(s, request, response);
        } catch (HaltException except) {
            HttpIoHandler.sendException(s, request, except);
        }
        String result = byteArrayOutputStream.toString("UTF-8").replace("\r", "");
        
        assertTrue("Wrong exception for file", result.startsWith("HTTP/1.1 404"));
    }
    
    @Test
    public void testOutsideFile() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(
            sampleGetRequest, 
            byteArrayOutputStream);
        
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "GET");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/../pom.xml", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        
        try {
            requestHandler.handle(request, response);
            HttpIoHandler.sendResponse(s, request, response);
        } catch (HaltException except) {
            HttpIoHandler.sendException(s, request, except);
        }
        String result = byteArrayOutputStream.toString("UTF-8").replace("\r", "");
        assertTrue("Wrong exception for file", result.startsWith("HTTP/1.1 403"));
    }
    
    @Test
    public void testAbsolute() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(
            sampleGetRequest, 
            byteArrayOutputStream);
        
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Method", "GET");
        headers.put("protocolVersion", "HTTP/1.1");
        HashMap<String, List<String>> parms = new HashMap<String, List<String>>();
        HttpRequest request = new HttpRequest(s, "/projects/555-hw1/pom.xml", false, headers, parms);
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(Paths.get("./www"));
        
        try {
            requestHandler.handle(request, response);
            HttpIoHandler.sendResponse(s, request, response);
        } catch (HaltException except) {
            HttpIoHandler.sendException(s, request, except);
        }
        String result = byteArrayOutputStream.toString("UTF-8").replace("\r", "");
        assertTrue("Wrong exception for file", result.startsWith("HTTP/1.1 404"));
    }

    
    @After
    public void tearDown() {}
}
