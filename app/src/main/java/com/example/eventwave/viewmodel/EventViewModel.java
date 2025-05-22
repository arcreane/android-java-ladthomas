package com.example.eventwave.viewmodel;

import android.app.Application;
import android.location.Location;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.eventwave.model.Event;
import com.example.eventwave.repository.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EventViewModel extends AndroidViewModel {
    private final EventRepository repository;
    private final MutableLiveData<Location> currentLocation = new MutableLiveData<>();
    private final MutableLiveData<Double> searchRadius = new MutableLiveData<>(5.0);
    private final MutableLiveData<String> selectedCategory = new MutableLiveData<>("Tous");
    private final MutableLiveData<Boolean> showFavoritesOnly = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> historyEvents = new MutableLiveData<>(new ArrayList<>());
    private final LiveData<List<Event>> events;

    public EventViewModel(Application application) {
        super(application);
        repository = new EventRepository(application);
        events = repository.getAllEvents();
    }

    private double calculateDistance(Location location1, Location location2) {
        if (location1 == null || location2 == null) return Double.MAX_VALUE;
        return location1.distanceTo(location2) / 1000.0; // Convert to kilometers
    }

    private List<Event> filterEventsByDistance(List<Event> events, Location userLocation, double maxRadius) {
        if (userLocation == null || events == null) return events;
        
        return events.stream()
                .filter(event -> {
                    Location eventLocation = new Location("");
                    eventLocation.setLatitude(event.getLatitude());
                    eventLocation.setLongitude(event.getLongitude());
                    double distance = calculateDistance(userLocation, eventLocation);
                    event.setDistance(distance); // Assuming you have this field in Event
                    return distance <= maxRadius;
                })
                .sorted((e1, e2) -> Double.compare(e1.getDistance(), e2.getDistance()))
                .collect(Collectors.toList());
    }

    public LiveData<List<Event>> getEvents() {
        return events;
    }

    public LiveData<List<Event>> getFavoriteEvents() {
        return repository.getFavoriteEvents();
    }

    public LiveData<List<Event>> getHistoryEvents() {
        return repository.getHistoryEvents();
    }

    public void addToHistory(Event event) {
        List<Event> currentHistory = historyEvents.getValue();
        if (currentHistory != null) {
            if (!currentHistory.contains(event)) {
                currentHistory.add(0, event);
                if (currentHistory.size() > 50) { // Limiter à 50 événements
                    currentHistory = currentHistory.subList(0, 50);
                }
                historyEvents.setValue(currentHistory);
                repository.saveHistory(currentHistory);
            }
        }
    }

    public void clearHistory() {
        historyEvents.setValue(new ArrayList<>());
        repository.clearHistory();
    }

    public void toggleFavorite(Event event) {
        repository.toggleFavorite(event);
    }

    public void setCurrentLocation(Location location) {
        currentLocation.setValue(location);
        refreshEventsWithCurrentLocation();
    }

    public void setSearchRadius(double radius) {
        searchRadius.setValue(radius);
        refreshEventsWithCurrentLocation();
    }

    public void setSelectedCategory(String category) {
        selectedCategory.setValue(category);
        refreshEventsWithCurrentLocation();
    }

    public void toggleShowFavoritesOnly() {
        Boolean currentValue = showFavoritesOnly.getValue();
        showFavoritesOnly.setValue(currentValue != null ? !currentValue : true);
        refreshEventsWithCurrentLocation();
    }

    private void refreshEventsWithCurrentLocation() {
        Location location = currentLocation.getValue();
        Double radius = searchRadius.getValue();
        
        if (location != null && radius != null) {
            try {
                repository.refreshEvents(location);
            } catch (Exception e) {
                error.setValue("Erreur lors de la mise à jour des événements : " + e.getMessage());
            }
        }
    }

    public LiveData<Boolean> getIsLoading() {
        return repository.getIsLoading();
    }

    public LiveData<String> getError() {
        return error;
    }

    public void clearCache() {
        repository.clearCache();
    }

    public void refreshEvents() {
        Location location = currentLocation.getValue();
        repository.refreshEvents(location);
    }

    public void refreshHistory() {
        repository.refreshHistoryEvents();
    }

    public void setUserLocation(Location location) {
        currentLocation.setValue(location);
    }
} 