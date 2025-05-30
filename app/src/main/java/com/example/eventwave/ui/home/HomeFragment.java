package com.example.eventwave.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventwave.MapActivity;
import com.example.eventwave.R;
import com.example.eventwave.adapter.EventAdapter;
import com.example.eventwave.databinding.FragmentHomeBinding;
import com.example.eventwave.model.Event;
import com.example.eventwave.viewmodel.EventViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

public class HomeFragment extends Fragment implements EventAdapter.OnEventClickListener {

    private FragmentHomeBinding binding;
    private EventViewModel eventViewModel;
    private EventAdapter eventAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        setupRecyclerView();
        setupChipGroup();
        setupSearch();
        observeViewModel();

        // Configurer le bouton FAB pour la carte
        binding.fabMap.setOnClickListener(v -> {
            // Code pour naviguer vers MapActivity
            Intent intent = new Intent(requireContext(), MapActivity.class);
            startActivity(intent);
        });

        return root;
    }

    private void setupRecyclerView() {
        eventAdapter = new EventAdapter(this);
        binding.eventsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.eventsRecyclerView.setAdapter(eventAdapter);
    }

    private void setupChipGroup() {
        binding.chipAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                eventAdapter.filterByCategory(null);
            }
        });

        binding.chipMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                eventAdapter.filterByCategory("Musique");
            }
        });

        binding.chipSports.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                eventAdapter.filterByCategory("Sport");
            }
        });

        binding.chipTheater.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                eventAdapter.filterByCategory("Théâtre");
            }
        });

        binding.chipFamily.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                eventAdapter.filterByCategory("Famille");
            }
        });
    }

    private void setupSearch() {
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                eventAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void observeViewModel() {
        eventViewModel.getEvents().observe(getViewLifecycleOwner(), events -> {
            if (events != null) {
                eventAdapter.setEvents(events);
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        eventViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        eventViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onEventClick(Event event) {
        // Ajouter l'événement à l'historique
        eventViewModel.addToHistory(event);
        
        // Ici vous pouvez naviguer vers les détails de l'événement
        Snackbar.make(binding.getRoot(), "Événement sélectionné : " + event.getTitle(), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onFavoriteClick(Event event) {
        // Inverser le statut de favori
        eventViewModel.toggleFavorite(event);
        
        // Mettre à jour l'adaptateur avec l'événement modifié
        eventAdapter.updateEvent(event);
        
        // Afficher un message de confirmation
        String message = event.isFavorite() ? 
            "Ajouté aux favoris" : 
            "Retiré des favoris";
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
    }
} 