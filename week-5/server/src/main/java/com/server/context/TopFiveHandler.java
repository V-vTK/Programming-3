package com.server.context;

import java.io.IOException;

import com.server.service.DatabaseJooc;
import com.server.service.SendResponse;
import com.server.service.UserAuthentication;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


public class TopFiveHandler implements HttpHandler {

    private UserAuthentication userContext;
    private SendResponse send;
    private DatabaseJooc dao;

    public TopFiveHandler(DatabaseJooc databaseJooc, UserAuthentication userContext) {
        this.userContext = userContext;
        this.send = new SendResponse();
        this.dao = databaseJooc;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            send.sendResponse(exchange, 200, "This is top 5");
        } else {
            send.sendErrorResponse(exchange, 400, "Not supported");
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException{
        send.sendResponse(exchange, 200, "This is top 5"); //TODO dao to search for top5
    }
    
}




