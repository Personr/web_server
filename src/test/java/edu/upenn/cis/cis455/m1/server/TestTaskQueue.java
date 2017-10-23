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

import org.apache.logging.log4j.Level;

public class TestTaskQueue {
    @Before
    public void setUp() {
        org.apache.logging.log4j.core.config.Configurator.setLevel("edu.upenn.cis.cis455", Level.DEBUG);
    }
    
    @Test
    public void testTaskQueue() throws IOException, InterruptedException {
        HttpTaskQueue sharedQueue = new HttpTaskQueue(4);

		//Creating a producer thread
		Producer producerObj= new Producer(sharedQueue);
		Thread prodThread = new Thread(producerObj);

		//Creating consumer thread.
		Consumer consumerObj= new Consumer(sharedQueue);
		Thread consThread = new Thread(consumerObj);
		
		//Creating consumer thread.
		Consumer consumerObj2= new Consumer(sharedQueue);
		Thread consThread2 = new Thread(consumerObj2);

		//Starting both producer and consumer thread.
		prodThread.start();
		consThread.start();
		consThread2.start();
		
		Thread.sleep(1000);
		assertTrue(sharedQueue.getLength() == 0);
    }

    
    @After
    public void tearDown() {}
}
