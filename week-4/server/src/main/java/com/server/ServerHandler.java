package com.server;

import java.io.IOException;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ServerHandler implements HttpHandler {

    private SendResponse send;
    private ReceiveData receive;
    private DatabaseJooc database;

    public ServerHandler(DatabaseJooc databaseJooc) {
        this.database = databaseJooc;
        this.send = new SendResponse();
        this.receive = new ReceiveData();
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
        JSONObject json = receive.receiveJSONobject(exchange);
        System.out.println(json.toString());
        UserMessage newMessage = new UserMessage();
        if (newMessage.inputJSONMessage(json)) {
            database.addMessage(newMessage.getLocationName(), newMessage.getLocationDescription(), newMessage.getLocationCity(), newMessage.getOriginalPostingTime());
            send.sendResponse(exchange, 200, "Message received");
        } else {
            send.sendErrorResponse(exchange, 400, "Invalid JSON format");
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        send.SendJSONResponse(exchange, 200, database.exportTableToJSON("messages"));
    }

}
