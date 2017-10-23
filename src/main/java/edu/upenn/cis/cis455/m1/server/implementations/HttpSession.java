package edu.upenn.cis.cis455.m1.server.implementations;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Random;

import edu.upenn.cis.cis455.m2.server.interfaces.Session;

public class HttpSession extends Session {
    
    private String id;
    private long creationTime;
    private long lastAccessedTime;
    private boolean valid;
    private int maxInactiveInterval;
    private Map<String, Object> attributes;
    
    public HttpSession() {
        id = randomId();
        creationTime = System.currentTimeMillis();
        lastAccessedTime = System.currentTimeMillis();
        valid = true;
        maxInactiveInterval = -1;
        attributes = new HashMap<String, Object>();
    }
    
    private String randomId() {
        String id = "";
        while (id.length() < 15) {
            Random rn = new Random(System.currentTimeMillis());
            int randomNum =  rn.nextInt(256);
            id += Character.toString((char)randomNum);
        }
        return id;        
    }
    
    public boolean isValid() {
        return valid && !(System.currentTimeMillis() - lastAccessedTime > maxInactiveInterval);
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public long creationTime() {
        return creationTime;
    }

    @Override
    public long lastAccessedTime() {
        return lastAccessedTime;
    }

    @Override
    public void invalidate() {
        valid = false;
        
    }

    @Override
    public int maxInactiveInterval() {
        return maxInactiveInterval;
    }

    @Override
    public void maxInactiveInterval(int interval) {
        maxInactiveInterval = interval;
        
    }

    @Override
    public void access() {
        lastAccessedTime = System.currentTimeMillis();
        
    }

    @Override
    public void attribute(String name, Object value) {
        attributes.put(name, value);
        
    }

    @Override
    public Object attribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Set<String> attributes() {
        return attributes.keySet();
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
        
    }
}
