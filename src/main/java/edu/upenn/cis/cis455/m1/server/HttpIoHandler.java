package edu.upenn.cis.cis455.m1.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static edu.upenn.cis.cis455.ServiceFactory.*;
import edu.upenn.cis.cis455.ServiceFactory;
import edu.upenn.cis.cis455.exceptions.HaltException;
import edu.upenn.cis.cis455.m1.server.implementations.HttpRequest;
import edu.upenn.cis.cis455.m1.server.interfaces.Request;
import edu.upenn.cis.cis455.m1.server.interfaces.Response;
import edu.upenn.cis.cis455.util.HttpParsing;

/**
 * Handles marshalling between HTTP Requests and Responses
 */
public class HttpIoHandler {
    final static Logger logger = LogManager.getLogger(HttpIoHandler.class);

    /**
     * Sends an exception back, in the form of an HTTP response code and message.  Returns true
     * if we are supposed to keep the connection open (for persistent connections).
     */
    public static boolean sendException(Socket socket, Request request, HaltException except) {
        String response = "HTTP/1.1 " + except.statusCode() + " " + except.body() + "\r";
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println(response);
            out.flush();
            out.close();
        } catch (IOException e) {
            logger.error(e);
        }
        
        if (request != null) {
            return request.persistentConnection();
        } else {
            return false;
        }
        
    }

    /**
     * Sends data back.   Returns true if we are supposed to keep the connection open (for 
     * persistent connections).
     */
    public static boolean sendResponse(Socket socket, Request request, Response response) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println(request.protocol() + " " + response.status() +  "\r");
            out.println(response.getHeaders());
            if (response.bodyRaw() != null) {
                out.println("\r");
                if (response.type().startsWith("text")) {
                    out.print(response.body());
                } else { // The file is not text
                    out.flush();
                    socket.getOutputStream().write(response.bodyRaw());
                }
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            logger.error(e);
        }
        
        if (request != null) {
            return request.persistentConnection();
        } else {
            return false;
        }
    }
}
