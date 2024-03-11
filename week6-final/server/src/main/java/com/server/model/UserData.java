package com.server.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserData {
    private String username;

    private String password;

    private String email;
    
    private String userNickname;

    public JsonObject getJson() {
        Gson gson = new Gson();
        String jsonString = gson.toJson(this);
        return gson.fromJson(jsonString, JsonObject.class);
    }

    public Boolean isValid() {
        return (email.length() > 1 && username.length() > 1 && password.length() > 1 && userNickname.length() > 1);
    }
}
