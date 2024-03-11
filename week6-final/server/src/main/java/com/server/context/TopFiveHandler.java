package com.server.context;

import java.io.IOException;
import java.sql.SQLException;

import com.server.service.DatabaseJooc;
import com.server.service.SendResponse;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


public class TopFiveHandler implements HttpHandler {

    private SendResponse send;
    private DatabaseJooc dao;

    public TopFiveHandler(DatabaseJooc databaseJooc) {
        this.send = new SendResponse();
        this.dao = databaseJooc;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            try {
                send.SendJSONResponse(exchange, 200, dao.getMostVisitedPlaces());
            } catch (IOException | SQLException e) {
                send.sendErrorResponse(exchange, 500, "Server error");
                e.printStackTrace();
            }
        } else {
            send.sendErrorResponse(exchange, 400, "Not supported");
        }
    }


    
}




