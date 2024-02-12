package com.tests;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;



/**
 * Test client for O3 tests
 */
public final class TestClient {

    String username;
    String password;
    String auth;
    String fullAddress;
    static Client client;

    private static final int CONNECT_TIMEOUT = 10 * 1000;
	private static final int REQUEST_TIMEOUT = 30 * 1000;

    private static final String USER_AGENT = "Mozilla/5.0";

    public TestClient(String address){

        client = new Client(address);
        System.out.println("Test client for week 1 created");

    }

    public TestClient(String keystore, String address, String newUser, String newPassword) {

        client = new Client(keystore, address);
        username = newUser;
        password = newPassword;
        auth = username + ":" + password;

    }

    public synchronized int testConnection() throws IOException, URISyntaxException{

        int responseCode = 200;

        fullAddress = client.getServerAddress();
        fullAddress += client.getMessageContext();

        URL url = new URI(fullAddress).toURL();

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		responseCode = con.getResponseCode();

        return responseCode;
    }

    public synchronized int testMessage(String message) throws IOException, URISyntaxException {
        int responseCode = 400;
        byte[] msgBytes;
        fullAddress = client.getServerAddress();
        fullAddress += client.getMessageContext();

        System.out.println(fullAddress);

        URL url = new URI(fullAddress).toURL();

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
	
        msgBytes = message.getBytes("UTF-8");
        con.setRequestProperty("Content-Type", "text/plain");

		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Length", String.valueOf(msgBytes.length));

		OutputStream writer = con.getOutputStream();
		writer.write(msgBytes);
		writer.close();


		responseCode = con.getResponseCode();

        return responseCode;
    }



    public synchronized String getMessages() throws IOException, URISyntaxException {
        int responseCode = 400;

        fullAddress = client.getServerAddress();
        fullAddress += client.getMessageContext();
        
        URL url = new URI(fullAddress).toURL();

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "text/plain");


		responseCode = con.getResponseCode();

        ArrayList<String> response = new ArrayList<String>();
        String input;
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));

        while ((input = in.readLine()) != null) {
            response.add(input);
        }

        String result = response.get(response.size()-1);

        return result;
    }

    /**
     * Main
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {

        
        // client.setupClient(args[0], args[1]);



    }

}
