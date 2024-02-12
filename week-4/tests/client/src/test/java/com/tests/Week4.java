package com.tests;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;

public class Week4 {
    
    private static TestClient testClient = null;
    private static TestSettings testSettings = null;


    Week4(){
        testSettings = new TestSettings();
        TestSettings.readSettingsXML("testconfigw4.xml");
        testClient = new TestClient(testSettings.getCertificate(), testSettings.getServerAddress(), testSettings.getNick(), testSettings.getPassword());

    }

    @Test
    @BeforeAll
    @DisplayName("Setting up the test environment")
    public static void initialize() {
        System.out.println("initialized week 4 tests");
    }
    
    @Test
    @AfterAll
    public static void teardown() {
        System.out.println("Testing finished.");
    }

    @Test 
    @Order(1)
    @DisplayName("Testing server connection")
    void testHTTPServerConnection() throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        System.out.println("Testing server connection");
        int result = testClient.testHTTPSConnection();
        assertTrue(result > 1);
    }

    @Test 
    @Order(2)
    @DisplayName("Testing registering an user")
    void testRegisterUser() throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        System.out.println("Testing registering an user");
        int result = testClient.testRegisterUserJSON("jokurandom", "jokurandompsw", "joku@random.com");
        System.out.println(result);
        assertTrue(200 <= result && result <= 299);
    }

    @Test 
    @Order(3)
    @DisplayName("Testing registering same user again - must fail")
    void testRegisterUserAgain() throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        System.out.println("Testing registering same user again - must fail");
        testClient.testRegisterUserJSON("randomi1", "randomi1", "random@random.com");
        int result = testClient.testRegisterUserJSON("randomi1", "randomi1", "random@random.com");
        System.out.println(result);
        assertFalse(200 <= result && result <= 299);
    }

    @Test 
    @Order(4)
    @DisplayName("Testing sending message to server")
    void testSendMessage() throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        System.out.println("Testing sending message to server");
        testClient.testRegisterUserJSON(testSettings.getNick(), testSettings.getPassword(), testSettings.getEmail());
        JSONObject obj = new JSONObject();


        ZonedDateTime now =ZonedDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd'T'HH:mm:ss.SSSX");

        String dateText = now.format(formatter);

        obj.put("locationName", "Kaivopuisto");
        obj.put("locationDescription", "joku puisto");
        obj.put("locationCity", "Helsingfors");
        obj.put("originalPostingTime", dateText);


        int result = testClient.testJSONHTTPSMessage(obj);
        System.out.println(result);
        assertTrue(200 <= result && result <= 299);
        String response = testClient.getHTTPSmessages();
        JSONArray obj2 = new JSONArray(response);
        System.out.println(response);
        System.out.println("object is" + obj2);
        //Jokin joukko jsonobjekteja, joista pitää tunnistaa lähetetty objekti...
        boolean isSame = false;
        JSONObject obj3 = new JSONObject();
        for(int i=0; i<obj2.length(); i++){
            obj3 = obj2.getJSONObject(i);

            System.out.println(obj3);
            if(obj.similar(obj3))
            {isSame = true; break;}
        }

        assertTrue(isSame);

    }

    @Test 
    @Order(5)
    @DisplayName("Testing characters")
    void testCharacters() throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        System.out.println("Testing characters in message");
        testClient.testRegisterUserJSON(testSettings.getNick(), testSettings.getPassword(), testSettings.getEmail());
        JSONObject obj = new JSONObject();

        ZonedDateTime now =ZonedDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd'T'HH:mm:ss.SSSX");

        String dateText = now.format(formatter);

        obj.put("locationName", "Kilpisjärvi");
        obj.put("locationDescription", "yksi Suomen pohjoisimmista järvistä");
        obj.put("locationCity", "emmäätiiäonkotääkaupunki");
        obj.put("originalPostingTime", dateText);

        int result = testClient.testJSONHTTPSMessage(obj);
        System.out.println(result);
        assertTrue(200 <= result && result <= 299);
        String response = testClient.getHTTPSmessages();
        JSONArray obj2 = new JSONArray(response);
        System.out.println(response);
        System.out.println("object is" + obj2);
        //Jokin joukko jsonobjekteja, joista pitää tunnistaa lähetetty objekti...
        boolean isSame = false;
        JSONObject obj3 = new JSONObject();
        for(int i=0; i<obj2.length(); i++){
            obj3 = obj2.getJSONObject(i);

            System.out.println(obj3);
            if(obj.similar(obj3))
            {isSame = true; break;}
        }

        assertTrue(isSame);
    }


    @Test 
    @Order(7)
    @DisplayName("Testing faulty time")
    void testFaultyTime() throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        System.out.println("Testing time in message");
        testClient.testRegisterUserJSON(testSettings.getNick(), testSettings.getPassword(), testSettings.getEmail());
        JSONObject obj = new JSONObject();

        ZonedDateTime now =ZonedDateTime.now(ZoneId.of("UTC"));

        obj.put("locationName", "Kaivopuisto");
        obj.put("locationDescription", "joku puisto");
        obj.put("locationCity", "Helsingfors");
        obj.put("originalPostingTime", now);

        int result = testClient.testJSONHTTPSMessage(obj);
        System.out.println(result);
        assertFalse(200 <= result && result <= 299);
        System.out.println("Test failed, time format was wrong, make sure that you check that the time is correct in your code");
        
    }


    @Test 
    @Order(8)
    @DisplayName("Sending empty string to registration - must fail")
    void testRegisterRubbish() throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        System.out.println("Testing sending empty string to registration");
        int result = testClient.testRegisterUserJSON("", "", "");
        System.out.println(result);
        assertFalse(200 <= result && result <= 299);

    }

    @Test 
    @Order(9)
    @DisplayName("Sending GET to registration - must fail")
    void testRegisterGet() throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        System.out.println("Sending GET to registration - must fail");
        int result = testClient.testRegisterGet();
        System.out.println(result);
        assertFalse(200 <= result && result <= 299);

    }
    
}