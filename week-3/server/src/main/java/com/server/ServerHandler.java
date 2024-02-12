package com.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ServerHandler implements HttpHandler {

    private List<UserMessage> messages;
    private SendResponse send;
    private ReceiveData receive;

    public ServerHandler() {
        this.messages = new ArrayList<>();
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
        UserMessage newMessage = new UserMessage();
        if (newMessage.inputJSONMessage(json)) {
            messages.add(newMessage);
            send.sendResponse(exchange, 200, "Message received");
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        // Use JSON ARRAY!
        if (messages.isEmpty()) {
            send.sendResponse(exchange, 203, "No Content");
        } else {
            JSONArray jsonObject = new JSONArray();
            for (int i = 0; i < messages.size(); i++) {
                JSONObject message = messages.get(i).exportJSONMessage();
                jsonObject.put(message);
            }
            System.out.println(jsonObject.toString());
            send.SendJSONResponse(exchange, 200, jsonObject);
        }
    }

}
