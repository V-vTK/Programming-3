package com.server.model;

import java.time.Instant;
import java.time.format.DateTimeParseException;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class UserMessage {
    private String locationName;
    private String locationDescription;
    private String locationCity;
    private String originalPostingTime;
    private String originalPoster;
    private Double latitude;
    private Double longitude;
    private String locationCountry;
    private String locationStreetAddress;

    public UserMessage() {

    }

    public boolean isMessageValid() {
        return (locationName.length() > 1 &&
            locationDescription.length() > 1 &&
            locationCity.length() > 1 &&
            locationCountry.length() > 1 &&
            locationStreetAddress.length() > 1 && getOriginalPostingTime() > -1);
    }

    public String getMessageType() { //TODO for additional
        return "message";
    }


    private synchronized long timeToEpoch(String time) {
        return Instant.parse(time).toEpochMilli();
    }

    public void addOriginalPoster(String nickname) {
        this.originalPoster = nickname;
    }

    public String getLocationCity() {
        return locationCity;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public String getOriginalPoster() {
        return originalPoster;
    }

    public String getLocationName() {
        return locationName;
    }

    public long getOriginalPostingTime() {
        try {
            return timeToEpoch(originalPostingTime);
        } catch (DateTimeParseException e) {
            return -1;
        }
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getLocationCountry() {
        return locationCountry;
    }

    public String getLocationStreetAddress() {
        return locationStreetAddress;
    }

    public JsonObject getJson() {
        Gson gson = new Gson();
        String jsonString = gson.toJson(this);
        System.out.println(longitude + "BEFORE JSON CONV");
        System.out.println(jsonString + "JSON STRING");
        return gson.fromJson(jsonString, JsonObject.class);
    }

}
