package edu.upenn.cis.cis455.m1.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
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
import edu.upenn.cis.cis455.util.HttpParsing;

import org.apache.logging.log4j.Level;

public class TestRequest {
    @Before
    public void setUp() {}
    
    String sampleGetRequest = 
        "GET http://oui.oui/a/b/hello.htm?q=x&v=12%200 HTTP/1.1\r\n" +
        "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\r\n" +
        "Host: www.cis.upenn.edu\r\n" +
        "Accept-Language: en-us\r\n" +
        "Accept-Encoding: gzip, deflate\r\n" +
        "Cookie: name1=value1;name2=value2;name3=value3\r\n" +
        "Connection: Keep-Alive\r\n\r\n";
    
    @Test
    public void testRequest() throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(
            sampleGetRequest, 
            byteArrayOutputStream);     
        
        HttpRequest request = new HttpRequest(s);

        String expectedMethod = "GET";
        String expectedUri = "http://www.cis.upenn.edu:0/a/b/hello.htm";
        String expectedPathInfo = "/a/b/hello.htm";
        String expectedUrl = "http://www.cis.upenn.edu:0/a/b/hello.htm?q=x&v=12%200";
        String expectedProtocol = "HTTP/1.1";
        String expectedUserAgent = "Mozilla/4.0 (compatible; MSIE5.01; Windows NT)";
        String expectedHost = "www.cis.upenn.edu";
        
        assertEquals("Wrong method", expectedMethod, request.requestMethod());
        assertEquals("Wrong uri", expectedUri, request.uri());
        assertEquals("Wrong path info", expectedPathInfo, request.pathInfo());
        assertEquals("Wrong url", expectedUrl, request.url());
        assertEquals("Wrong protocol", expectedProtocol, request.protocol());
        assertEquals("Wrong user agent", expectedUserAgent, request.userAgent());
        assertEquals("Wrong host", expectedHost, request.host());
    }

    
    @After
    public void tearDown() {}
}
