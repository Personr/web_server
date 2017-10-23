package edu.upenn.cis.cis455.m1.server.implementations;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;

import edu.upenn.cis.cis455.m1.server.Cookie;
import edu.upenn.cis.cis455.m2.server.interfaces.Response;

public class HttpResponse extends Response {
    
    /*
     * Return the actual body length
     */
    public int bodyLength() {
        if (body == null) {
            return 0;
        } else {
            return body.length;
        }
    }

    @Override
    public String getHeaders() {
        ZonedDateTime dateTime = ZonedDateTime.now();
        String date = dateTime.format(DateTimeFormatter.RFC_1123_DATE_TIME);
        
        // Add "custom" headers
        String res = "";
        Iterator<Map.Entry<String, String>> it = headers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            res += entry.getKey() + ": " + entry.getValue() + "\r\n";
        }
        
        // Add cookies
        for (Cookie cookie : cookies) {
            res += cookie.toString();
        }
        
        // Add other headers
        return res + "Date: " + date + "\r\n" + 
            "Content-Type: " + contentType + "\r\n" + 
            "Connection: close\r\n" +
            "Content-Length: " + this.contentLength() + "\r";
    }

    @Override
    public void header(String header, String value) {
        headers.put(header, value);
        
    }

    @Override
    public void redirect(String location) {
        String body = "<!DOCTYPE html>\n" +
                          "<html>\n" +
                          "<head>" +
                          "<title>Redirect</title>" +
                          "</head>" +
                          "<body>" +
                          "Sending you to " + location +
                          "</body>" +
                          "</html>";
        body(body);
        header("Location", location);
        type("text/html");
        statusCode = 301;
    }

    @Override
    public void redirect(String location, int httpStatusCode) {
        redirect(location);
        statusCode = httpStatusCode;
        
    }

    @Override
    public void cookie(String name, String value) {
        cookies.add(new Cookie(name, value));
        
    }

    @Override
    public void cookie(String name, String value, int maxAge) {
        cookies.add(new Cookie(name, value, maxAge));
        
    }

    @Override
    public void cookie(String name, String value, int maxAge, boolean secured) {
        cookies.add(new Cookie(name, value, maxAge, secured));
        
    }

    @Override
    public void cookie(String name, String value, int maxAge, boolean secured, boolean httpOnly) {
        cookies.add(new Cookie(name, value, maxAge, secured, httpOnly));
        
    }

    @Override
    public void cookie(String path, String name, String value) {
        cookies.add(new Cookie(path, name, value));
        
    }

    @Override
    public void cookie(String path, String name, String value, int maxAge) {
        cookies.add(new Cookie(path, name, value, maxAge));
        
    }

    @Override
    public void cookie(String path, String name, String value, int maxAge, boolean secured) {
        cookies.add(new Cookie(path, name, value, maxAge, secured));
        
    }

    @Override
    public void cookie(String path, String name, String value, int maxAge, boolean secured, boolean httpOnly) {
        cookies.add(new Cookie(path, name, value, maxAge, secured, httpOnly));
        
    }

    @Override
    public void removeCookie(String name) {
        cookies.add(new Cookie(name, "", 0));
        
    }

    @Override
    public void removeCookie(String path, String name) {
        cookies.add(new Cookie(path, name, "", 0));
        
    }
}
