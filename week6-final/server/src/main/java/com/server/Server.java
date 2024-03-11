package com.server;

import com.server.context.RegistrationHandler;
import com.server.context.ServerHandler;
import com.server.context.TopFiveHandler;
import com.server.service.DatabaseJooc;
import com.server.service.UserAuthentication;
import com.sun.net.httpserver.*;
import java.security.KeyStore;
import java.util.concurrent.Executors;
import java.io.FileInputStream;
import java.net.InetSocketAddress;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLParameters;

public class Server {

    private Server() {

    }

    private static SSLContext myServerSSLContext() throws Exception {
        //System.out.println("Current Directory: " + System.getProperty("user.dir"));
        char[] passphrase = "1234567890".toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("keystore.jks"), passphrase);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, passphrase);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        SSLContext ssl = SSLContext.getInstance("TLS");
        ssl.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        return ssl;
    }

    private static SSLContext myServerSSLContext(String[] args) throws Exception {
        //System.out.println("Current Directory: " + System.getProperty("user.dir"));
        char[] passphrase = args[1].toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(args[0]), passphrase);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, passphrase);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        SSLContext ssl = SSLContext.getInstance("TLS");
        ssl.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        return ssl;
    }


    public static void main(String[] args) throws Exception {
        SSLContext sslContext = null;

        DatabaseJooc databaseJooc = new DatabaseJooc("jdbc:sqlite:database.db");
        //Restart from a fresh table
        databaseJooc.dropTable("messages");
        databaseJooc.dropTable("users");
        //Create new tables if ones don't exist
        databaseJooc.createMessagesTable();
        databaseJooc.createUsersTable();

        //Check if keystore is given inside arguments for automated testing
        if (args.length > 1) {
            sslContext = myServerSSLContext(args);
        } else {
            sslContext = myServerSSLContext();
        }
        
        UserAuthentication userAuthenticator = new UserAuthentication("/info", databaseJooc);

        //create the https server to port 8001 with default logger
        HttpsServer server = HttpsServer.create(new InetSocketAddress(8001), 0);
        server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
            public void configure (HttpsParameters params) {
            //InetSocketAddress remote = params.getClientAddress();
            SSLContext c = getSSLContext();
            SSLParameters sslparams = c.getDefaultSSLParameters();
            params.setSSLParameters(sslparams);
            }
        });

        server.setExecutor(Executors.newCachedThreadPool());

        HttpContext topFiveContext = server.createContext("/topfive", new TopFiveHandler(databaseJooc));
        topFiveContext.setAuthenticator(userAuthenticator);

        HttpContext infoContext = server.createContext("/info", new ServerHandler(databaseJooc));
        infoContext.setAuthenticator(userAuthenticator);

        server.createContext("/registration", new RegistrationHandler(userAuthenticator));
        
        server.setExecutor(null); 
        
        System.out.println("Hello init from server");

        server.start(); 
        

        
    }
}