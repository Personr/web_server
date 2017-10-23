package edu.upenn.cis.cis455.m1.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Stub class for implementing the queue of HttpTasks
 */
public class HttpTaskQueue {
    static final Logger logger = LogManager.getLogger(HttpTaskQueue.class);	

	private final List<HttpTask> sharedQueue;
	private final int maxSize;

	public HttpTaskQueue(int maxSize) {
		this.sharedQueue = new ArrayList<HttpTask>();
		this.maxSize = maxSize;
	}
	
	public int getLength() {
	    return sharedQueue.size();
	}
	
	/*
	 * Add a task to the queue in a synchronized way
	 * Notify the waiting consumer threads that an element has been added
	 * If the queue is full, the task is thrown away
	 */
	public void add(HttpTask task) throws InterruptedException {
		logger.debug("Adding element to queue");
		if (sharedQueue.size() < maxSize) {
    		synchronized (sharedQueue) {
    		    logger.debug("Element added to queue");
    			sharedQueue.add(task);
    			sharedQueue.notify();
    		}
		} else {
		    logger.info("Request thrown away");
		}
	}
	
	/*
	 * Read a task from the queue in a synchronized way
	 * If the queue is empty, wait for an element to be added
	 */
	public HttpTask read() throws InterruptedException {
        logger.debug("Reading from queue");
        while (true) {
            synchronized (sharedQueue) {
                if (!sharedQueue.isEmpty()) {
                    HttpTask task =  sharedQueue.remove(0);
                    logger.debug("Element read from queue: " + task);
                    return task;
                } else
                    sharedQueue.wait();
            }
        }
    }
    
}
