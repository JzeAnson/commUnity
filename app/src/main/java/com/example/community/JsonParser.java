package com.example.community;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {

    // Parse a single JSON object
    private HashMap<String, String> parseJsonObject(JSONObject object) {
        HashMap<String, String> dataList = new HashMap<>();
        try {
            // Extract name, latitude, and longitude
            String name = object.getString("name");
            String latitude = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lat");
            String longitude = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lng");

            // Extract place_id
            String placeId = object.getString("place_id");

            // Extract the first type from the "types" array
            JSONArray typesArray = object.getJSONArray("types");
            String type = typesArray.length() > 0 ? typesArray.getString(0) : "unknown";

            // Extract opening hours (if available)
            String openingHours = parseOpeningHours(object);

            // Extract contact number (if available)
            String contactNumber = parseContactNumber(object);

            // Put extracted data into the HashMap
            dataList.put("name", name);
            dataList.put("lat", latitude);
            dataList.put("lng", longitude);
            dataList.put("type", type); // Add type to the HashMap
            dataList.put("place_id", placeId); // Add place_id to the HashMap
            dataList.put("opening_hours", openingHours); // Add formatted opening hours
            dataList.put("contact_number", contactNumber); // Add contact number
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    // Method to parse the opening hours
    private String parseOpeningHours(JSONObject object) {
        StringBuilder hours = new StringBuilder();
        try {
            // Check if opening hours are available
            if (object.has("opening_hours")) {
                JSONArray weekdayText = object.getJSONObject("opening_hours").getJSONArray("weekday_text");
                for (int i = 0; i < weekdayText.length(); i++) {
                    String dayHours = weekdayText.getString(i);

                    // Check for and remove unexpected characters (if any)
                    dayHours = dayHours.replaceAll("[^\\x20-\\x7E]", ""); // Removes non-ASCII characters

                    hours.append(dayHours).append("\n");
                }
            } else {
                hours.append("No opening hours available.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            hours.append("Error parsing opening hours.");
        }
        return hours.toString();
    }

    // Method to parse the contact number

    private String parseContactNumber(JSONObject object) {
        String contactNumber = "No contact number available."; // Default message
        try {
            // Check if contact number is available in the JSON object
            if (object.has("contact_number")) {
                contactNumber = object.getString("contact_number");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contactNumber;
    }


    // Parse a JSON array
    private List<HashMap<String, String>> parseJsonArray(JSONArray jsonArray) {
        List<HashMap<String, String>> dataList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                HashMap<String, String> data = parseJsonObject((JSONObject) jsonArray.get(i));
                dataList.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return dataList;
    }

    // Parse the overall JSON result
    public List<HashMap<String, String>> parseResult(JSONObject object) {
        JSONArray jsonArray = null;
        try {
            jsonArray = object.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseJsonArray(jsonArray);
    }
}
