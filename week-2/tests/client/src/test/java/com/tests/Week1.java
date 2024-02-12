package com.tests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;

/**
 * Unit tests
 */
public class Week1 {


    private static TestClient testClient = null;
    private static TestSettings testSettings = null;

    Week1(){
        testSettings = new TestSettings();
        TestSettings.readSettingsXML("testconfigw1.xml");
        testClient = new TestClient(testSettings.getServerAddress());
        System.out.println("initialized week 1 tests");
    }

    @Test
    @BeforeAll
    @DisplayName("Setting up the test environment")
    public static void initialize() {

    }

    @Test
    @Order(1)
    @DisplayName("Testing server connection")
    void testHTTPServerConnection() throws IOException, URISyntaxException {
        System.out.println("Testing server connection");
        int result = testClient.testConnection();
        assertTrue(result == 200 || result == 204);
    }

    @Test
    @Order(2)
    @DisplayName("Testing sending any message to server, should return back the same message")
    void testSendMessage() throws IOException, URISyntaxException {
        System.out.println("Testing sending a message to server");
        String message = "Oulun Tuomiokirkko";
        int result = testClient.testMessage(message);
        assertTrue(result == 200 || result == 204);
        String response = testClient.getMessages();

        //search for a specific response in a string

        Boolean found = false;

        found = response.contains(message);

        assertTrue(found);
    }

    @Test
    @Order(3)
    @DisplayName("Testing characters")
    void testCharacters() throws IOException, URISyntaxException {
        System.out.println("Testing characters");
        String message = "åäö";
        int result = testClient.testMessage(message);
        assertTrue(result == 200 || result == 204);
        String response = testClient.getMessages();

        //search for a specific response in a string

        Boolean found = false;

        found = response.contains(message);

        assertTrue(found);
    }

}
