package com.example.eventwave.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.example.eventwave.api.EventService;
import com.example.eventwave.dao.EventDao;
import com.example.eventwave.database.EventDatabase;
import com.example.eventwave.model.Event;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EventRepository {
    private final EventDao eventDao;
    private final EventService eventService;
    private final Executor executor;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final SharedPreferences preferences;
    private final Gson gson;

    public EventRepository(Application application) {
        EventDatabase db = EventDatabase.getInstance(application);
        this.eventDao = db.eventDao();
        this.eventService = new EventService();
        this.executor = Executors.newFixedThreadPool(4);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(application);
        this.gson = new Gson();
    }

    public LiveData<List<Event>> getAllEvents() {
        return eventDao.getAllEvents();
    }

    public LiveData<List<Event>> getFavoriteEvents() {
        return eventDao.getFavoriteEvents();
    }

    public LiveData<List<Event>> getEventsByCategory(String category) {
        return eventDao.getEventsByCategory(category);
    }

    public LiveData<List<Event>> getEventsNearby(Location location, double radius) {
        // Convertir le rayon en degrés (approximatif)
        double radiusInDegrees = radius / 111.0;
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        
        double minLat = lat - radiusInDegrees;
        double maxLat = lat + radiusInDegrees;
        double minLon = lon - radiusInDegrees / Math.cos(Math.toRadians(lat));
        double maxLon = lon + radiusInDegrees / Math.cos(Math.toRadians(lat));
        
        return eventDao.getEventsInArea(minLat, maxLat, minLon, maxLon);
    }

    public LiveData<List<Event>> getFavoriteEventsNearby(Location location, double radius) {
        double radiusInDegrees = radius / 111.0;
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        
        double minLat = lat - radiusInDegrees;
        double maxLat = lat + radiusInDegrees;
        double minLon = lon - radiusInDegrees / Math.cos(Math.toRadians(lat));
        double maxLon = lon + radiusInDegrees / Math.cos(Math.toRadians(lat));
        
        return eventDao.getFavoriteEventsInArea(minLat, maxLat, minLon, maxLon);
    }

    public LiveData<List<Event>> getEventsByCategoryNearby(String category, Location location, double radius) {
        double radiusInDegrees = radius / 111.0;
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        
        double minLat = lat - radiusInDegrees;
        double maxLat = lat + radiusInDegrees;
        double minLon = lon - radiusInDegrees / Math.cos(Math.toRadians(lat));
        double maxLon = lon + radiusInDegrees / Math.cos(Math.toRadians(lat));
        
        return eventDao.getEventsByCategoryInArea(category, minLat, maxLat, minLon, maxLon);
    }

    public void refreshEvents(Location location) {
        isLoading.postValue(true);
        
        if (location == null) {
            error.postValue("Impossible d'obtenir votre position");
            isLoading.postValue(false);
            return;
        }

        executor.execute(() -> {
            try {
                List<Event> events = eventService.getEvents(location);
                eventDao.deleteAllEvents();
                eventDao.insertEvents(events);
                error.postValue(null);
                
                // Vérifier si ce sont des données fictives (id commence par "1")
                if (events.size() > 0) {
                    boolean usingMockData = false;
                    for (Event event : events) {
                        if (event.getId().equals("1") || event.getId().equals("2") || 
                            event.getId().equals("3") || event.getId().equals("4") || 
                            event.getId().equals("5")) {
                            usingMockData = true;
                            break;
                        }
                    }
                    
                    if (usingMockData) {
                        error.postValue("Mode démo : données fictives utilisées car l'API n'est pas disponible");
                    }
                }
            } catch (IOException e) {
                error.postValue("Erreur lors de la récupération des événements : " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    public void toggleFavorite(Event event) {
        executor.execute(() -> {
            event.setFavorite(!event.isFavorite());
            eventDao.update(event);
        });
    }

    public void saveHistory(List<Event> events) {
        String json = gson.toJson(events);
        preferences.edit().putString("history_events", json).apply();
    }

    public List<Event> getHistory() {
        String json = preferences.getString("history_events", null);
        if (json != null) {
            Type type = new TypeToken<List<Event>>(){}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }

    public void clearHistory() {
        preferences.edit().remove("history_events").apply();
    }

    public void clearCache() {
        executor.execute(() -> {
            eventDao.deleteAllEvents();
            preferences.edit()
                .remove("history_events")
                .remove("last_refresh")
                .apply();
        });
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void refreshHistoryEvents() {
        // Récupère les événements de l'historique
        isLoading.postValue(true);
        executor.execute(() -> {
            try {
                // Logique pour récupérer l'historique
                isLoading.postValue(false);
            } catch (Exception e) {
                error.postValue("Erreur lors de la récupération de l'historique : " + e.getMessage());
                isLoading.postValue(false);
            }
        });
    }

    public LiveData<List<Event>> getHistoryEvents() {
        // Pour l'instant, on retourne juste tous les événements
        return eventDao.getAllEvents();
    }

    public LiveData<List<Event>> getEvents() {
        // Pour compatibilité avec le ViewModel
        return getAllEvents();
    }
} 