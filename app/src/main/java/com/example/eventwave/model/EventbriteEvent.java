package com.example.eventwave.model;

import com.google.gson.annotations.SerializedName;

public class EventbriteEvent {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private EventbriteName name;

    @SerializedName("description")
    private EventbriteDescription description;

    @SerializedName("start")
    private EventbriteDateTime start;

    @SerializedName("venue")
    private EventbriteVenue venue;

    @SerializedName("logo")
    private EventbriteLogo logo;

    @SerializedName("category")
    private EventbriteCategory category;

    // Classes internes pour la structure imbriquée de l'API Eventbrite
    public static class EventbriteName {
        @SerializedName("text")
        public String text;
    }

    public static class EventbriteDescription {
        @SerializedName("text")
        public String text;
    }

    public static class EventbriteDateTime {
        @SerializedName("utc")
        public String utc;
    }

    public static class EventbriteVenue {
        @SerializedName("name")
        public String name;

        @SerializedName("latitude")
        public String latitude;

        @SerializedName("longitude")
        public String longitude;
    }

    public static class EventbriteLogo {
        @SerializedName("url")
        public String url;
    }

    public static class EventbriteCategory {
        @SerializedName("name")
        public String name;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return name != null ? name.text : "";
    }

    public String getDescription() {
        return description != null ? description.text : "";
    }

    public String getImageUrl() {
        return logo != null ? logo.url : "";
    }

    public String getCategory() {
        return category != null ? category.name : "";
    }

    public String getVenueName() {
        return venue != null ? venue.name : "";
    }

    public double getLatitude() {
        if (venue != null && venue.latitude != null) {
            try {
                return Double.parseDouble(venue.latitude);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }

    public double getLongitude() {
        if (venue != null && venue.longitude != null) {
            try {
                return Double.parseDouble(venue.longitude);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }

    public long getStartDate() {
        if (start != null && start.utc != null) {
            try {
                return java.time.Instant.parse(start.utc).toEpochMilli();
            } catch (Exception e) {
                return System.currentTimeMillis();
            }
        }
        return System.currentTimeMillis();
    }
} 