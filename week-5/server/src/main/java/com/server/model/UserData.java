package com.server.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class UserData {
    private String username;

    private String password;

    private String email;
    
    private String userNickname;

    public UserData() {

    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
    
    public String getNickName() {
        return userNickname;
    }

    public JsonObject getJson() {
        Gson gson = new Gson();
        String jsonString = gson.toJson(this);
        return gson.fromJson(jsonString, JsonObject.class);
    }

    public Boolean isValid() {
        return (email.length() > 1 && username.length() > 1 && password.length() > 1 && userNickname.length() > 1);
    }
}
