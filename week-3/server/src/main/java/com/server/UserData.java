package com.server;

import org.json.JSONException;
import org.json.JSONObject;

public class UserData {
    private String username;
    private String password;
    private String email;

    public UserData() {

    }

    public UserData(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public boolean inputJsonUserData(JSONObject jsonObject) {
        try {
            this.username = jsonObject.getString("username");
            this.password = jsonObject.getString("password");
            this.email = jsonObject.getString("email");
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
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

    public JSONObject getJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", this.username);
        jsonObject.put("password", this.password);
        jsonObject.put("email", this.email);
        return jsonObject;
    }

    public Boolean isValid() {
        return (email.length() > 1 && username.length() > 1 && password.length() > 1);
    }
}
