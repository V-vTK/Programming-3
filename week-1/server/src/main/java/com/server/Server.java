package com.server;

import com.sun.net.httpserver.*;
import java.net.InetSocketAddress;

public class Server {

    private Server() {
        
    }
    public static void main(String[] args) throws Exception {
        //create the http server to port 8001 with default logger
        HttpServer server = HttpServer.create(new InetSocketAddress(8001),0);

        //server.createContext("/help", new HelpHandler());
        //server.createContext("/info", new InfoHandler());
        server.createContext("/", new ServerHandler());
        server.setExecutor(null); 
        
        server.start(); 
    }
}

