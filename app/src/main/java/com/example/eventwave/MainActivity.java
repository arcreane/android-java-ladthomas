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
import com.example.eventwave.api.TicketmasterService;
import com.example.eventwave.databinding.ActivityMainBinding;
import com.example.eventwave.utils.Constants;
import com.example.eventwave.viewmodel.EventViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

/**
 * 🏠 ACTIVITÉ PRINCIPALE - Point d'entrée de l'application
 * 
 * Responsabilités :
 * - Gestion des permissions de localisation
 * - Configuration de la navigation entre fragments
 * - Initialisation du ViewModel partagé
 * - Test de connexion API
 */
public class MainActivity extends AppCompatActivity {
    
    // 📱 Code de demande pour la permission de localisation
    private static final int REQUEST_LOCATION_PERMISSION = Constants.LOCATION_PERMISSION_REQUEST_CODE;
    
    // 🔗 Composants principaux
    private ActivityMainBinding binding;          // ViewBinding pour accès sécurisé aux vues
    private EventViewModel viewModel;             // ViewModel partagé entre fragments
    private FusedLocationProviderClient fusedLocationClient; // Service de localisation Google
    private final Handler mainHandler = new Handler(Looper.getMainLooper()); // Handler pour UI thread
    private NavController navController;          // Contrôleur de navigation
    private AppBarConfiguration appBarConfiguration; // Configuration de la barre d'action

    /**
     * 🚀 MÉTHODE DE CRÉATION - Initialisation de l'activité
     * Appelée automatiquement lors du lancement de l'activité
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 📐 Configuration du View Binding pour accès sécurisé aux vues
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 🧪 Test de connexion API en arrière-plan
        // Permet de vérifier rapidement si l'API Ticketmaster est accessible
        new Thread(() -> {
            new TicketmasterService().testApiConnection();
        }).start();

        // 🔧 Configuration de la Toolbar personnalisée
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        // 📍 Initialisation du service de localisation Google Play Services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        // 🧠 Initialisation du ViewModel partagé entre tous les fragments
        viewModel = new ViewModelProvider(this).get(EventViewModel.class);

        // 🗺️ Configuration du système de navigation
        setupNavigation();
        
        // 🔒 Demande des permissions de localisation
        requestLocationPermission();
    }

    /**
     * 🧭 CONFIGURATION DE LA NAVIGATION - Setup du Navigation Component
     * Configure la navigation entre les différents fragments de l'app
     */
    private void setupNavigation() {
        // 🎯 Récupération du NavHostFragment depuis le layout
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        
        // 🔗 Obtention du NavController pour gérer la navigation
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            
            // 📱 Configuration de la BottomNavigationView
            BottomNavigationView navView = binding.bottomNavigation;
            
            // ⚙️ Configuration des destinations principales (sans flèche retour)
            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home,      // 🏠 Accueil
                    R.id.navigation_history,   // 📚 Historique  
                    R.id.navigation_favorites, // ⭐ Favoris
                    R.id.navigation_settings)  // ⚙️ Paramètres
                    .build();
            
            // 🔗 Liaison de la Toolbar avec le NavController pour navigation automatique
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            
            // 🔗 Liaison de la BottomNavigationView avec le NavController
            NavigationUI.setupWithNavController(navView, navController);
        }
    }

    /**
     * 🔒 DEMANDE DE PERMISSION DE LOCALISATION
     * Vérifie si la permission est accordée, sinon la demande à l'utilisateur
     */
    private void requestLocationPermission() {
        // 🔍 Vérification de l'état actuel de la permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
            != PackageManager.PERMISSION_GRANTED) {
            
            // ❌ Permission non accordée : demande à l'utilisateur
            ActivityCompat.requestPermissions(this, 
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 
                REQUEST_LOCATION_PERMISSION);
        } else {
            // ✅ Permission déjà accordée : récupération de la localisation
            getLocation();
        }
    }

    /**
     * 📍 RÉCUPÉRATION DE LA LOCALISATION UTILISATEUR
     * Utilise FusedLocationProvider pour obtenir la position actuelle
     */
    private void getLocation() {
        // 🔒 Double vérification de la permission (sécurité)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
            == PackageManager.PERMISSION_GRANTED) {
            
            // 📡 Demande de la dernière localisation connue
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    // ✅ Localisation obtenue : mise à jour du ViewModel
                    viewModel.setUserLocation(location);
                    refreshEvents(); // 🔄 Chargement des événements pour cette localisation
                } else {
                    // ❌ Localisation indisponible : message à l'utilisateur
                    Snackbar.make(binding.getRoot(), R.string.location_not_found, 
                        Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * 🔄 RAFRAÎCHISSEMENT DES ÉVÉNEMENTS
     * Déclenche le chargement des événements via le ViewModel
     */
    private void refreshEvents() {
        viewModel.refreshEvents();
    }

    /**
     * 📱 GESTION DE LA RÉPONSE AUX PERMISSIONS
     * Callback appelé quand l'utilisateur répond à la demande de permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        // 🔍 Vérification que c'est bien notre demande de permission de localisation
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // ✅ Permission accordée : récupération de la localisation
                getLocation();
            } else {
                // ❌ Permission refusée : fonctionnement en mode dégradé
                Snackbar.make(binding.getRoot(), R.string.permission_denied, 
                    Snackbar.LENGTH_LONG).show();
                // 🔄 Chargement des événements sans localisation (données génériques)
                refreshEvents();
            }
        }
    }

    /**
     * 🔙 GESTION DU BOUTON RETOUR
     * Support pour la navigation avec le bouton "Up" dans la Toolbar
     */
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration) 
            || super.onSupportNavigateUp();
    }
}