package com.example.eventwave.api;

import android.location.Location;
import android.util.Log;

import com.example.eventwave.model.Event;
import com.example.eventwave.model.OpenAgendaResponse;
import com.example.eventwave.model.OpenAgendaResponse.OpenAgendaEvent;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;

public class EventService {
    private static final String TAG = "EventService";
    // Passer à false pour utiliser l'API OpenAgenda
    private static final boolean USE_MOCK_DATA = true;

    private final OpenAgendaApi api;

    public EventService() {
        api = RetrofitClient.getOpenAgendaApi();
    }

    public List<Event> getEvents(Location location) throws IOException {
        if (USE_MOCK_DATA) {
            return getMockEvents(location);
        } else {
            try {
                return getEventsFromApi(location);
            } catch (IOException e) {
                Log.e(TAG, "Erreur lors de l'appel API, utilisation des données fictives", e);
                return getMockEvents(location);
            }
        }
    }

    private List<Event> getEventsFromApi(Location location) throws IOException {
        List<Event> events = new ArrayList<>();
        
        try {
            // Recherche d'événements en France, à proximité de la position de l'utilisateur
            Call<OpenAgendaResponse> call = api.searchEventsByLocation(
                "France", 
                location.getLatitude(), 
                location.getLongitude(), 
                10 // 10km de rayon
            );
            
            Log.d(TAG, "Appel API avec URL: " + call.request().url());
            
            Response<OpenAgendaResponse> response = call.execute();
            
            if (response.isSuccessful() && response.body() != null) {
                List<OpenAgendaEvent> openAgendaEvents = response.body().events;
                
                if (openAgendaEvents != null) {
                    for (OpenAgendaEvent openAgendaEvent : openAgendaEvents) {
                        // Déterminer la catégorie en utilisant les mots-clés
                        String category = "Événement";
                        if (openAgendaEvent.keywords != null && !openAgendaEvent.keywords.isEmpty()) {
                            for (String keyword : openAgendaEvent.keywords) {
                                String mappedCategory = mapCategory(keyword);
                                if (!mappedCategory.equals("Événement")) {
                                    category = mappedCategory;
                                    break;
                                }
                            }
                        }
                        
                        Event event = new Event(
                            openAgendaEvent.getId(),
                            openAgendaEvent.getTitle(),
                            openAgendaEvent.getDescription(),
                            openAgendaEvent.getImageUrl(),
                            category,
                            openAgendaEvent.getVenueName(),
                            openAgendaEvent.getLatitude() != 0.0 ? openAgendaEvent.getLatitude() : location.getLatitude(),
                            openAgendaEvent.getLongitude() != 0.0 ? openAgendaEvent.getLongitude() : location.getLongitude(),
                            openAgendaEvent.getStartDate(),
                            false
                        );
                        
                        events.add(event);
                    }
                }
            } else {
                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Erreur inconnue";
                Log.e(TAG, "Échec de la requête API: " + errorBody + " - Code: " + response.code());
                throw new IOException("Échec de la requête API - Code: " + response.code());
            }
            
            return events;
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'appel API", e);
            throw new IOException("Erreur lors de la récupération des événements: " + e.getMessage(), e);
        }
    }
    
    /**
     * Mappe les catégories d'OpenAgenda à nos catégories internes
     */
    private String mapCategory(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return "Événement";
        }
        
        String lowerKeyword = keyword.toLowerCase();
        
        if (lowerKeyword.contains("music") || lowerKeyword.contains("musique") || lowerKeyword.contains("concert")) {
            return "Musique";
        } else if (lowerKeyword.contains("theatre") || lowerKeyword.contains("théâtre") || lowerKeyword.contains("performance")) {
            return "Théâtre";
        } else if (lowerKeyword.contains("game") || lowerKeyword.contains("gaming") || lowerKeyword.contains("jeu")) {
            return "Gaming";
        } else if (lowerKeyword.contains("conference") || lowerKeyword.contains("conférence") || lowerKeyword.contains("seminar")) {
            return "Conférence";
        } else if (lowerKeyword.contains("sport") || lowerKeyword.contains("fitness") || lowerKeyword.contains("health")) {
            return "Sport";
        }
        
