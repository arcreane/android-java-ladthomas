package com.example.eventwave.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TicketmasterResponse {
    @SerializedName("_embedded")
    public Embedded embedded;
    
    @SerializedName("_links")
    public Links links;
    
    @SerializedName("page")
    public Page page;
    
    public static class Embedded {
        @SerializedName("events")
        public List<TicketmasterEvent> events;
    }
    
    public static class Links {
        @SerializedName("self")
        public Link self;
        
        @SerializedName("next")
        public Link next;
        
        @SerializedName("prev")
        public Link prev;
        
        @SerializedName("first")
        public Link first;
        
        @SerializedName("last")
        public Link last;
        
        public static class Link {
            @SerializedName("href")
            public String href;
        }
    }
    
    public static class Page {
        @SerializedName("size")
        public int size;
        
        @SerializedName("totalElements")
        public int totalElements;
        
        @SerializedName("totalPages")
        public int totalPages;
        
        @SerializedName("number")
        public int number;
    }
    
    // Méthode utilitaire pour obtenir la liste des événements
    public List<TicketmasterEvent> getEvents() {
        if (embedded != null && embedded.events != null) {
            return embedded.events;
        }
        return null;
    }
} 