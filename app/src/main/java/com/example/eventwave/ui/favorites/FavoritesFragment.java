package com.example.eventwave.ui.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventwave.adapter.EventAdapter;
import com.example.eventwave.databinding.FragmentFavoritesBinding;
import com.example.eventwave.model.Event;
import com.example.eventwave.viewmodel.EventViewModel;
import com.google.android.material.snackbar.Snackbar;

public class FavoritesFragment extends Fragment implements EventAdapter.OnEventClickListener {

    private FragmentFavoritesBinding binding;
    private EventViewModel eventViewModel;
    private EventAdapter eventAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        setupRecyclerView();
        observeViewModel();

        return root;
    }

    private void setupRecyclerView() {
        eventAdapter = new EventAdapter(this);
        binding.favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.favoritesRecyclerView.setAdapter(eventAdapter);
    }

    private void observeViewModel() {
        eventViewModel.getFavoriteEvents().observe(getViewLifecycleOwner(), events -> {
            eventAdapter.setEvents(events);
            binding.emptyView.setVisibility(events.isEmpty() ? View.VISIBLE : View.GONE);
            binding.progressBar.setVisibility(View.GONE);
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
        // Gérer le clic sur un événement favori
    }
} 