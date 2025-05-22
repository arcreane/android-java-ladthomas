package com.example.eventwave;

import android.os.Bundle;
import android.view.View;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private EventViewModel viewModel;
    private BottomSheetBehavior<View> bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialisation de la carte
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialisation du ViewModel
        viewModel = new ViewModelProvider(this).get(EventViewModel.class);

        // Configuration de la bottom sheet
        View bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        // Observation des événements
        viewModel.getEvents().observe(this, this::updateMapMarkers);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Centrer la carte sur la France
        LatLng france = new LatLng(46.603354, 1.888334);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(france, 6));
    }

    private void updateMapMarkers(List<Event> events) {
        if (mMap == null) return;

        mMap.clear();
        for (Event event : events) {
            LatLng position = new LatLng(event.getLatitude(), event.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(event.getTitle())
                    .snippet(event.getVenueName()));
        }

        // Ajouter un listener pour les marqueurs
        mMap.setOnMarkerClickListener(marker -> {
            String title = marker.getTitle();
            String snippet = marker.getSnippet();
            Toast.makeText(this, title + "\n" + snippet, Toast.LENGTH_SHORT).show();
            return true;
        });
    }
} 