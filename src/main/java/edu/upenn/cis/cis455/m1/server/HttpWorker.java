package edu.upenn.cis.cis455.m1.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.upenn.cis.cis455.ServiceFactory;
import edu.upenn.cis.cis455.exceptions.HaltException;
import edu.upenn.cis.cis455.m1.server.implementations.HttpRequest;
import edu.upenn.cis.cis455.m1.server.implementations.HttpResponse;
import edu.upenn.cis.cis455.m1.server.implementations.RequestHandler;
import edu.upenn.cis.cis455.util.HttpParsing;

import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.BufferedReader;

/**
 * Stub class for a thread worker for
 * handling Web requests
 */
public class HttpWorker extends Thread {

    final static Logger      logger = LogManager.getLogger(HttpWorker.class);
    private final HttpServer server;
    private String currentRequest = "";

    public HttpWorker(HttpServer server) {
        this.server = server;
    }
    
    public String currentRequest() {
        return currentRequest;
    }
    
    
    private void process(HttpTask task) {
        Socket socket = task.getSocket();

        HttpRequest request = null;
        logger.debug("Parsing request...");
        try {
            request = new HttpRequest(socket);
        } catch (HaltException except) {
            logger.debug("Bad request");
            HttpIoHandler.sendException(socket, request, except);
            return;
        }
        
        currentRequest = request.url();
        logger.debug("Request parsed: " + request.protocol() + " " + request.requestMethod() + " " + request.url());
        HttpResponse response = new HttpResponse();
        RequestHandler requestHandler = new RequestHandler(server.getRootDirectory());
        requestHandler.setServer(server);

        try {
            logger.debug("Building response...");
            requestHandler.handle(request, response);
            logger.debug("Response built: " + request.protocol() + " " + response.status());
            HttpIoHandler.sendResponse(socket, request, response);
        } catch (HaltException except) {
            logger.debug("Response built: " + request.protocol() + " " + except.statusCode());
            HttpIoHandler.sendException(socket, request, except);
        }
        logger.debug("Response sent");
    }


    public void run() {
        logger.debug("Worker started running");
        while (server.isActive()) {
            try {
                HttpTask task = server.readFromQueue();
                // Notify the server that a task has started
                server.start(this);
                process(task);
                // Notify that it's done
                server.done(this);
                currentRequest = "";
            } catch (Exception ex) {
                server.error(this);
                logger.error("Exception in worker thread: " + ex);
                ex.printStackTrace();
            }
        }
        logger.info("Worker terminating");
    }

}
