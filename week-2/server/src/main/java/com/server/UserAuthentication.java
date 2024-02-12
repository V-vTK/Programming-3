package com.server;

import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.BasicAuthenticator;

public class UserAuthentication extends BasicAuthenticator{
    private Map<String,String> users = null;

    public UserAuthentication() {
        super("info");
        this.users = new HashMap<>();
        users.put("dummy", "passwd");
    }

    @Override
    public boolean checkCredentials(String username, String password) {
        return (users.containsKey(username) && password.equals(users.get(username)));
    }

    public boolean addUser(String userName, String password) {
        if (!users.containsKey(userName)) {
            users.put(userName, password);
            return true;
        }
        return false;
    }
}
