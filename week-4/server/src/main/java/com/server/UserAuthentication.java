package com.server;

import org.json.JSONObject;

import com.sun.net.httpserver.BasicAuthenticator;

public class UserAuthentication extends BasicAuthenticator{
    private DatabaseJooc database;

    public UserAuthentication(String realm, DatabaseJooc databaseJooc) {
        super(realm);
        this.database = databaseJooc;
        database.addUser("dummy", "passwd", "dummy@example.com");
    }

    @Override
    public boolean checkCredentials(String username, String password) {
        return (database.checkPassword(username, password));
    }

    public boolean addUser(String username, String password, String email) {
        UserData newUser = new UserData(username, password, email);
        if (database.checkUsername(username) || newUser.isValid()) {
            database.addUser(username, password, email);
            return true;
        }
        return false;
    }

    public boolean addUser(JSONObject jsonObject) {
        UserData newUser = new UserData();
        if (newUser.inputJsonUserData(jsonObject)) {
            if (database.checkUsername(newUser.getUsername()) || !newUser.isValid()) {
                return false;
            }
            database.addUser(newUser.getUsername(), newUser.getPassword(), newUser.getEmail());
            return true;
        } else {
            return false;
        }
    }
}
