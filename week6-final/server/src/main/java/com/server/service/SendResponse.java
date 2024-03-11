package com.server.service;


import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.json.JSONObject;
import org.json.XML;
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

    public String SendXLMPostTo(String url, String xmlContent) throws IOException {
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(url)).header("Content-Type", "application/xml")
            .POST(HttpRequest.BodyPublishers.ofString(xmlContent)).build();

            HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            JSONObject weather = XML.toJSONObject(response.body()).getJSONObject("weather");

            return weather.getDouble("temperature") + " " + weather.getString("Unit");

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "Could not get weather data";
    }

    public String buildXlmString(Double latitude, Double longitude) {
        return String.format(Locale.US, "<coordinates><latitude>%f</latitude><longitude>%f</longitude></coordinates>", latitude, longitude);
    } //<coordinates><latitude>28.23333</latitude><longitude>10.23344</longitude></coordinates>

}

