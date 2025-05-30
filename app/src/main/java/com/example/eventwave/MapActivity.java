package com.example.eventwave;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventwave.model.Event;
import com.example.eventwave.viewmodel.EventViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private EventViewModel viewModel;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private Map<Marker, Event> markerEventMap = new HashMap<>();
    private boolean isMapCentered = false; // Flag pour éviter le double centrage
    private List<Event> pendingEvents = null; // Événements en attente si la carte n'est pas prête
    
    // Vues de la bottom sheet
    private TextView eventTitle;
    private TextView eventDate;
    private TextView eventVenue;
    private MaterialButton directionsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialisation des vues
        initViews();

        // Initialisation de la carte
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialisation du ViewModel
        viewModel = new ViewModelProvider(this).get(EventViewModel.class);

        // Observation des événements
        viewModel.getEvents().observe(this, this::updateMapMarkers);
    }
    
    private void initViews() {
        // Configuration de la bottom sheet
        View bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        
        // Initialisation des vues de la bottom sheet
        eventTitle = findViewById(R.id.eventTitle);
        eventDate = findViewById(R.id.eventDate);
        eventVenue = findViewById(R.id.eventVenue);
        directionsButton = findViewById(R.id.directionsButton);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Si nous avons déjà des événements en attente, les afficher maintenant
        if (pendingEvents != null) {
            updateMapMarkers(pendingEvents);
            pendingEvents = null;
        }
        // Sinon, ne pas centrer la carte maintenant, attendre les événements
    }

    private void updateMapMarkers(List<Event> events) {
        // Si la carte n'est pas encore prête, sauvegarder les événements pour plus tard
        if (mMap == null) {
            pendingEvents = events;
            return;
        }

        mMap.clear();
        markerEventMap.clear();
        
        if (events == null || events.isEmpty()) {
            // Si pas d'événements, centrer sur la localisation de l'utilisateur ou par défaut
            if (!isMapCentered) {
                if (viewModel.getUserLocation() != null) {
                    LatLng userLocation = new LatLng(
                        viewModel.getUserLocation().getLatitude(),
                        viewModel.getUserLocation().getLongitude()
                    );
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));
                } else {
                    LatLng defaultLocation = new LatLng(46.603354, 1.888334); // France
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 6));
                }
                isMapCentered = true;
            }
            return;
        }
        
        // Calculer les limites pour centrer la carte sur tous les événements
        double minLat = Double.MAX_VALUE;
        double maxLat = Double.MIN_VALUE;
        double minLng = Double.MAX_VALUE;
        double maxLng = Double.MIN_VALUE;
        
        for (Event event : events) {
            LatLng position = new LatLng(event.getLatitude(), event.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(event.getTitle())
                    .snippet(event.getVenueName()));
            
            if (marker != null) {
                markerEventMap.put(marker, event);
            }
            
            // Mettre à jour les limites
            minLat = Math.min(minLat, event.getLatitude());
            maxLat = Math.max(maxLat, event.getLatitude());
            minLng = Math.min(minLng, event.getLongitude());
            maxLng = Math.max(maxLng, event.getLongitude());
        }
        
        // Centrer la carte sur tous les événements (seulement la première fois)
        if (!isMapCentered) {
            if (events.size() == 1) {
                // Si un seul événement, centrer dessus avec un zoom approprié
                Event event = events.get(0);
                LatLng position = new LatLng(event.getLatitude(), event.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 14));
            } else {
                // Si plusieurs événements, ajuster la vue pour tous les voir
                com.google.android.gms.maps.model.LatLngBounds bounds = 
                    new com.google.android.gms.maps.model.LatLngBounds.Builder()
                        .include(new LatLng(minLat, minLng))
                        .include(new LatLng(maxLat, maxLng))
                        .build();
                
                // Ajouter un padding de 150px autour des marqueurs et utiliser une animation
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
            }
            isMapCentered = true;
        }

        // Ajouter un listener pour les marqueurs
        mMap.setOnMarkerClickListener(marker -> {
            Event event = markerEventMap.get(marker);
            if (event != null) {
                showEventDetails(event);
            }
            return true;
        });
        
        // Fermer la bottom sheet quand on clique sur la carte
        mMap.setOnMapClickListener(latLng -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });
    }
    
    private void showEventDetails(Event event) {
        // Remplir les informations de l'événement
        eventTitle.setText(event.getTitle());
        
        // Utiliser la méthode de formatage existante
        eventDate.setText(event.getFormattedStartDate());
        
        eventVenue.setText(event.getVenueName());
        
        // Configurer le bouton d'itinéraire
        directionsButton.setOnClickListener(v -> {
            openDirections(event.getLatitude(), event.getLongitude(), event.getVenueName());
        });
        
        // Afficher la bottom sheet
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }
    
    private void openDirections(double latitude, double longitude, String venueName) {
        // Créer un intent pour ouvrir Google Maps avec l'itinéraire
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(%s)", 
            latitude, longitude, latitude, longitude, venueName);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Si Google Maps n'est pas installé, utiliser le navigateur
            String browserUri = String.format(Locale.ENGLISH, 
                "https://www.google.com/maps/dir/?api=1&destination=%f,%f", 
                latitude, longitude);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(browserUri));
            startActivity(browserIntent);
        }
    }

    /**
     * Méthode pour recentrer la carte sur les événements
     */
    public void recenterMap() {
        isMapCentered = false;
        List<Event> currentEvents = viewModel.getEvents().getValue();
        if (currentEvents != null) {
            updateMapMarkers(currentEvents);
        }
    }
} 