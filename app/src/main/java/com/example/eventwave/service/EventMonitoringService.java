package com.example.eventwave.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import com.example.eventwave.model.Event;
import com.example.eventwave.repository.EventRepository;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EventMonitoringService extends Service {
    private static final long CHECK_INTERVAL = 30; // minutes
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private EventRepository repository;
    private NotificationService notificationService;
    private SharedPreferences preferences;
    private LocationManager locationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        repository = new EventRepository(getApplication());
        notificationService = new NotificationService(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startMonitoring();
        return START_STICKY;
    }

    private void startMonitoring() {
        scheduler.scheduleAtFixedRate(() -> {
            if (!preferences.getBoolean("notifications_enabled", false)) {
                return;
            }

            Location lastLocation = getLastKnownLocation();
            if (lastLocation == null) {
                return;
            }

            double radius = preferences.getFloat("search_radius", 5.0f);
            checkNearbyEvents(lastLocation, radius);
        }, 0, CHECK_INTERVAL, TimeUnit.MINUTES);
    }

    private void checkNearbyEvents(Location location, double radius) {
        List<Event> events = repository.getEventsNearby(location, radius).getValue();
        if (events != null) {
            for (Event event : events) {
                Location eventLocation = new Location("");
                eventLocation.setLatitude(event.getLatitude());
                eventLocation.setLongitude(event.getLongitude());
                
                float distance = location.distanceTo(eventLocation) / 1000; // en km
                if (distance <= radius) {
                    notificationService.showNearbyEventNotification(event, distance);
                }
            }
        }
    }

    private Location getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        Location lastKnownLocation = null;
        List<String> providers = locationManager.getProviders(true);

        for (String provider : providers) {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                if (lastKnownLocation == null || location.getTime() > lastKnownLocation.getTime()) {
                    lastKnownLocation = location;
                }
            }
        }

        return lastKnownLocation;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scheduler.shutdown();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
} 