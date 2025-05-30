package com.example.eventwave.api;

import android.location.Location;
import android.util.Log;

import com.example.eventwave.model.Event;
import com.example.eventwave.model.TicketmasterEvent;
import com.example.eventwave.model.TicketmasterResponse;
import com.example.eventwave.utils.Constants;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TicketmasterService {
    private static final String TAG = "TicketmasterService";
    private static final boolean USE_MOCK_DATA = false; // Mettre à true pour utiliser les données fictives
    
    private final TicketmasterApi api;
    private final Retrofit retrofit;
    
    public TicketmasterService() {
        // Configuration du logging pour debug
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        // Configuration du client HTTP
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        
        // Configuration de Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.TICKETMASTER_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        api = retrofit.create(TicketmasterApi.class);
    }
    
    /**
     * Obtient la date d'aujourd'hui au format ISO pour l'API Ticketmaster
     */
    private String getTodayDateISO() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return now.format(formatter);
    }
    
    /**
     * Récupère les événements basés sur la localisation
     */
    public List<Event> getEvents(Location location) throws IOException {
        if (USE_MOCK_DATA) {
            return getMockEvents(location);
        }
        
        try {
            // Format de la géolocalisation pour Ticketmaster: "latitude,longitude"
            String latlong = location.getLatitude() + "," + location.getLongitude();
            
            // Détection automatique du code pays basé sur la localisation
            String countryCode = getCountryCodeFromLocation(location);
            
            Log.d(TAG, "Recherche d'événements pour: " + latlong + " dans le pays: " + countryCode);
            
            // Appel à l'API Ticketmaster
            Call<TicketmasterResponse> call = api.searchEvents(
                Constants.TICKETMASTER_API_KEY,
                latlong,
                String.valueOf(Constants.DEFAULT_SEARCH_RADIUS),
                "miles",
                Constants.DEFAULT_EVENT_COUNT,
                0,
                Constants.DEFAULT_SORT,
                null,
                countryCode,
                getTodayDateISO()
            );
            
            Response<TicketmasterResponse> response = call.execute();
            
            if (response.isSuccessful() && response.body() != null) {
                TicketmasterResponse ticketmasterResponse = response.body();
                List<TicketmasterEvent> ticketmasterEvents = ticketmasterResponse.getEvents();
                
                if (ticketmasterEvents != null && !ticketmasterEvents.isEmpty()) {
                    Log.d(TAG, "Événements trouvés: " + ticketmasterEvents.size());
                    return convertToEvents(ticketmasterEvents);
                } else {
                    Log.w(TAG, "Aucun événement trouvé pour " + countryCode + ", essai avec US");
                    // Si aucun événement trouvé, essayer avec US comme fallback
                    if (!countryCode.equals("US")) {
                        List<Event> usEvents = getEventsWithCountryCode(location, "US");
                        if (!usEvents.isEmpty()) {
                            return usEvents;
                        }
                    }
                    
                    // Si toujours aucun événement, essayer sans restriction de pays
                    Log.w(TAG, "Aucun événement trouvé avec US, essai sans restriction de pays");
                    List<Event> globalEvents = getEventsWithoutCountryRestriction(location);
                    if (!globalEvents.isEmpty()) {
                        return globalEvents;
                    }
                    
                    Log.w(TAG, "Aucun événement trouvé même sans restriction, utilisation des données fictives");
                    return getMockEvents(location);
                }
            } else {
                Log.e(TAG, "Erreur API: " + response.code() + " - " + response.message());
                if (response.errorBody() != null) {
                    Log.e(TAG, "Détails erreur: " + response.errorBody().string());
                }
                return getMockEvents(location);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception lors de l'appel API: " + e.getMessage(), e);
            return getMockEvents(location);
        }
    }
    
    /**
     * Détermine le code pays basé sur la localisation
     */
    private String getCountryCodeFromLocation(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        
        // Détection basique basée sur les coordonnées géographiques
        // États-Unis (approximatif)
        if (latitude >= 24.0 && latitude <= 71.0 && longitude >= -180.0 && longitude <= -66.0) {
            return "US";
        }
        // Canada (approximatif)
        else if (latitude >= 41.0 && latitude <= 84.0 && longitude >= -141.0 && longitude <= -52.0) {
            return "CA";
        }
        // Royaume-Uni (approximatif)
        else if (latitude >= 49.0 && latitude <= 61.0 && longitude >= -8.0 && longitude <= 2.0) {
            return "GB";
        }
        // France (approximatif)
        else if (latitude >= 41.0 && latitude <= 51.0 && longitude >= -5.0 && longitude <= 10.0) {
            return "FR";
        }
        // Europe de l'Ouest (approximatif)
        else if (latitude >= 35.0 && latitude <= 71.0 && longitude >= -10.0 && longitude <= 40.0) {
            return "FR"; // Utiliser FR pour l'Europe par défaut
        }
        // Par défaut, utiliser US car c'est là où Ticketmaster a le plus d'événements
        else {
            return "US";
        }
    }
    
    /**
     * Récupère les événements avec un code pays spécifique
     */
    private List<Event> getEventsWithCountryCode(Location location, String countryCode) throws IOException {
        try {
            String latlong = location.getLatitude() + "," + location.getLongitude();
            
            Call<TicketmasterResponse> call = api.searchEvents(
                Constants.TICKETMASTER_API_KEY,
                latlong,
                String.valueOf(Constants.DEFAULT_SEARCH_RADIUS),
                "miles",
                Constants.DEFAULT_EVENT_COUNT,
                0,
                Constants.DEFAULT_SORT,
                null,
                countryCode,
                getTodayDateISO()
            );
            
            Response<TicketmasterResponse> response = call.execute();
            
            if (response.isSuccessful() && response.body() != null) {
                TicketmasterResponse ticketmasterResponse = response.body();
                List<TicketmasterEvent> ticketmasterEvents = ticketmasterResponse.getEvents();
                
                if (ticketmasterEvents != null && !ticketmasterEvents.isEmpty()) {
                    Log.d(TAG, "Événements trouvés avec " + countryCode + ": " + ticketmasterEvents.size());
                    return convertToEvents(ticketmasterEvents);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la recherche avec " + countryCode + ": " + e.getMessage());
        }
        
        return new ArrayList<>();
    }
    
    /**
     * Récupère les événements sans restriction de code pays
     */
    private List<Event> getEventsWithoutCountryRestriction(Location location) throws IOException {
        try {
            String latlong = location.getLatitude() + "," + location.getLongitude();
            
            Call<TicketmasterResponse> call = api.searchEventsWithoutCountry(
                Constants.TICKETMASTER_API_KEY,
                latlong,
                String.valueOf(Constants.DEFAULT_SEARCH_RADIUS),
                "miles",
                Constants.DEFAULT_EVENT_COUNT,
                0,
                Constants.DEFAULT_SORT,
                null,
                getTodayDateISO()
            );
            
            Response<TicketmasterResponse> response = call.execute();
            
            if (response.isSuccessful() && response.body() != null) {
                TicketmasterResponse ticketmasterResponse = response.body();
                List<TicketmasterEvent> ticketmasterEvents = ticketmasterResponse.getEvents();
                
                if (ticketmasterEvents != null && !ticketmasterEvents.isEmpty()) {
                    Log.d(TAG, "Événements trouvés sans restriction de pays: " + ticketmasterEvents.size());
                    return convertToEvents(ticketmasterEvents);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la recherche sans restriction de pays: " + e.getMessage());
        }
        
        return new ArrayList<>();
    }
    
    /**
     * Convertit les événements Ticketmaster en objets Event
     */
    private List<Event> convertToEvents(List<TicketmasterEvent> ticketmasterEvents) {
        List<Event> events = new ArrayList<>();
        
        for (TicketmasterEvent tmEvent : ticketmasterEvents) {
            try {
                Event event = new Event(
                    tmEvent.id,
                    tmEvent.getEventName(),
                    "Événement " + tmEvent.getCategory() + " à " + tmEvent.getCityName(),
                    tmEvent.getImageUrl(),
                    mapCategory(tmEvent.getCategory()),
                    tmEvent.getVenueName() + ", " + tmEvent.getCityName(),
                    tmEvent.getLatitude(),
                    tmEvent.getLongitude(),
                    tmEvent.getStartDateMillis(),
                    false
                );
                events.add(event);
            } catch (Exception e) {
                Log.e(TAG, "Erreur lors de la conversion de l'événement: " + e.getMessage());
            }
        }
        
        return events;
    }
    
    /**
     * Mappe les catégories Ticketmaster vers nos catégories
     */
    private String mapCategory(String ticketmasterCategory) {
        if (ticketmasterCategory == null) return "Événement";
        
        String category = ticketmasterCategory.toLowerCase();
        
        if (category.contains("music") || category.contains("concert")) {
            return "Musique";
        } else if (category.contains("sports") || category.contains("sport")) {
            return "Sport";
        } else if (category.contains("arts") || category.contains("theatre") || category.contains("theater")) {
            return "Théâtre";
        } else if (category.contains("family") || category.contains("miscellaneous")) {
            return "Famille";
        } else {
            return "Événement";
        }
    }
    
    /**
     * Données fictives pour les tests
     */
    private List<Event> getMockEvents(Location location) {
        List<Event> events = new ArrayList<>();
        
        // Événements de musique
        events.add(new Event(
            "tm1",
            "Concert de Jazz - Miles Davis Tribute",
            "Un hommage exceptionnel au légendaire Miles Davis avec les meilleurs musiciens de jazz français.",
            "",
            "Musique",
            "Olympia",
            location.getLatitude() + 0.005,
            location.getLongitude() + 0.005,
            System.currentTimeMillis() + 86400000, // dans 1 jour
            false
        ));
        
        events.add(new Event(
            "tm2",
            "Festival Rock - Les Légendes",
            "Trois jours de rock avec les plus grands groupes français et internationaux.",
            "",
            "Musique",
            "Stade de France",
            location.getLatitude() - 0.01,
            location.getLongitude() + 0.01,
            System.currentTimeMillis() + 172800000, // dans 2 jours
            false
        ));
        
        // Événements sportifs
        events.add(new Event(
            "tm3",
            "Match de Football - PSG vs OM",
            "Le classique du football français dans une ambiance électrique.",
            "",
            "Sport",
            "Parc des Princes",
            location.getLatitude() + 0.02,
            location.getLongitude() + 0.02,
            System.currentTimeMillis() + 259200000, // dans 3 jours
            false
        ));
        
        events.add(new Event(
            "tm4",
            "Tournoi de Tennis - Masters 1000",
            "Les meilleurs joueurs mondiaux s'affrontent dans ce tournoi prestigieux.",
            "",
            "Sport",
            "AccorHotels Arena",
            location.getLatitude() - 0.02,
            location.getLongitude() - 0.02,
            System.currentTimeMillis() + 345600000, // dans 4 jours
            false
        ));
        
        // Événements théâtre
        events.add(new Event(
            "tm5",
            "Pièce de Théâtre - Cyrano de Bergerac",
            "La célèbre pièce d'Edmond Rostand dans une mise en scène moderne et captivante.",
            "",
            "Théâtre",
            "Comédie-Française",
            location.getLatitude() + 0.015,
            location.getLongitude() - 0.015,
            System.currentTimeMillis() + 432000000, // dans 5 jours
            false
        ));
        
        return events;
    }
    
    /**
     * Méthode de test pour vérifier l'API Ticketmaster
     */
    public void testApiConnection() {
        Log.d(TAG, "Test de l'API Ticketmaster");
        
        try {
            // Test avec New York comme localisation (plus d'événements disponibles)
            String latlong = "40.7128,-74.0060"; // New York City
            
            Call<TicketmasterResponse> call = api.searchEvents(
                Constants.TICKETMASTER_API_KEY,
                latlong,
                "25",
                "miles",
                5,
                0,
                Constants.DEFAULT_SORT,
                null,
                "US", // Utiliser US pour New York
                getTodayDateISO()
            );
            
            Log.d(TAG, "URL de test: " + call.request().url());
            
            call.enqueue(new retrofit2.Callback<TicketmasterResponse>() {
                @Override
                public void onResponse(Call<TicketmasterResponse> call, Response<TicketmasterResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        TicketmasterResponse ticketmasterResponse = response.body();
                        List<TicketmasterEvent> events = ticketmasterResponse.getEvents();
                        if (events != null) {
                            Log.d(TAG, " Test API réussi - " + events.size() + " événements trouvés");
                            for (int i = 0; i < Math.min(3, events.size()); i++) {
                                TicketmasterEvent event = events.get(i);
                                Log.d(TAG, "Événement " + (i+1) + ": " + event.getEventName() + " à " + event.getCityName());
                            }
                        } else {
                            Log.w(TAG, " Test API - Aucun événement trouvé");
                        }
                    } else {
                        Log.e(TAG, " Test API échoué - Code: " + response.code());
                        try {
                            if (response.errorBody() != null) {
                                Log.e(TAG, "Erreur: " + response.errorBody().string());
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Erreur lecture réponse: " + e.getMessage());
                        }
                    }
                }
                
                @Override
                public void onFailure(Call<TicketmasterResponse> call, Throwable t) {
                    Log.e(TAG, " Test API échoué - Exception: " + t.getMessage(), t);
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, " Exception lors du test API: " + e.getMessage(), e);
        }
    }
} 