package edu.upenn.cis.cis455.m1.server.implementations;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.upenn.cis.cis455.exceptions.HaltException;
import edu.upenn.cis.cis455.handlers.Filter;
import edu.upenn.cis.cis455.handlers.Route;
import edu.upenn.cis.cis455.m1.server.HttpServer;
import edu.upenn.cis.cis455.m1.server.FilterPath;
import edu.upenn.cis.cis455.m1.server.interfaces.HttpRequestHandler;
import edu.upenn.cis.cis455.m1.server.interfaces.Request;
import edu.upenn.cis.cis455.m1.server.interfaces.Response;

public class RequestHandler implements HttpRequestHandler {
    final static Logger logger = LogManager.getLogger(RequestHandler.class);
    
    private Path rootDirectory;
    private HttpServer server;
    
    public RequestHandler(Path rootDirectory) {
        this.rootDirectory = rootDirectory;
    }
    
    public void setServer(HttpServer server) {
        this.server = server;
    }
    
    /*
     * If the path is illegal, throw a Forbidden HaltException
     */
    private void testForbidden(String pathInfo) throws HaltException {
        for (String part : pathInfo.split("/")) {
            if (part.equals("..")) {
                throw new HaltException(403, "Forbidden");
            }
        }
    }
    
    private boolean isSpecial(String pathInfo) {
        return pathInfo.equals("/control") || pathInfo.equals("/shutdown");
    }
    
    /*
     * Handle control and shutdown
     */
    private void handleSpecial(Request request, Response response) {
        if (request.pathInfo().equals("/control")) {
            String body = "<!DOCTYPE html>\n" +
                          "<html>\n" +
                          "<head>" +
                          "<title>Control panel</title>" +
                          "</head>" +
                          "<body>" +
                          "<h1>Welcome</h1>" +
                          "<h2>Thread pool</h2>" +
                          server.getPoolHtml() +
                          "<h2>Useful links</h2>" +
                          "<ul>" +
                          "<li><a href=\"second.html\">Link</a></li>" +
                          "<li><a href=\"/shutdown\">Shut down</a></li>" +
                          "</ul>" +
                          "</body>" +
                          "</html>";
            response.body(body);
        } else if (request.pathInfo().equals("/shutdown")) {
            server.stop();
            String body = "<!DOCTYPE html>\n" +
                          "<html>\n" +
                          "<head>" +
                          "<title>Shutdown</title>" +
                          "</head>" +
                          "<body>" +
                          "<h1>Shutting down...</h1>" +
                          "</body>" +
                          "</html>";
            response.body(body);
        }
        response.type("text/html");
    }
    
    private boolean match(String routePath, String requestPath) {
        routePath = routePath.replaceAll("\\*", "[^/]*");
        routePath = routePath.replaceAll(":name", "[^/]*");
        return requestPath.matches(routePath);
    }
    
    private Route findRoute(String pathInfo, String type) {
        if (server == null) {
            return null;
        }
        Iterator<Map.Entry<String, Route>> it = server.getRoutes(type).entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Route> entry = it.next();
            String routePath = entry.getKey();
            Route route = entry.getValue();
            if (match(routePath, pathInfo)) {
                return route;
            }
        }
        return null;
    }

    private void applyFilters(List<FilterPath> filters, Request request, Response response) throws Exception {
        for (FilterPath filterPath : filters) {
            String routePath = filterPath.getPath();
            String type = filterPath.getType();
            Filter filter = filterPath.getFilter();
            if ((type.equals("") || type.equals(request.requestMethod())) && 
                (routePath.equals("") || match(routePath, request.pathInfo()))) {
                filter.handle(request, response);
            }
        }
    }

    private void beforeFilters(Request request, Response response) throws Exception {
        if (server != null) {
            applyFilters(server.getBeforeFilters(), request, response);
        }      
    }

    private void afterFilters(Request request, Response response) throws Exception {
        if (server != null) {
            applyFilters(server.getAfterFilters(), request, response);
        }
    }

    @Override
    public void handle(Request request, Response response) throws HaltException {
        try {
            testForbidden(request.pathInfo());
            if (isSpecial(request.pathInfo())) {
                handleSpecial(request, response);
            } else {
                beforeFilters(request, response);
                
                Route route = findRoute(request.pathInfo(), request.requestMethod());
                if (route != null) {
                    logger.debug("Route set by user");
                    response.type("text/html");
                    route.handle(request, response);

                } else if (request.requestMethod().equals("GET") || request.requestMethod().equals("HEAD")) {
                    // Try to read a file
                    Path path = Paths.get(rootDirectory + request.pathInfo());
                    logger.debug("Fetching file: " + rootDirectory + request.pathInfo());
                    byte[] file = Files.readAllBytes(path);
                    logger.debug("File found");
                    response.contentLength(file.length);
                    if (!request.requestMethod().equals("HEAD")) {
                        response.bodyRaw(file);
                    }

                    // Set the appropriate MIME type according to the file
                    if (request.uri().endsWith(".html") || request.uri().endsWith(".htm")) {
                        response.type("text/html");
                    } else if (request.uri().endsWith(".txt")) {
                        response.type("text/plain");
                    } else if (request.uri().endsWith(".jpg") || request.uri().endsWith(".jpeg")) {
                        response.type("image/jpeg");
                    } else if (request.uri().endsWith(".gif")) {
                        response.type("image/gif");
                    } else if (request.uri().endsWith(".png")) {
                        response.type("image/png");
                    } else if (request.uri().endsWith(".ico")) {
                        response.type("image/x-icon");
                    } else if (request.uri().endsWith(".svg")) {
                        response.type("image/svg+xml");
                    } else if (request.uri().endsWith(".tif") || request.uri().endsWith(".tiff")) {
                        response.type("image/tiff");
                    } else if (request.uri().endsWith(".webp")) {
                        response.type("image/webp");
                    }
                }

            }
            response.status(200);
            afterFilters(request, response);


        } catch (IOException e) {
            // If the file couldn't be read, throw HaltException
            logger.debug("File couldn't be read: " + e);
            throw new HaltException(404, "Not found");
        } catch (HaltException e) {
            logger.debug("HaltException thrown: " + e.statusCode() + " " + e.body());
            throw e;

        } catch (Exception e) {
            logger.error(e);
            //TODO
        }
    }
}