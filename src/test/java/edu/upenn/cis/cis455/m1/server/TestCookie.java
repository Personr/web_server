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

public class TestCookie {
    
    @Before
    public void setUp() {} 
    
    @Test
    public void testCookie() {
        Cookie cookie = new Cookie("test", "123");
        assertEquals("Wrong cookie", "Set-Cookie: test=123\r\n", cookie.toString());
    }
    
    @Test
    public void testFullCookie() {
        Cookie cookie = new Cookie("/path", "test", "123", 1000, true, true);
        assertTrue("Wrong start", cookie.toString().startsWith("Set-Cookie: test=123; Path=/path; Expires="));
        assertTrue("Wrong end", cookie.toString().endsWith("; Secure; HttpOnly\r\n"));
    }

    
    @After
    public void tearDown() {}
}