        return "Événement";
    }

    // Conserver la méthode qui génère des données fictives comme fallback
    private List<Event> getMockEvents(Location location) {
        // Données fictives pour le développement
        List<Event> events = new ArrayList<>();
        
        // Événements musicaux
        events.add(new Event(
            "1",
            "Concert de Jazz",
            "Un superbe concert de jazz avec les meilleurs artistes de la scène parisienne. Une soirée pleine de swing et d'impro à ne pas manquer !",
            "",
            "Musique",
            "Le Petit Journal",
            location.getLatitude() + 0.01,
            location.getLongitude() - 0.01,
            System.currentTimeMillis() + 86400000, // demain
            false
        ));
        
        events.add(new Event(
            "6",
            "Festival Électro Summer",
            "Le plus grand festival électro de l'été avec des DJs internationaux et une ambiance de folie !",
            "",
            "Musique",
            "Parc des Expositions",
            location.getLatitude() - 0.025,
            location.getLongitude() + 0.035,
            System.currentTimeMillis() + 518400000, // dans 6 jours
            false
        ));
        
        events.add(new Event(
            "9",
            "Concert Classique - Mozart",
            "Une soirée dédiée aux œuvres de Mozart interprétées par l'orchestre philharmonique",
            "",
            "Musique",
            "Salle Pleyel",
            location.getLatitude() + 0.018,
            location.getLongitude() - 0.022,
            System.currentTimeMillis() + 604800000, // dans 7 jours
            false
        ));

        // Événements théâtre
        events.add(new Event(
            "2",
            "Théâtre Moderne",
            "Une pièce contemporaine fascinante qui explore les relations humaines dans notre société digitale. Mise en scène par Jean Dupont.",
            "",
            "Théâtre",
            "Théâtre du Centre",
            location.getLatitude() - 0.01,
            location.getLongitude() + 0.01,
            System.currentTimeMillis() + 172800000, // dans 2 jours
            false
        ));
        
        events.add(new Event(
            "7",
            "Comédie - Le Malentendu",
            "Une comédie hilarante sur les quiproquos familiaux. Deux heures de rire garanties !",
            "",
            "Théâtre",
            "La Comédie Française",
            location.getLatitude() + 0.008,
            location.getLongitude() - 0.003,
            System.currentTimeMillis() + 345600000, // dans 4 jours
            false
        ));

        // Événements gaming
        events.add(new Event(
            "3",
            "Tournoi de Gaming",
            "Compétition de jeux vidéo avec de nombreux prix à gagner. Venez défier les meilleurs joueurs de la région sur Fortnite, LoL et CS:GO.",
            "",
            "Gaming",
            "Salle eSport Arena",
            location.getLatitude() + 0.02,
            location.getLongitude() + 0.02,
            System.currentTimeMillis() + 259200000, // dans 3 jours
            false
        ));
        
        events.add(new Event(
            "10",
            "Convention Retro Gaming",
            "Retrouvez les consoles et jeux de votre enfance. Expositions, ventes et tournois sur des jeux vintage !",
            "",
            "Gaming",
            "Centre Commercial Geek",
            location.getLatitude() - 0.015,
            location.getLongitude() + 0.025,
            System.currentTimeMillis() + 1209600000, // dans 14 jours
            false
        ));

        // Événements conférence
        events.add(new Event(
            "4",
            "Conférence Tech",
            "Les dernières innovations technologiques présentées par des experts du domaine. Intelligence artificielle, blockchain et métavers au programme.",
            "",
            "Conférence",
            "Centre des Congrès",
            location.getLatitude() - 0.02,
            location.getLongitude() - 0.02,
            System.currentTimeMillis() + 345600000, // dans 4 jours
            false
        ));
        
        events.add(new Event(
            "8",
            "Séminaire Marketing Digital",
            "Apprenez les dernières stratégies de marketing digital pour développer votre entreprise",
            "",
            "Conférence",
            "Business Center",
            location.getLatitude() - 0.005,
            location.getLongitude() - 0.008,
            System.currentTimeMillis() + 432000000, // dans 5 jours
            false
        ));

        // Événements sportifs
        events.add(new Event(
            "5",
            "Match de Football",
            "Derby local avec les équipes favorites. Ambiance garantie pour ce match qui s'annonce décisif pour le championnat !",
            "",
            "Sport",
            "Stade Municipal",
            location.getLatitude() + 0.015,
            location.getLongitude() - 0.015,
            System.currentTimeMillis() + 432000000, // dans 5 jours
            false
        ));
        
        events.add(new Event(
            "11",
            "Marathon de la Ville",
            "Le grand marathon annuel ouvert à tous. Parcours de 10km, semi-marathon et marathon complet.",
            "",
            "Sport",
            "Place Centrale",
            location.getLatitude() + 0.005,
            location.getLongitude() + 0.005,
            System.currentTimeMillis() + 1296000000, // dans 15 jours
            false
        ));
        
        events.add(new Event(
            "12",
            "Tournoi de Tennis Amateur",
            "Compétition amicale de tennis ouverte à tous les niveaux. Inscriptions sur place.",
            "",
            "Sport",
            "Club de Tennis Municipal",
            location.getLatitude() - 0.012,
            location.getLongitude() + 0.018,
            System.currentTimeMillis() + 691200000, // dans 8 jours
            false
        ));

        return events;
    }

    /**
     * Méthode de test pour vérifier l'API OpenAgenda
     */
    public void testApiConnection() {
        Log.d(TAG, "Test de l'API OpenAgenda");
        
        try {
            // Formatter la date pour le test
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            String today = sdf.format(cal.getTime());
            
            cal.add(Calendar.MONTH, 1);
            String nextMonth = sdf.format(cal.getTime());
            
            String dateRange = today + "," + nextMonth;
            
            // Créer un appel pour tester l'API
            Call<OpenAgendaResponse> call = api.searchEvents(
                "France",
                "Paris",
                null,
                null,
                null,
                "musique",
                dateRange
            );
            
            Log.d(TAG, "Test API avec URL: " + call.request().url());
            
            // Exécuter la requête de manière asynchrone
            call.enqueue(new retrofit2.Callback<OpenAgendaResponse>() {
                @Override
                public void onResponse(Call<OpenAgendaResponse> call, Response<OpenAgendaResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        OpenAgendaResponse data = response.body();
                        Log.d(TAG, "Test API réussi: " + (data.events != null ? data.events.size() : 0) + " événements trouvés");
                        if (data.events != null && !data.events.isEmpty()) {
                            OpenAgendaEvent event = data.events.get(0);
                            Log.d(TAG, "Premier événement: " + event.title);
                        }
                    } else {
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Erreur inconnue";
                            Log.e(TAG, "Test API échoué: " + errorBody + " - Code: " + response.code());
                        } catch (IOException e) {
                            Log.e(TAG, "Erreur lors de la lecture de l'erreur", e);
                        }
                    }
                }
                
                @Override
                public void onFailure(Call<OpenAgendaResponse> call, Throwable t) {
                    Log.e(TAG, "Test API échoué avec exception", t);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors du test de l'API", e);
        }
    }
} 