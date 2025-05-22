package com.example.eventwave.api;

import com.example.eventwave.model.OpenAgendaResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenAgendaApi {
    /**
     * Recherche d'événements avec filtres
     * @param country Pays (exemple: "France")
     * @param city Ville (exemple: "Paris")
     * @param latitude Latitude pour filtrer par position
     * @param longitude Longitude pour filtrer par position
     * @param radius Rayon de recherche en km
     * @param keywords Mots-clés pour la recherche
     * @param daterange Plage de dates au format "YYYY-MM-DD,YYYY-MM-DD"
     * @return Réponse contenant les événements
     */
    @GET("events.json")
    Call<OpenAgendaResponse> searchEvents(
            @Query("country") String country,
            @Query("city") String city,
            @Query("latitude") Double latitude,
            @Query("longitude") Double longitude,
            @Query("radius") Integer radius,
            @Query("keywords") String keywords,
            @Query("daterange") String daterange
    );
    
    /**
     * Version simplifiée pour rechercher des événements par position
     */
    @GET("events.json")
    Call<OpenAgendaResponse> searchEventsByLocation(
            @Query("country") String country,
            @Query("latitude") Double latitude,
            @Query("longitude") Double longitude,
            @Query("radius") Integer radius
    );
} 