package com.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ServerHandler implements HttpHandler {

    //private Map<String, String> posts;
    private List<String> posts;
    private SendResponse send;

    public ServerHandler() {
        //Map<String, String> posts = new HashMap<>();
        posts = new ArrayList<String>();
        this.send = new SendResponse();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            handlePostRequest(exchange);
        } else if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            handleGetRequest(exchange);
        } else {
            send.sendErrorResponse(exchange, 400, "Not supported");
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        InputStream stream = exchange.getRequestBody();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String text = reader.lines().collect(Collectors.joining("\n"));
            posts.add(text);
            send.sendResponse(exchange, 200, "Message received");
        } finally {
            stream.close();
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        if (posts.isEmpty()) {
            send.sendResponse(exchange, 200, "");
        } else {
            String response = String.join("\n", posts);
            send.sendResponse(exchange, 200, response);
        }
    }

}
