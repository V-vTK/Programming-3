package com.server.context;

import java.io.IOException;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.server.model.UserMessage;
import com.server.service.DatabaseJooc;
import com.server.service.ReceiveData;
import com.server.service.SendResponse;
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

    private synchronized void handlePostRequest(HttpExchange exchange) throws IOException {
        JSONObject json = receive.receiveJSONobject(exchange);
        System.out.println(json.toString());
        Gson gson = new Gson();
        try {
            UserMessage newUserMessage = gson.fromJson(json.toString(), UserMessage.class);
            if (newUserMessage.isMessageValid()) {
                newUserMessage.addOriginalPoster(database.getNickname(exchange.getPrincipal().getUsername()));
                database.addMessage(newUserMessage);
                System.out.println(newUserMessage.getOriginalPostingTime() + " " + "here is the time"); 
                send.sendResponse(exchange, 200, "Message received");
            } else {
                send.sendErrorResponse(exchange, 400, "Invalid JSON format");
            }
        } catch (Exception e) {
            send.sendErrorResponse(exchange, 400, "Invalid JSON format");
        }

    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        send.SendJSONResponse(exchange, 200, database.exportTableToJSON("messages"));
    }

}
