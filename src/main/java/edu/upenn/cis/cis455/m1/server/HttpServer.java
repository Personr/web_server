package edu.upenn.cis.cis455.m1.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.upenn.cis.cis455.handlers.Filter;
import edu.upenn.cis.cis455.handlers.Route;
import edu.upenn.cis.cis455.m1.server.implementations.HttpSession;

/**
 * Stub for your HTTP server, which
 * listens on a ServerSocket and handles
 * requests
 */
public class HttpServer implements ThreadManager {
    static final Logger logger = LogManager.getLogger(HttpServer.class);	
    
    private final int QUEUE_SIZE = 30;
    private final HttpTaskQueue queue;
    private Map<HttpWorker, Boolean> threadPool;
    private final int portNumber;
    private final Path rootDirectory;
    private ServerSocket serverSocket;
    private boolean active = true;
    private Map<String, Map<String, Route>> routes;
    private List<FilterPath> beforeFilters;
    private List<FilterPath> afterFilters;
    private Map<String, HttpSession> sessions;
    
    public HttpServer(int portNumber, Path rootDirectory, int threads) {
        this.portNumber = portNumber;
        this.rootDirectory = rootDirectory;
        this.sessions = new HashMap<String, HttpSession>();
        
        this.beforeFilters = new ArrayList<FilterPath>();
        this.afterFilters = new ArrayList<FilterPath>();
        
        this.routes = new HashMap<String, Map<String, Route>>();
        routes.put("GET", new HashMap<String, Route>());
        routes.put("POST", new HashMap<String, Route>());
        routes.put("PUT", new HashMap<String, Route>());
        routes.put("DELETE", new HashMap<String, Route>());
        routes.put("HEAD", new HashMap<String, Route>());
        routes.put("OPTIONS", new HashMap<String, Route>());
        
		this.queue = new HttpTaskQueue(QUEUE_SIZE);
        threadPool = new HashMap<HttpWorker, Boolean>();	
        for (int i = 0; i < threads; i++) {
            HttpWorker worker = new HttpWorker(this);
            threadPool.put(worker, false);
        }
	}
	
	public void addSession(String id, HttpSession session) {
	    sessions.put(id, session);
	}
	
	public HttpSession getSession(String id) {
	    return sessions.get(id);
	}
	
	public void removeSession(String id) {
	    sessions.remove(id);
	}
	
	public Map<String, Route> getRoutes(String type) {
	    return routes.get(type);
	}
	
	public void addRoute(String type, String path, Route route) {
	    routes.get(type).put(path, route);
	}
	
	public List<FilterPath> getBeforeFilters() {
	    return beforeFilters;
	}
	
	public void addBeforeFilter(String path, String acceptType, Filter filter) {
	    beforeFilters.add(new FilterPath(path, acceptType, filter));
	}
	
	public List<FilterPath> getAfterFilters() {
	    return afterFilters;
	}
	
	public void addAfterFilter(String path, String acceptType, Filter filter) {
	    afterFilters.add(new FilterPath(path, acceptType, filter));
	}
	
	/*
	 * Start the workers in the thread pool
	 */
	public void startWorkers() {
	    logger.info("Starting workers");
	    for (HttpWorker worker: threadPool.keySet()) {
            Thread workerThread = new Thread(worker);
            workerThread.start();
        }  
	}
	
	/*
	 * Listen on the specified port for incoming requests
	 * Request are added to the queue
	 */
    public void listen() {     
        try {
            serverSocket = new ServerSocket(portNumber);
            while (true) {
                Socket socket = serverSocket.accept();
                logger.debug("Incoming request");
                HttpTask task = new HttpTask(socket);
                queue.add(task);
            }
        } catch (IOException e) { // The server socket has been closed because of shutdown
            logger.info("Server socket closed, terminating");
        } catch (InterruptedException e) {
            logger.error(e);
        } finally {
            waitWorkers();
        }        

    }
    
    /*
     * Wait for the workers to terminate
     */
    private void waitWorkers() {
        logger.info("Waiting for worker threads to terminate...");
        ArrayList<HttpWorker> runningWorkers = new ArrayList<HttpWorker>();
        
        // Stop idle workers and keep track of the others
        synchronized (threadPool) {
            Iterator<Map.Entry<HttpWorker, Boolean>> it = threadPool.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<HttpWorker, Boolean> entry = it.next();
                HttpWorker worker = entry.getKey();
                boolean workerActive = entry.getValue();
                if (!workerActive) {
                    worker.interrupt();
                } else {
                    runningWorkers.add(worker);
                }
            }
        }
        
        // Wait for the active workers to terminate
        for (HttpWorker worker: runningWorkers) {
            try {
                worker.join();
                logger.debug(worker + " just terminated");
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
        logger.info("All workers have terminated, shutting down");
    }
	
	/*
	 * Shut down the server by closing the server socket
	 */
	public void stop() {
	    try {
	        active = false;
            serverSocket.close();
        } catch (IOException e) {
            logger.error(e);
        }
	    
	}
	
	public HttpTask readFromQueue() throws InterruptedException {
	    return queue.read();
	}
	
	public Path getRootDirectory() {
	    return this.rootDirectory;
	}
	
	/*
	 * Get the workers and their state in an html table
	 */
    public String getPoolHtml() {
        String res = "<table>" + 
                     "<tr>\n" +
                     "<th>Thread</th>\n" +
                     "<th>State</th>\n" +
                     "</tr>\n";
        synchronized (threadPool) {
            Iterator<Map.Entry<HttpWorker, Boolean>> it = threadPool.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<HttpWorker, Boolean> entry = it.next();
                HttpWorker worker = entry.getKey();
                String state = entry.getValue() ? worker.currentRequest() : "waiting";
                logger.debug(worker + " : " + entry.getValue());
                res += "<tr>\n" +
                     "<th>" + worker + "</th>\n" +
                     "<th>" + state + "</th>\n" +
                     "</tr>\n";
            }
        }
        return res + "</table>";
    }

    @Override
    public HttpTaskQueue getRequestQueue() {
        return this.queue;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void start(HttpWorker worker) {
        synchronized (threadPool) {
            threadPool.put(worker, true);
        }
        
    }

    @Override
    public void done(HttpWorker worker) {
        synchronized (threadPool) {
            threadPool.put(worker, false);
        }
        
    }

    @Override
    public void error(HttpWorker worker) {
        synchronized (threadPool) {
            threadPool.put(worker, false);
        }
        
    }
}
