package edu.upenn.cis.cis455.m1.server;

import edu.upenn.cis.cis455.exceptions.HaltException;
import edu.upenn.cis.cis455.handlers.Filter;
import edu.upenn.cis.cis455.handlers.Route;
import edu.upenn.cis.cis455.m1.server.implementations.HttpWebService;
import edu.upenn.cis.cis455.m1.server.interfaces.WebService;

public class WebServiceController {
    
    private static WebService webService = new HttpWebService();
    
    public static WebService getSingleton() {
        return webService;
    }
    
    public static HttpServer getServer() {
        return webService.getServer();
    }
    
    public static void start() {
        webService.start();
    }
    
    public static void stop() {
        webService.stop();
    }
    
    public static void awaitInitialization() {
        webService.awaitInitialization();
    }
    
    public static HaltException halt() {
        return webService.halt();
    }


    public static HaltException halt(int statusCode) {
        return webService.halt(statusCode);
    }
    
    public static HaltException halt(String body) {
        return webService.halt(body);
    }
    
    public HaltException halt(int statusCode, String body) {
        return webService.halt(statusCode, body);
    }

    public static void staticFileLocation(String directory) {
        webService.staticFileLocation(directory);
    }

    public static void ipAddress(String ipAddress) {
        webService.ipAddress(ipAddress);
    }
    
    public static void port(int port) {
        webService.port(port);
    }
    
    public static void threadPool(int threads) {
        webService.threadPool(threads);
    }
    
    public static void get(String path, Route route) {
        webService.get(path, route);
    }
    
    public static void post(String path, Route route) {
        webService.post(path, route);
    }
    
    public static void put(String path, Route route) {
        webService.put(path, route);
    }
    
    public static void delete(String path, Route route) {
        webService.delete(path, route);
    }
    
    public static void head(String path, Route route) {
        webService.head(path, route);
    }
    
    public static void options(String path, Route route) {
        webService.options(path, route);
    }
    
    public static void before(Filter filter) {
        webService.before(filter);
        
    }

    public static void after(Filter filter) {
        webService.after(filter);
        
    }

    public static void before(String path, String acceptType, Filter filter) {
        webService.before(path, acceptType, filter);
        
    }

    public static void after(String path, String acceptType, Filter filter) {
        webService.after(path, acceptType, filter);
        
    }
    
}
