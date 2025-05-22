package com.example.eventwave.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class EventbriteResponse {
    @SerializedName("events")
    public List<EventbriteEvent> events;

    @SerializedName("pagination")
    public Pagination pagination;

    public static class Pagination {
        @SerializedName("page_count")
        public int pageCount;

        @SerializedName("page_number")
        public int pageNumber;

        @SerializedName("page_size")
        public int pageSize;

        @SerializedName("has_more_items")
        public boolean hasMoreItems;
    }
    
    public static class Event extends EventbriteEvent {
        @SerializedName("venue")
        public EventbriteVenue venue;
        
        @SerializedName("category")
        public Category category;
        
        @SerializedName("ticket_availability")
        public TicketAvailability ticketAvailability;
        
        @SerializedName("ticket_classes")
        public List<TicketClass> ticketClasses;
    }
    
    public static class Category {
        @SerializedName("id")
        public String id;
        
        @SerializedName("name")
        public String name;
        
        @SerializedName("name_localized")
        public String nameLocalized;
        
        @SerializedName("short_name")
        public String shortName;
        
        @SerializedName("short_name_localized")
        public String shortNameLocalized;
    }
    
    public static class TicketAvailability {
        @SerializedName("has_available_tickets")
        public boolean hasAvailableTickets;
        
        @SerializedName("minimum_ticket_price")
        public Price minimumTicketPrice;
        
        @SerializedName("maximum_ticket_price")
        public Price maximumTicketPrice;
        
        @SerializedName("is_sold_out")
        public boolean isSoldOut;
        
        @SerializedName("start_sales_date")
        public EventbriteDateTime startSalesDate;
    }
    
    public static class Price {
        @SerializedName("currency")
        public String currency;
        
        @SerializedName("value")
        public int value;
        
        @SerializedName("major_value")
        public String majorValue;
        
        @SerializedName("display")
        public String display;
    }
    
    public static class TicketClass {
        @SerializedName("id")
        public String id;
        
        @SerializedName("name")
        public String name;
        
        @SerializedName("description")
        public String description;
        
        @SerializedName("free")
        public boolean isFree;
        
        @SerializedName("quantity_total")
        public int quantityTotal;
        
        @SerializedName("quantity_sold")
        public int quantitySold;
        
        @SerializedName("sales_start")
        public String salesStart;
        
        @SerializedName("sales_end")
        public String salesEnd;
    }
} 