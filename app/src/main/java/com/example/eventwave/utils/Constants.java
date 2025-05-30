package com.example.eventwave.utils;

public class Constants {
    // API Ticketmaster - UTILISÉES dans TicketmasterService
    public static final String TICKETMASTER_API_KEY = "lNveRcgFldF1K60Ag7vfe8scoJw7xG3R";
    public static final String TICKETMASTER_BASE_URL = "https://app.ticketmaster.com/";
    
    // Configuration par défaut - UTILISÉES dans TicketmasterService  
    public static final int DEFAULT_SEARCH_RADIUS = 50; // en miles
    public static final int DEFAULT_EVENT_COUNT = 20;
    public static final String DEFAULT_SORT = "date,asc";
    
    // Permissions - UTILISÉE dans MainActivity
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
} 