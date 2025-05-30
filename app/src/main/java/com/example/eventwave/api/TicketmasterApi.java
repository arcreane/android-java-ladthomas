package com.example.eventwave.api;

import com.example.eventwave.model.TicketmasterResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TicketmasterApi {
    
    /**
     * Recherche des événements par géolocalisation
     * Documentation: https://developer.ticketmaster.com/products-and-docs/apis/discovery-api/v2/
     */
    @GET("discovery/v2/events.json")
    Call<TicketmasterResponse> searchEvents(
        @Query("apikey") String apiKey,
        @Query("latlong") String latlong,
        @Query("radius") String radius,
        @Query("unit") String unit,
        @Query("size") int size,
        @Query("page") int page,
        @Query("sort") String sort,
        @Query("classificationName") String classificationName,
        @Query("countryCode") String countryCode,
        @Query("startDateTime") String startDateTime
    );
    
    /**
     * Recherche des événements par géolocalisation sans restriction de pays
     */
    @GET("discovery/v2/events.json")
    Call<TicketmasterResponse> searchEventsWithoutCountry(
        @Query("apikey") String apiKey,
        @Query("latlong") String latlong,
        @Query("radius") String radius,
        @Query("unit") String unit,
        @Query("size") int size,
        @Query("page") int page,
        @Query("sort") String sort,
        @Query("classificationName") String classificationName,
        @Query("startDateTime") String startDateTime
    );
    
    /**
     * Recherche des événements par ville
     */
    @GET("discovery/v2/events.json")
    Call<TicketmasterResponse> searchEventsByCity(
        @Query("apikey") String apiKey,
        @Query("city") String city,
        @Query("countryCode") String countryCode,
        @Query("size") int size,
        @Query("page") int page,
        @Query("sort") String sort,
        @Query("classificationName") String classificationName
    );
    
    /**
     * Recherche des événements par mots-clés
     */
    @GET("discovery/v2/events.json")
    Call<TicketmasterResponse> searchEventsByKeyword(
        @Query("apikey") String apiKey,
        @Query("keyword") String keyword,
        @Query("latlong") String latlong,
        @Query("radius") String radius,
        @Query("unit") String unit,
        @Query("size") int size,
        @Query("page") int page,
        @Query("sort") String sort
    );
} 