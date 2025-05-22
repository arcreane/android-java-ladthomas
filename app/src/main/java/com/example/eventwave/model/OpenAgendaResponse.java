package com.example.eventwave.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OpenAgendaResponse {
    @SerializedName("events")
    public List<OpenAgendaEvent> events;
    
    @SerializedName("total")
    public int total;
    
    @SerializedName("offset")
    public int offset;
    
    @SerializedName("limit")
    public int limit;
    
    // Classe interne pour représenter un événement d'OpenAgenda
    public static class OpenAgendaEvent {
        @SerializedName("uid")
        public String id;
        
        @SerializedName("slug")
        public String slug;
        
        @SerializedName("title")
        public String title;
        
        @SerializedName("description")
        public String description;
        
        @SerializedName("longDescription")
        public String longDescription;
        
        @SerializedName("image")
        public OpenAgendaImage image;
        
        @SerializedName("locationName")
        public String locationName;
        
        @SerializedName("location")
        public OpenAgendaLocation location;
        
        @SerializedName("timings")
        public List<OpenAgendaTiming> timings;
        
        @SerializedName("registration")
        public String registration;
        
        @SerializedName("conditions")
        public String conditions;
        
        @SerializedName("city")
        public String city;
        
        @SerializedName("department")
        public String department;
        
        @SerializedName("region")
        public String region;
        
        @SerializedName("country")
        public String country;
        
        @SerializedName("keywords")
        public List<String> keywords;
        
        public String getId() {
            return id;
        }
        
        public String getTitle() {
            return title;
        }
        
        public String getDescription() {
            return description != null ? description : (longDescription != null ? longDescription : "Pas de description");
        }
        
        public String getImageUrl() {
            return image != null && image.base != null ? image.base : "";
        }
        
        public String getVenueName() {
            return locationName != null ? locationName : "Lieu non spécifié";
        }
        
        public double getLatitude() {
            return location != null ? location.latitude : 0.0;
        }
        
        public double getLongitude() {
            return location != null ? location.longitude : 0.0;
        }
        
        public long getStartDate() {
            if (timings != null && !timings.isEmpty() && timings.get(0) != null) {
                return timings.get(0).begin;
            }
            return System.currentTimeMillis();
        }
    }
    
    public static class OpenAgendaImage {
        @SerializedName("base")
        public String base;
        
        @SerializedName("variants")
        public List<String> variants;
    }
    
    public static class OpenAgendaLocation {
        @SerializedName("latitude")
        public double latitude;
        
        @SerializedName("longitude")
        public double longitude;
    }
    
    public static class OpenAgendaTiming {
        @SerializedName("begin")
        public long begin;
        
        @SerializedName("end")
        public long end;
    }
} 