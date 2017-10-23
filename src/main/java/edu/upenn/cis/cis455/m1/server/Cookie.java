package edu.upenn.cis.cis455.m1.server;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Cookie {
    
    private String path;
    private String name;
    private String value;
    private int maxAge = -2;
    private boolean secured = false;
    private boolean httpOnly = false;
    
    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }
    
    public Cookie(String name, String value, int maxAge) { 
        this.name = name;
        this.value = value;
        this.maxAge = maxAge;
    }
    
    public Cookie(String name, String value, int maxAge, boolean secured) {
        this.name = name;
        this.value = value;
        this.maxAge = maxAge;
        this.secured = secured;
    }
    
    public Cookie(String name, String value, int maxAge, boolean secured, boolean httpOnly) {
        this.name = name;
        this.value = value;
        this.maxAge = maxAge;
        this.secured = secured;
        this.httpOnly = httpOnly;
    }
    
    public Cookie(String path, String name, String value) {
        this.path = path;
        this.name = name;
        this.value = value;
    }
    
    public Cookie(String path, String name, String value, int maxAge) {
        this.path = path;
        this.name = name;
        this.value = value;
        this.maxAge = maxAge;
    }
    
    public Cookie(String path, String name, String value, int maxAge, boolean secured) {
        this.path = path;
        this.name = name;
        this.value = value;
        this.maxAge = maxAge;
        this.secured = secured;
    }
    
    public Cookie(String path, String name, String value, int maxAge, boolean secured, boolean httpOnly) {
        this.path = path;
        this.name = name;
        this.value = value;
        this.maxAge = maxAge;
        this.secured = secured;
        this.httpOnly = httpOnly;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Cookie)) {
            return false;
        }
        Cookie c = (Cookie) o;
        if (path == null || c.path == null) {
            return path == c.path && name.equals(c.name);
        }
        return path.equals(c.path) && name.equals(c.name);
    }
    
    @Override
    public int hashCode() {
        return (name + ((path == null) ? "" : path)).hashCode();
    }
    
    @Override
    public String toString() {
        ZonedDateTime dateTime = ZonedDateTime.now();
        dateTime = dateTime.plusSeconds(maxAge);
        String expireDate = dateTime.format(DateTimeFormatter.RFC_1123_DATE_TIME);
        
        return "Set-Cookie: " + name + "=" + value + 
                ((path == null) ? "" : "; Path=" + path) + 
                ((maxAge == -2) ? "" : "; Expires=" + expireDate) + 
                (!secured ? "" : "; Secure") + 
                (!httpOnly ? "" : "; HttpOnly") + "\r\n";
    }
}
