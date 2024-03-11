package com.server.context;

import java.io.IOException;
import org.json.JSONObject;

import com.server.service.ReceiveData;
import com.server.service.SendResponse;
import com.server.service.UserAuthentication;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RegistrationHandler implements HttpHandler {

    private UserAuthentication userContext;
    private SendResponse send;
    private ReceiveData receive;

    public RegistrationHandler(UserAuthentication userContext) {
        this.userContext = userContext;
        this.send = new SendResponse();
        this.receive = new ReceiveData();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            handlePostRequestJson(exchange);
        } else {
            send.sendErrorResponse(exchange, 400, "Not supported");
        }
    }

    private void handlePostRequestJson(HttpExchange exchange) throws IOException { 
        JSONObject json = receive.receiveJSONobject(exchange);
        if (userContext.addUser(json)) {
            send.sendResponse(exchange, 200, "JSON received and processed successfully");
        } else {
            send.sendResponse(exchange, 400, "Data invalid");
        }
    }

    
}
