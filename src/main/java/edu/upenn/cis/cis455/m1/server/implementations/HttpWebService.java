package edu.upenn.cis.cis455.m1.server.implementations;

import java.nio.file.Paths;

import edu.upenn.cis.cis455.handlers.Filter;
import edu.upenn.cis.cis455.handlers.Route;
import edu.upenn.cis.cis455.m1.server.interfaces.WebService;
import edu.upenn.cis.cis455.m1.server.HttpServer;

public class HttpWebService extends WebService {
    
    private String ipAddress = "0.0.0.0";
    private String rootDirectory;
    private int port = 8080;
    private int threads = 10;

    @Override
    public void start() {
        this.basicServer = new HttpServer(port, Paths.get(rootDirectory), threads);
        this.basicServer.startWorkers();
        this.basicServer.listen();
    }

    @Override
    public void stop() {
        this.basicServer.stop();
        
    }

    @Override
    public void staticFileLocation(String directory) {
        rootDirectory = directory;
        
    }

    @Override
    public void ipAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        
    }

    @Override
    public void port(int port) {
        this.port = port;
        
    }

    @Override
    public void threadPool(int threads) {
        this.threads = threads;
        
    }
    
    @Override
    public void get(String path, Route route) {
        this.basicServer.addRoute("GET", path, route);

    }

    @Override
    public void post(String path, Route route) {
        this.basicServer.addRoute("POST", path, route);
        
    }

    @Override
    public void put(String path, Route route) {
        this.basicServer.addRoute("PUT", path, route);
        
    }

    @Override
    public void delete(String path, Route route) {
        this.basicServer.addRoute("DELETE", path, route);
        
    }

    @Override
    public void head(String path, Route route) {
        this.basicServer.addRoute("HEAD", path, route);
        
    }

    @Override
    public void options(String path, Route route) {
        this.basicServer.addRoute("OPTIONS", path, route);
        
    }

    @Override
    public void before(Filter filter) {
        this.basicServer.addBeforeFilter("", "", filter);
        
    }

    @Override
    public void after(Filter filter) {
        this.basicServer.addAfterFilter("", "", filter);
        
    }

    @Override
    public void before(String path, String acceptType, Filter filter) {
        this.basicServer.addBeforeFilter(acceptType, path, filter);
        
    }

    @Override
    public void after(String path, String acceptType, Filter filter) {
        this.basicServer.addAfterFilter(acceptType, path, filter);
        
    }
}
