package com.example.eventwave.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 🗄️ ENTITÉ EVENT - Modèle principal de données pour les événements
 * 
 * Cette classe représente un événement dans l'application.
 * Annotations Room :
 * - @Entity : Définit cette classe comme une table de base de données
 * - @PrimaryKey : Définit l'identifiant unique de l'entité
 * - @Ignore : Exclut certains champs de la base de données
 */
@Entity(tableName = "events") // 📊 Nom de la table dans la base de données SQLite
public class Event {
    
    // 🔑 IDENTIFIANT UNIQUE - Obligatoire et non null
    @PrimaryKey
    @NonNull
    private String id; // ID unique de l'événement (provient de l'API Ticketmaster)
    
    // 📝 INFORMATIONS DE BASE
    private String title;        // Nom de l'événement
    private String description;  // Description détaillée
    private String imageUrl;     // URL de l'image de couverture
    private String category;     // Catégorie (Musique, Sport, etc.)
    
    // 📍 INFORMATIONS DE LIEU
    private String venueName;    // Nom du lieu/salle
    private double latitude;     // Coordonnée géographique Nord-Sud
    private double longitude;    // Coordonnée géographique Est-Ouest
    
    // ⏰ INFORMATIONS TEMPORELLES
    private long startDate;      // Date de début en timestamp (millisecondes depuis 1970)
    
    // 🏷️ MÉTADONNÉES UTILISATEUR
    private boolean favorite;    // Statut favori défini par l'utilisateur
    
    // 📏 CHAMP CALCULÉ DYNAMIQUEMENT
    @Ignore // 🚫 Ce champ n'est PAS stocké en base de données
    private transient double distance; // Distance depuis la position utilisateur (en km)

    /**
     * 🏗️ CONSTRUCTEUR PRINCIPAL
     * Utilisé par Room pour créer des objets Event depuis la base de données
     */
    public Event(@NonNull String id, String title, String description, String imageUrl, 
                String category, String venueName, double latitude, double longitude, 
                long startDate, boolean favorite) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.venueName = venueName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.startDate = startDate;
        this.favorite = favorite;
        this.distance = 0.0; // 📏 Valeur par défaut pour la distance
    }

    // 🔍 === GETTERS - Accès en lecture aux propriétés ===
    
    @NonNull
    public String getId() { return id; }
    
    public String getTitle() { return title; }
    
    public String getDescription() { return description; }
    
    public String getImageUrl() { return imageUrl; }
    
    public String getCategory() { return category; }
    
    public String getVenueName() { return venueName; }
    
    public double getLatitude() { return latitude; }
    
    public double getLongitude() { return longitude; }
    
    public long getStartDate() { return startDate; }
    
    public boolean isFavorite() { return favorite; }
    
    public double getDistance() { return distance; }

    /**
     * 📅 FORMATAGE DE DATE - Méthode utilitaire pour l'affichage
     * Convertit le timestamp en chaîne lisible en français
     * Format : "25 décembre 2023 à 20:30"
     */
    public String getFormattedStartDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy à HH:mm", Locale.FRENCH);
        return sdf.format(new Date(startDate));
    }

    // ✏️ === SETTERS - Modification des propriétés ===
    
    public void setId(@NonNull String id) { this.id = id; }
    
    public void setTitle(String title) { this.title = title; }
    
    public void setDescription(String description) { this.description = description; }
    
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public void setCategory(String category) { this.category = category; }
    
    public void setVenueName(String venueName) { this.venueName = venueName; }
    
    public void setLatitude(double latitude) { this.latitude = latitude; }
    
    public void setLongitude(double longitude) { this.longitude = longitude; }
    
    public void setStartDate(long startDate) { this.startDate = startDate; }
    
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
    
    public void setDistance(double distance) { this.distance = distance; }

    /**
     * ⚖️ COMPARAISON D'OBJETS - Méthode equals()
     * Définit quand deux événements sont considérés comme identiques
     * Important pour les collections et la déduplication
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // 🔍 Même référence en mémoire
        if (o == null || getClass() != o.getClass()) return false; // 🔍 Type différent
        Event event = (Event) o;
        // 🔍 Comparaison basée sur l'ID unique
        return Double.compare(event.latitude, latitude) == 0 &&
                Double.compare(event.longitude, longitude) == 0 &&
                startDate == event.startDate &&
                favorite == event.favorite &&
                id.equals(event.id) &&
                title.equals(event.title) &&
                description.equals(event.description) &&
                imageUrl.equals(event.imageUrl) &&
                category.equals(event.category) &&
                venueName.equals(event.venueName);
    }

    /**
     * 🔢 CODE DE HACHAGE - Méthode hashCode()
     * Génère un code de hachage unique pour optimiser les collections (HashMap, HashSet)
     * Doit être cohérent avec equals()
     */
    @Override
    public int hashCode() {
        int result = 17; // 🎯 Nombre premier de base
        result = 31 * result + id.hashCode();           // 🔑 ID principal
        result = 31 * result + title.hashCode();        // 📝 Titre
        result = 31 * result + description.hashCode();  // 📄 Description
        result = 31 * result + imageUrl.hashCode();     // 🖼️ Image
        result = 31 * result + category.hashCode();     // 🏷️ Catégorie
        result = 31 * result + venueName.hashCode();    // 📍 Lieu
        result = 31 * result + Double.hashCode(latitude);   // 🌐 Coordonnées
        result = 31 * result + Double.hashCode(longitude);
        result = 31 * result + Long.hashCode(startDate);    // ⏰ Date
        result = 31 * result + Boolean.hashCode(favorite);  // ⭐ Favori
        return result;
    }
} 