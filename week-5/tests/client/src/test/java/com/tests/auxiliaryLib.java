package com.tests;

import org.json.JSONObject;

public class auxiliaryLib {
    
    public static JSONObject generateJSONObject(JSONObject obj, String stringLoc, String stringDesc, String stringCity, String stringCountry, String stringStreet, String stringDate){

        genericJsonMessage(obj, stringLoc, stringDesc, stringCity, stringCountry, stringStreet, stringDate);

        return obj;

    }

    public static JSONObject generateJSONObject(JSONObject obj, String stringLoc, String stringDesc, String stringCity, String stringCountry, String stringStreet, String stringDate, double doubleLon, double doubleLat){

        genericJsonMessage(obj, stringLoc, stringDesc, stringCity, stringCountry, stringStreet, stringDate);

        obj.put("longitude", doubleLon);

        obj.put("latitude", doubleLat);

        return obj;

    }

    private static void genericJsonMessage (JSONObject obj, String stringLoc, String stringDesc, String stringCity, String stringCountry, String stringStreet, String stringDate){
        if(!stringLoc.isEmpty()){
            obj.put("locationName", stringLoc);
        }
        if(!stringDesc.isEmpty()){
            obj.put("locationDescription", stringDesc);
        }
        if(!stringCity.isEmpty()){
            obj.put("locationCity", stringCity);
        }
        if(!stringCountry.isEmpty()){
            obj.put("locationCountry", stringCountry);
        }
        if(!stringStreet.isEmpty()){
            obj.put("locationStreetAddress", stringStreet);
        }
        if(!stringDate.isEmpty()){
            obj.put("originalPostingTime", stringDate);
        }
    }

    public JSONObject generateSpecificJSONObject (JSONObject originalObject){

        JSONObject specificObject = new JSONObject();

        specificObject.put("sent", originalObject.getString("sent"));
        specificObject.put("nickname", originalObject.getString("nickname"));
        specificObject.put("latitude", originalObject.getDouble("latitude"));
        specificObject.put("longitude", originalObject.getDouble("longitude"));
        specificObject.put("dangertype", originalObject.getString("dangertype"));

        if(originalObject.has("areacode") && originalObject.has("phonenumber")){
            specificObject.put("areacode", originalObject.get("areacode"));
            specificObject.put("phonenumber", originalObject.get("phonenumber"));
        }

        //Feature 6 specific
        if(originalObject.has("weather")){
            specificObject.put("weather", originalObject.get("weather"));
        }

        //Feature 7 specific
        if(originalObject.has("description")){
            specificObject.put("description", originalObject.getString("description"));
        }
        if(originalObject.has("id")){
            specificObject.put("id", originalObject.getInt("id"));
        }

        //Feature 8 specific
        if(originalObject.has("updatereason")){
            specificObject.put("updatereason", originalObject.getString("updatereason"));
        }
        if(originalObject.has("modified")){
            specificObject.put("modified", originalObject.get("modified"));
        }

        return specificObject;
    }


}

