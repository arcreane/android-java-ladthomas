package com.example.eventwave.adapter;

import android.view.LayoutInflater;
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

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    public EventAdapter(OnEventClickListener listener) {
        this.listener = listener;
        this.filteredEvents = new ArrayList<>();
    }

    public void setEvents(List<Event> events) {
        this.events = events;
        this.filteredEvents.clear();
        this.filteredEvents.addAll(events);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        filteredEvents.clear();
        if (query.isEmpty()) {
            filteredEvents.addAll(events);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Event event : events) {
                if (event.getTitle().toLowerCase().contains(lowerCaseQuery)) {
                    filteredEvents.add(event);
                }
            }
        }
        notifyDataSetChanged();
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
            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onEventClick(filteredEvents.get(position));
                }
            });
        }

        void bind(Event event) {
            binding.eventTitle.setText(event.getTitle());
            binding.eventDescription.setText(event.getDescription());
            binding.eventDate.setText(event.getFormattedStartDate());
            binding.eventLocation.setText(event.getVenueName());
            binding.categoryChip.setText(event.getCategory());

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