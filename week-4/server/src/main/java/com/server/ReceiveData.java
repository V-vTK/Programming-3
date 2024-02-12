package com.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

public class ReceiveData {

    public String receivePlainText(HttpExchange exchange) throws IOException {
        InputStream stream = exchange.getRequestBody();
        String text = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            text = reader.lines().collect(Collectors.joining("\n"));
        }
        return text; 
    }

    public JSONObject receiveJSONobject(HttpExchange exchange) {
        try {
            InputStream requestBody = exchange.getRequestBody();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody))) {
                String requestBodyString = reader.lines().collect(Collectors.joining("\n"));
                return new JSONObject(requestBodyString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
