package edu.upenn.cis.cis455.m1.server;

import edu.upenn.cis.cis455.handlers.Filter;

public class FilterPath {
    
    private String path;
    private String type;
    private Filter filter;
    
    public FilterPath(String path, String type, Filter filter) {
        this.path = path;
        this.type = type;
        this.filter = filter;
    }
    
    public String getType() {
        return type;
    } 
    
    public String getPath() {
        return path;
    }
    
    public Filter getFilter() {
        return filter;
    }
    
}
