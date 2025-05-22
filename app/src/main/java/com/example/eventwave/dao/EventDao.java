package com.example.eventwave.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.eventwave.model.Event;

import java.util.List;

@Dao
public interface EventDao {
    @Query("SELECT * FROM events")
    LiveData<List<Event>> getAllEvents();

    @Query("SELECT * FROM events WHERE favorite = 1")
    LiveData<List<Event>> getFavoriteEvents();

    @Query("SELECT * FROM events WHERE category = :category")
    LiveData<List<Event>> getEventsByCategory(String category);

    @Query("SELECT * FROM events WHERE latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLon AND :maxLon")
    LiveData<List<Event>> getEventsInArea(double minLat, double maxLat, double minLon, double maxLon);

    @Query("SELECT * FROM events WHERE favorite = 1 AND latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLon AND :maxLon")
    LiveData<List<Event>> getFavoriteEventsInArea(double minLat, double maxLat, double minLon, double maxLon);

    @Query("SELECT * FROM events WHERE category = :category AND latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLon AND :maxLon")
    LiveData<List<Event>> getEventsByCategoryInArea(String category, double minLat, double maxLat, double minLon, double maxLon);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEvents(List<Event> events);

    @Update
    void update(Event event);

    @Delete
    void delete(Event event);

    @Query("DELETE FROM events")
    void deleteAllEvents();
} 