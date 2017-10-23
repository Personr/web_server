package edu.upenn.cis.cis455.m1.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import edu.upenn.cis.cis455.TestHelper;
import edu.upenn.cis.cis455.exceptions.HaltException;
import edu.upenn.cis.cis455.m1.server.HttpIoHandler;
import edu.upenn.cis.cis455.m1.server.HttpTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Producer extends Thread {

	static final Logger logger = LogManager.getLogger(Producer.class);	

    private final HttpTaskQueue queue;
    
    public Producer(HttpTaskQueue queue) {
		this.queue = queue;
	}
	
	public void run() {
		//Adding 10 elements onto queue.
		for (int i = 0; i < 10; i++) {
			try {			
				queue.add(new HttpTask(null));
			} catch (InterruptedException ex) {
				logger.error("Interrupt Exception in Producer thread.");
			}
		}
	}
	
}