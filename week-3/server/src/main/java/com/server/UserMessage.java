package com.server;

import org.json.JSONException;
import org.json.JSONObject;

public class UserMessage {
    private String locationName;
    private String locationDescription;
    private String locationCity;

    public UserMessage() {

    }

    public UserMessage(String locationName, String locationDescription, String locationCity) {
        this.locationName = locationName;
        this.locationDescription = locationDescription;
        this.locationCity = locationCity;
    }

    public boolean inputJSONMessage(JSONObject jsonObject) {
        try {
            this.locationName = jsonObject.getString("locationName");
            this.locationDescription = jsonObject.getString("locationDescription");
            this.locationCity = jsonObject.getString("locationCity");
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getLocationCity() {
        return locationCity;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public String getLocationName() {
        return locationName;
    }

    public JSONObject exportJSONMessage() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("locationName", this.locationName);
            jsonObject.put("locationDescription", this.locationDescription);
            jsonObject.put("locationCity", this.locationCity);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;  
    }

}
