package edu.upenn.cis.cis455;

import org.apache.logging.log4j.Level;
import static edu.upenn.cis.cis455.m1.server.WebServiceController.*;

public class WebServer {
    public static void main(String[] args) {
        org.apache.logging.log4j.core.config.Configurator.setLevel("edu.upenn.cis.cis455", Level.INFO);
        
        int port = 8080;
        String rootDirectory = "./www";
        if (args.length == 2) {
            port = Integer.parseInt(args[0]);
		    rootDirectory = args[1];
        } else if (args.length != 0) {
			System.out.println("Wrong number of arguments : 2 expected, " + args.length + " found");
			System.exit(0);
		}
		
		get("/a", (req, res) -> "a");
		port(port);
		staticFileLocation(rootDirectory);
		start();   
    }

}
