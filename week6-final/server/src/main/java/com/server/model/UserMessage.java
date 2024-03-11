package com.server.model;

import java.time.Instant;
import java.time.format.DateTimeParseException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserMessage {
    // Desc
    private Integer locationID;
    private String locationName;
    private String locationDescription;
    
    @Getter(AccessLevel.NONE)
    private String originalPostingTime;
    private String originalPoster;

    // Optional
    private Double latitude;
    private Double longitude;

    // Place
    private String locationCity;
    private String locationCountry;
    private String locationStreetAddress;
    
    //Feature 5
    private String weather;

    //Feature 7
    @SerializedName("updatereason") // for automated testing camelcase breaks the pipeline
    private String updateReason; 
    private String modified;


    public boolean isMessageValid() {
        return (locationName.length() > 1 &&
            locationDescription.length() > 1 &&
            locationCity.length() > 1 &&
            locationCountry.length() > 1 &&
            locationStreetAddress.length() > 1 && getOriginalPostingTime() > 1);
    }

    public boolean isWeatherMessage() {
        return ((weather != null) && (latitude != null) && (longitude != null));
    }

    public boolean isUpdateMessage() {
        return ((locationID != null) && (originalPoster != null)); 
    }


    private synchronized long timeToEpoch(String time) {
        return Instant.parse(time).toEpochMilli();
    }

    public void addOriginalPoster(String nickname) {
        this.originalPoster = nickname;
    }

    public long getOriginalPostingTime() {
        try {
            return timeToEpoch(originalPostingTime);
        } catch (DateTimeParseException e) {
            return -1;
        }
    }

    public JsonObject getJson() {
        Gson gson = new Gson();
        String jsonString = gson.toJson(this);
        return gson.fromJson(jsonString, JsonObject.class);
    }
}
