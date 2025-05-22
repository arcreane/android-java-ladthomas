package com.example.eventwave.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "events")
public class Event {
    @PrimaryKey
    @NonNull
    private String id;
    private String title;
    private String description;
    private String imageUrl;
    private String category;
    private String venueName;
    private double latitude;
    private double longitude;
    private long startDate;
    private boolean favorite;
    @Ignore // Ignorer ce champ pour Room
    private transient double distance; // Distance from user's location in kilometers

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
        this.distance = 0.0;
    }

    // Getters
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

    public String getFormattedStartDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy à HH:mm", Locale.FRENCH);
        return sdf.format(new Date(startDate));
    }

    // Setters
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
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

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + id.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + imageUrl.hashCode();
        result = 31 * result + category.hashCode();
        result = 31 * result + venueName.hashCode();
        result = 31 * result + Double.hashCode(latitude);
        result = 31 * result + Double.hashCode(longitude);
        result = 31 * result + Long.hashCode(startDate);
        result = 31 * result + Boolean.hashCode(favorite);
        return result;
    }
} 