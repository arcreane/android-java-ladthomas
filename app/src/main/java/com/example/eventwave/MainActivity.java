package com.example.eventwave;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.eventwave.api.EventService;
import com.example.eventwave.databinding.ActivityMainBinding;
import com.example.eventwave.viewmodel.EventViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private ActivityMainBinding binding;
    private EventViewModel viewModel;
    private FusedLocationProviderClient fusedLocationClient;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Test de l'API OpenAgenda
        new Thread(() -> {
            new EventService().testApiConnection();
        }).start();

        // Configurer la Toolbar
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        viewModel = new ViewModelProvider(this).get(EventViewModel.class);

        setupNavigation();
        requestLocationPermission();
    }

    private void setupNavigation() {
        // Récupère le NavHostFragment à partir du FragmentContainerView
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        
        // Obtient le NavController à partir du NavHostFragment
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            
            // Configure la BottomNavigationView
            BottomNavigationView navView = binding.bottomNavigation;
            
            // Configure l'AppBarConfiguration
            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_history, 
                    R.id.navigation_favorites, R.id.navigation_settings)
                    .build();
            
            // Configure la toolbar avec le NavController
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            
            // Configure la BottomNavigationView avec le NavController
            NavigationUI.setupWithNavController(navView, navController);
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            getLocation();
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    viewModel.setUserLocation(location);
                    refreshEvents();
                } else {
                    Snackbar.make(binding.getRoot(), R.string.location_not_found, Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    private void refreshEvents() {
        viewModel.refreshEvents();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Snackbar.make(binding.getRoot(), R.string.permission_denied, Snackbar.LENGTH_LONG).show();
                refreshEvents();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}