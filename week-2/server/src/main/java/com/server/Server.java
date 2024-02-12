package com.server;

import com.sun.net.httpserver.*;
import java.security.KeyStore;


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

    private static SSLContext myServerSSLContextARG(String[] args) throws Exception {
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

    //curl -k -d "dummyuser3:stillapassword" https://localhost:8001/registration -H "Content-Type: text/plain"
    //curl -u dummy:passwd -k -d "Nallikari" https://localhost:8001/info -H "Content-Type: text/plain"
    //curl -u dummy:passwd https://localhost:8001/info -H "Content-Type: text/plain" --insecure
    //https://localhost:8001/info and login dummy | passwd
    public static void main(String[] args) throws Exception {
        //Week-2 return
        //SSLContext sslContext = myServerSSLContextARG(args);
        SSLContext sslContext = myServerSSLContext();
        UserAuthentication userAuthenticator = new UserAuthentication();

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

        HttpContext infoContext = server.createContext("/info", new ServerHandler());
        infoContext.setAuthenticator(userAuthenticator);

        HttpContext registerContext = server.createContext("/registration", new RegistrationHander(userAuthenticator));
        
        server.setExecutor(null); 
        
        server.start(); 
    }
}


