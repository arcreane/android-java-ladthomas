package com.example.eventwave.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TicketmasterEvent {
    @SerializedName("id")
    public String id;
    
    @SerializedName("name")
    public String name;
    
    @SerializedName("url")
    public String url;
    
    @SerializedName("images")
    public List<Image> images;
    
    @SerializedName("dates")
    public Dates dates;
    
    @SerializedName("_embedded")
    public Embedded embedded;
    
    @SerializedName("classifications")
    public List<Classification> classifications;
    
    @SerializedName("priceRanges")
    public List<PriceRange> priceRanges;
    
    public static class Image {
        @SerializedName("ratio")
        public String ratio;
        
        @SerializedName("url")
        public String url;
        
        @SerializedName("width")
        public int width;
        
        @SerializedName("height")
        public int height;
        
        @SerializedName("fallback")
        public boolean fallback;
    }
    
    public static class Dates {
        @SerializedName("start")
        public Start start;
        
        @SerializedName("timezone")
        public String timezone;
        
        @SerializedName("status")
        public Status status;
        
        public static class Start {
            @SerializedName("localDate")
            public String localDate;
            
            @SerializedName("localTime")
            public String localTime;
            
            @SerializedName("dateTime")
            public String dateTime;
            
            @SerializedName("dateTBD")
            public boolean dateTBD;
            
            @SerializedName("dateTBA")
            public boolean dateTBA;
            
            @SerializedName("timeTBA")
            public boolean timeTBA;
            
            @SerializedName("noSpecificTime")
            public boolean noSpecificTime;
        }
        
        public static class Status {
            @SerializedName("code")
            public String code;
        }
    }
    
    public static class Embedded {
        @SerializedName("venues")
        public List<Venue> venues;
        
        public static class Venue {
            @SerializedName("name")
            public String name;
            
            @SerializedName("type")
            public String type;
            
            @SerializedName("id")
            public String id;
            
            @SerializedName("test")
            public boolean test;
            
            @SerializedName("url")
            public String url;
            
            @SerializedName("locale")
            public String locale;
            
            @SerializedName("timezone")
            public String timezone;
            
            @SerializedName("city")
            public City city;
            
            @SerializedName("country")
            public Country country;
            
            @SerializedName("address")
            public Address address;
            
            @SerializedName("location")
            public Location location;
            
            public static class City {
                @SerializedName("name")
                public String name;
            }
            
            public static class Country {
                @SerializedName("name")
                public String name;
                
                @SerializedName("countryCode")
                public String countryCode;
            }
            
            public static class Address {
                @SerializedName("line1")
                public String line1;
                
                @SerializedName("line2")
                public String line2;
            }
            
            public static class Location {
                @SerializedName("longitude")
                public String longitude;
                
                @SerializedName("latitude")
                public String latitude;
            }
        }
    }
    
    public static class Classification {
        @SerializedName("primary")
        public boolean primary;
        
        @SerializedName("segment")
        public Segment segment;
        
        @SerializedName("genre")
        public Genre genre;
        
        @SerializedName("subGenre")
        public SubGenre subGenre;
        
        public static class Segment {
            @SerializedName("id")
            public String id;
            
            @SerializedName("name")
            public String name;
        }
        
        public static class Genre {
            @SerializedName("id")
            public String id;
            
            @SerializedName("name")
            public String name;
        }
        
        public static class SubGenre {
            @SerializedName("id")
            public String id;
            
            @SerializedName("name")
            public String name;
        }
    }
    
    public static class PriceRange {
        @SerializedName("type")
        public String type;
        
        @SerializedName("currency")
        public String currency;
        
        @SerializedName("min")
        public double min;
        
        @SerializedName("max")
        public double max;
    }
    
    // Méthodes utilitaires pour extraire les données
    public String getEventName() {
        return name != null ? name : "Événement sans nom";
    }
    
    public String getEventDate() {
        if (dates != null && dates.start != null) {
            if (dates.start.localDate != null && dates.start.localTime != null) {
                return dates.start.localDate + " " + dates.start.localTime;
            } else if (dates.start.localDate != null) {
                return dates.start.localDate;
            }
        }
        return "Date non spécifiée";
    }
    
    public String getVenueName() {
        if (embedded != null && embedded.venues != null && !embedded.venues.isEmpty()) {
            return embedded.venues.get(0).name;
        }
        return "Lieu non spécifié";
    }
    
    public String getCityName() {
        if (embedded != null && embedded.venues != null && !embedded.venues.isEmpty()) {
            Embedded.Venue venue = embedded.venues.get(0);
            if (venue.city != null) {
                return venue.city.name;
            }
        }
        return "Ville non spécifiée";
    }
    
    public String getImageUrl() {
        if (images != null && !images.isEmpty()) {
            // Chercher une image de bonne qualité
            for (Image image : images) {
                if (image.width >= 640 && image.height >= 360) {
                    return image.url;
                }
            }
            // Si pas d'image de bonne qualité, prendre la première
            return images.get(0).url;
        }
        return "";
    }
    
    public double getLatitude() {
        if (embedded != null && embedded.venues != null && !embedded.venues.isEmpty()) {
            Embedded.Venue venue = embedded.venues.get(0);
            if (venue.location != null && venue.location.latitude != null) {
                try {
                    return Double.parseDouble(venue.location.latitude);
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }
        }
        return 0.0;
    }
    
    public double getLongitude() {
        if (embedded != null && embedded.venues != null && !embedded.venues.isEmpty()) {
            Embedded.Venue venue = embedded.venues.get(0);
            if (venue.location != null && venue.location.longitude != null) {
                try {
                    return Double.parseDouble(venue.location.longitude);
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }
        }
        return 0.0;
    }
    
    public String getCategory() {
        if (classifications != null && !classifications.isEmpty()) {
            Classification classification = classifications.get(0);
            if (classification.segment != null) {
                return classification.segment.name;
            }
        }
        return "Événement";
    }
    
    public long getStartDateMillis() {
        if (dates != null && dates.start != null && dates.start.dateTime != null) {
            try {
                return java.time.Instant.parse(dates.start.dateTime).toEpochMilli();
            } catch (Exception e) {
                return System.currentTimeMillis();
            }
        }
        return System.currentTimeMillis();
    }
} 