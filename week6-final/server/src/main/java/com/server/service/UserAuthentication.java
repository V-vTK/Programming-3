package com.server.service;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.server.model.UserData;
import com.sun.net.httpserver.BasicAuthenticator;

public class UserAuthentication extends BasicAuthenticator{
    private DatabaseJooc database;

    public UserAuthentication(String realm, DatabaseJooc databaseJooc) {
        super(realm);
        this.database = databaseJooc;
        //database.addUser("dummy", "passwd", "dummy@example.com", "dummy_nickname");
    }

    @Override
    public boolean checkCredentials(String username, String password) {
        return (database.checkPassword(username, password));
    }

    public boolean addUser(JSONObject jsonObject) {
        synchronized (this) { // Stops duplicate users
            Gson gson = new Gson();
            UserData newUser = gson.fromJson(jsonObject.toString(), UserData.class);
            if (database.checkUsername(newUser.getUsername()) || !newUser.isValid()) {
                return false; 
            }
            database.addUser(newUser.getUsername(), newUser.getPassword(), newUser.getEmail(), newUser.getUserNickname());
            return true;
        }
    }
}
