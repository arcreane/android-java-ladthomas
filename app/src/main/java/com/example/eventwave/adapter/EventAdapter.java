package com.example.eventwave.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.eventwave.R;
import com.example.eventwave.databinding.ItemEventBinding;
import com.example.eventwave.model.Event;
import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> events = new ArrayList<>();
    private List<Event> filteredEvents = new ArrayList<>();
    private final OnEventClickListener listener;
    private String currentSearchQuery = "";
    private String currentCategoryFilter = null;

    public interface OnEventClickListener {
        void onEventClick(Event event);
        void onFavoriteClick(Event event);
    }

    public EventAdapter(OnEventClickListener listener) {
        this.listener = listener;
        this.filteredEvents = new ArrayList<>();
    }

    public void setEvents(List<Event> events) {
        this.events = events;
        applyFilters();
    }

    public void filter(String query) {
        this.currentSearchQuery = query;
        applyFilters();
    }
    
    public void filterByCategory(String category) {
        this.currentCategoryFilter = category;
        applyFilters();
    }
    
    private void applyFilters() {
        filteredEvents.clear();
        
        for (Event event : events) {
            boolean matchesSearch = true;
            boolean matchesCategory = true;
            
            // Filtrage par recherche textuelle
            if (!currentSearchQuery.isEmpty()) {
                matchesSearch = event.getTitle().toLowerCase().contains(currentSearchQuery.toLowerCase());
            }
            
            // Filtrage par catégorie
            if (currentCategoryFilter != null && !currentCategoryFilter.equals("Tous")) {
                matchesCategory = event.getCategory().equals(currentCategoryFilter);
            }
            
            if (matchesSearch && matchesCategory) {
                filteredEvents.add(event);
            }
        }
        
        notifyDataSetChanged();
    }

    public void updateEvent(Event event) {
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getId().equals(event.getId())) {
                events.set(i, event);
                break;
            }
        }
        
        for (int i = 0; i < filteredEvents.size(); i++) {
            if (filteredEvents.get(i).getId().equals(event.getId())) {
                filteredEvents.set(i, event);
                notifyItemChanged(i);
                break;
            }
        }
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEventBinding binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false);
        return new EventViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = filteredEvents.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return filteredEvents.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        private final ItemEventBinding binding;

        EventViewHolder(ItemEventBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            
            // Rendre toute la carte cliquable
            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onEventClick(filteredEvents.get(position));
                }
            });
            
            // Configurer le bouton favoris
            binding.favoriteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Event event = filteredEvents.get(position);
                    listener.onFavoriteClick(event);
                }
            });
        }

        void bind(Event event) {
            binding.eventTitle.setText(event.getTitle());
            binding.eventDescription.setText(event.getDescription());
            binding.eventDate.setText(event.getFormattedStartDate());
            binding.eventLocation.setText(event.getVenueName());
            binding.categoryChip.setText(event.getCategory());
            
            // Mettre à jour l'icône de favoris
            binding.favoriteButton.setImageResource(
                event.isFavorite() ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border
            );

            // Sélectionner l'image en fonction de la catégorie
            int imageResource = R.drawable.event_placeholder;
            String category = event.getCategory().toLowerCase();
            
            if (category.contains("musique") || category.contains("music") || category.contains("concert")) {
                imageResource = R.drawable.music_event;
            } else if (category.contains("théâtre") || category.contains("theatre") || category.contains("theater")) {
                imageResource = R.drawable.theatre_event;
            } else if (category.contains("gaming") || category.contains("jeu") || category.contains("game")) {
                imageResource = R.drawable.gaming_event;
            } else if (category.contains("conférence") || category.contains("conference") || category.contains("talk")) {
                imageResource = R.drawable.conference_event;
            } else if (category.contains("sport") || category.contains("sportif")) {
                imageResource = R.drawable.sports_event;
            }

            // Charger l'image avec Glide
            Glide.with(binding.getRoot().getContext())
                .load(imageResource)
                .centerCrop()
                .into(binding.eventImage);
        }
    }
} 