package com.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RegistrationHander implements HttpHandler {

    private UserAuthentication userContext;
    private SendResponse send;

    public RegistrationHander(UserAuthentication userContext) {
        this.userContext = userContext;
        this.send = new SendResponse();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            handlePostRequest(exchange);
        } else {
            send.sendErrorResponse(exchange, 400, "Not supported");
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        InputStream stream = exchange.getRequestBody();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String text = reader.lines().collect(Collectors.joining("\n"));
            String[] userInfo = text.split(":");
            if (userInfo.length == 2) {
                if (userContext.addUser(userInfo[0], userInfo[1])) {
                    send.sendResponse(exchange, 200, "Registration received");
                } else {
                    send.sendErrorResponse(exchange, 403, "Username not available");
                }
            } else {
                send.sendErrorResponse(exchange, 403, "Data not OK");
            }
            
        } finally {
            stream.close();
        }
    }
    
}
