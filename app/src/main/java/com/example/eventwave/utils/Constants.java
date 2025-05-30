package com.example.eventwave.utils;

public class Constants {
    // API Ticketmaster
    public static final String TICKETMASTER_API_KEY = "lNveRcgFldF1K60Ag7vfe8scoJw7xG3R";
    public static final String TICKETMASTER_BASE_URL = "https://app.ticketmaster.com/";
    
    // Configuration par défaut
    public static final int DEFAULT_SEARCH_RADIUS = 50; // en miles
    public static final int DEFAULT_EVENT_COUNT = 20;
    public static final String DEFAULT_COUNTRY_CODE = "US";
    public static final String DEFAULT_SORT = "date,asc";
    
    // Codes de pays supportés
    public static final String COUNTRY_FRANCE = "FR";
    public static final String COUNTRY_USA = "US";
    public static final String COUNTRY_CANADA = "CA";
    public static final String COUNTRY_UK = "GB";
    
    // Catégories d'événements
    public static final String CATEGORY_MUSIC = "Music";
    public static final String CATEGORY_SPORTS = "Sports";
    public static final String CATEGORY_ARTS = "Arts & Theatre";
    public static final String CATEGORY_FAMILY = "Family";
    public static final String CATEGORY_MISCELLANEOUS = "Miscellaneous";
    
    // Permissions
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    
    // Préférences
    public static final String PREF_SEARCH_RADIUS = "search_radius";
    public static final String PREF_NOTIFICATIONS_ENABLED = "notifications_enabled";
    public static final String PREF_HISTORY_EVENTS = "history_events";
    public static final String PREF_LAST_REFRESH = "last_refresh";
    
    // Messages d'erreur
    public static final String ERROR_NO_LOCATION = "Impossible d'obtenir votre position";
    public static final String ERROR_NO_EVENTS = "Aucun événement trouvé dans votre région";
    public static final String ERROR_API_FAILED = "Erreur lors de la récupération des événements";
    public static final String ERROR_NETWORK = "Problème de connexion réseau";
} 