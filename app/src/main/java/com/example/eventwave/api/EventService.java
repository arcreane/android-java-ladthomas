package com.example.eventwave.api;

import android.location.Location;
import android.util.Log;

import com.example.eventwave.model.Event;

import java.io.IOException;
import java.util.List;

public class EventService {
    private static final String TAG = "EventService";
    private final TicketmasterService ticketmasterService;
    
    public EventService() {
        this.ticketmasterService = new TicketmasterService();
    }
    
    /**
     * Récupère les événements depuis l'API Ticketmaster
     */
    public List<Event> getEvents(Location location) throws IOException {
        Log.d(TAG, "Récupération des événements via Ticketmaster API");
        return ticketmasterService.getEvents(location);
    }
    
    /**
     * Test de connexion à l'API Ticketmaster
     */
    public void testApiConnection() {
        Log.d(TAG, "Test de connexion à l'API Ticketmaster");
        ticketmasterService.testApiConnection();
    }
} 