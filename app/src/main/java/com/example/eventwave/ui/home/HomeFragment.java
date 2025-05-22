package com.example.eventwave.ui.home;

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

        return root;
    }

    private void setupRecyclerView() {
        eventAdapter = new EventAdapter(this);
        binding.eventsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.eventsRecyclerView.setAdapter(eventAdapter);
    }

    private void setupChipGroup() {
        binding.chipAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) eventViewModel.setSelectedCategory(null);
        });

        binding.chipMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) eventViewModel.setSelectedCategory("Musique");
        });

        // Ajoutez les autres chips de la même manière
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
            eventAdapter.setEvents(events);
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
        // Gérer le clic sur un événement
    }
} 