package com.server.service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import org.json.JSONArray;

import com.sun.net.httpserver.HttpExchange;

public class SendResponse {

    public void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    public void sendErrorResponse(HttpExchange exchange, int statusCode, String errorMessage) throws IOException {
        // Implemented for code readability
        sendResponse(exchange, statusCode, errorMessage);
    }

    public void SendJSONResponse(HttpExchange exchange, int statusCode, JSONObject jsonObject) throws IOException {
        byte[] responseBytes = jsonObject.toString().getBytes();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(responseBytes);
        outputStream.flush();
        outputStream.close();
    }

    public void SendJSONResponse(HttpExchange exchange, int statusCode, JSONArray jsonObject) throws IOException {
        byte[] responseBytes = jsonObject.toString().getBytes();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(responseBytes);
        outputStream.flush();
        outputStream.close();
    }
}
