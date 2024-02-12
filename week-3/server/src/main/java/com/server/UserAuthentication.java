package com.server;

import java.util.Hashtable;
import org.json.JSONObject;

import com.sun.net.httpserver.BasicAuthenticator;

public class UserAuthentication extends BasicAuthenticator{
    private Hashtable<String,UserData> users;

    public UserAuthentication(String realm) {
        super(realm);
        this.users = new Hashtable<>();
        users.put("dummy", new UserData("dummy", "passwd", "dummy@example.com"));
    }

    @Override
    public boolean checkCredentials(String username, String password) {
        return (users.containsKey(username) && password.equals(users.get(username).getPassword()));
    }

    public boolean addUser(String username, String password, String email) {
        if (!users.containsKey(username)) {
            users.put(username, new UserData(username, password, email));
            return true;
        }
        return false;
    }

    public boolean addUser(JSONObject jsonObject) {
        UserData newUser = new UserData();
        if (newUser.inputJsonUserData(jsonObject)) {
            if (users.containsKey(newUser.getUsername()) || !newUser.isValid()) {
                return false;
            }
            users.put(newUser.getUsername(), newUser);
            System.out.println(users.toString());
            return true;
        } else {
            return false;
        }
    }
}
