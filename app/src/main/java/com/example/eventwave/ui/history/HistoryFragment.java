package com.example.eventwave.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventwave.adapter.EventAdapter;
import com.example.eventwave.databinding.FragmentHistoryBinding;
import com.example.eventwave.model.Event;
import com.example.eventwave.viewmodel.EventViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

public class HistoryFragment extends Fragment implements EventAdapter.OnEventClickListener {

    private FragmentHistoryBinding binding;
    private EventViewModel eventViewModel;
    private EventAdapter eventAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        setupRecyclerView();
        setupClearHistoryButton();
        setupSwipeRefresh();
        observeViewModel();

        return root;
    }

    private void setupRecyclerView() {
        eventAdapter = new EventAdapter(this);
        binding.historyRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.historyRecyclerView.setAdapter(eventAdapter);
    }

    private void setupClearHistoryButton() {
        binding.clearHistoryButton.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Effacer l'historique")
                .setMessage("Voulez-vous vraiment effacer tout l'historique ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    eventViewModel.clearHistory();
                    Snackbar.make(binding.getRoot(), "Historique effacé", Snackbar.LENGTH_SHORT).show();
                })
                .setNegativeButton("Non", null)
                .show();
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(() -> {
            eventViewModel.refreshHistory();
        });
    }

    private void observeViewModel() {
        eventViewModel.getHistoryEvents().observe(getViewLifecycleOwner(), events -> {
            eventAdapter.setEvents(events);
            binding.progressBar.setVisibility(View.GONE);
            binding.swipeRefresh.setRefreshing(false);
            binding.emptyView.setVisibility(events.isEmpty() ? View.VISIBLE : View.GONE);
        });

        eventViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG).show();
                binding.swipeRefresh.setRefreshing(false);
            }
        });

        eventViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onEventClick(Event event) {
        // Gérer le clic sur un événement de l'historique
    }
} 