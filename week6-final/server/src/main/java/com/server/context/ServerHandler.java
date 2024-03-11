package com.server.context;

import java.io.IOException;
import java.sql.SQLException;

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
    private DatabaseJooc dao;

    public ServerHandler(DatabaseJooc databaseJooc) {
        this.dao = databaseJooc;
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
            if (json.length() == 2) { // Visit
                handleVisitMessage(exchange, json);
            } else {
                UserMessage newUserMessage = gson.fromJson(json.toString(), UserMessage.class);
                if (newUserMessage.isMessageValid()) { // Valid
                    if (newUserMessage.isWeatherMessage()) { // Weather
                        handleWeatherMessage(newUserMessage);
                    }
                    if (newUserMessage.isUpdateMessage()) { // Update
                        handleUpdateMessage(exchange, newUserMessage);
                    } else { // Normal post
                    newUserMessage.addOriginalPoster(dao.getNickname(exchange.getPrincipal().getUsername()));
                    dao.addMessage(newUserMessage); 
                    send.sendResponse(exchange, 200, "Message received");
                    }
                } else {
                    send.sendErrorResponse(exchange, 400, "Invalid JSON message");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            send.sendErrorResponse(exchange, 400, "Invalid JSON format");
        }

    }

    private void handleVisitMessage(HttpExchange exchange, JSONObject json) throws IOException, SQLException {
        if (dao.addVisit(json.getInt("locationID"))) {
            dao.getMostVisitedPlaces();
            send.sendResponse(exchange, 200, "Visit received");
        } else {
        send.sendErrorResponse(exchange, 400, "Visit failed");
        }
    }
    

    private void handleWeatherMessage(UserMessage newUserMessage) throws IOException {
        String data = send.SendXLMPostTo(
            "http://localhost:4001/weather", 
            send.buildXlmString(newUserMessage.getLatitude(), newUserMessage.getLongitude())
            );
        newUserMessage.setWeather(data);
    }


    private void handleGetRequest(HttpExchange exchange) throws IOException {
        send.SendJSONResponse(exchange, 200, dao.exportTableToJSON("messages"));
    }

    private void handleUpdateMessage(HttpExchange exchange, UserMessage newUserMessage) throws IOException {
        try {
            String nickname = dao.getNickname(exchange.getPrincipal().getUsername());
            String originalPoster = dao.getMessagePoster(newUserMessage.getLocationID());
            
            //Is the user the original poster, and does the user update the message with his nickname
            if (newUserMessage.getOriginalPoster().equals(nickname) && originalPoster.equals(nickname)) {  
                dao.updateMessage(newUserMessage); 
                send.sendResponse(exchange, 200, "Message received");
            } else {
                send.sendErrorResponse(exchange, 400, "Invalid Update message");
            } 
        } catch (Exception e) {
            e.printStackTrace();
            send.sendErrorResponse(exchange, 400, "Invalid Update message");
        }
    }



}
