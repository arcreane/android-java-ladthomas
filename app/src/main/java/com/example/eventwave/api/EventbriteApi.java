package com.example.eventwave.api;

import com.example.eventwave.model.EventbriteResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import java.util.Map;

public interface EventbriteApi {
    /**
     * Recherche des événements par localisation.
     * Documentation: https://www.eventbrite.com/platform/docs/events
     */
    @GET("api/v3/events/search/")
    Call<EventbriteResponse> searchEvents(
        @Query("location.latitude") double latitude,
        @Query("location.longitude") double longitude,
        @Query("location.within") String radiusKm,
        @Query("categories") String categories,
        @Query("sort_by") String sortBy,
        @Header("Authorization") String authToken
    );
    
    /**
     * Obtient un événement spécifique.
     */
    @GET("api/v3/events/{event_id}/")
    Call<EventbriteResponse.Event> getEvent(
        @Path("event_id") String eventId,
        @Header("Authorization") String authToken
    );
    
    /**
     * Récupère les événements d'une organisation.
     */
    @GET("api/v3/organizations/{organization_id}/events/")
    Call<EventbriteResponse> getOrganizationEvents(
        @Path("organization_id") String organizationId,
        @Query("status") String status,
        @Query("time_filter") String timeFilter,
        @Header("Authorization") String authToken
    );
} 