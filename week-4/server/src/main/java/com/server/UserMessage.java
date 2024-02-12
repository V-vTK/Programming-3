package com.server;

import java.time.Instant;
import java.time.format.DateTimeParseException;

import org.json.JSONException;
import org.json.JSONObject;

public class UserMessage {
    private String locationName;
    private String locationDescription;
    private String locationCity;
    private long originalPostingTime;

    public UserMessage() {

    }

    public UserMessage(String locationName, String locationDescription, String locationCity, long originalPostingTime) {
        this.locationName = locationName;
        this.locationDescription = locationDescription;
        this.locationCity = locationCity;
        this.originalPostingTime = originalPostingTime;
    }

    public boolean inputJSONMessage(JSONObject jsonObject) {
        try {
            this.locationName = jsonObject.getString("locationName");
            this.locationDescription = jsonObject.getString("locationDescription");
            this.locationCity = jsonObject.getString("locationCity");
            try {
                long epochMilli = Instant.parse(jsonObject.getString("originalPostingTime")).toEpochMilli();;
                System.out.println("Epoch milliseconds: " + epochMilli);
                this.originalPostingTime = epochMilli;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid timestamp format" + e);
                return false;
            }
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

    public long getOriginalPostingTime() {
        return originalPostingTime;
    }

    public JSONObject exportJSONMessage() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("locationName", this.locationName);
            jsonObject.put("locationDescription", this.locationDescription);
            jsonObject.put("locationCity", this.locationCity);
            jsonObject.put("originalPostingTime", this.originalPostingTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;  
    }

}
