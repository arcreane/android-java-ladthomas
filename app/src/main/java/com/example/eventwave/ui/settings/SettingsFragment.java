package com.example.eventwave.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.example.eventwave.databinding.FragmentSettingsBinding;
import com.example.eventwave.service.EventMonitoringService;
import com.example.eventwave.viewmodel.EventViewModel;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private EventViewModel eventViewModel;
    private SharedPreferences preferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        setupRadiusSlider();
        setupNotificationSwitch();
        setupClearCacheButton();

        return root;
    }

    private void setupRadiusSlider() {
        float savedRadius = preferences.getFloat("search_radius", 5.0f);
        binding.radiusSlider.setValue(savedRadius);
        updateRadiusText(savedRadius);

        binding.radiusSlider.addOnChangeListener((slider, value, fromUser) -> {
            updateRadiusText(value);
            if (fromUser) {
                preferences.edit().putFloat("search_radius", value).apply();
                eventViewModel.setSearchRadius(value);
            }
        });
    }

    private void updateRadiusText(float radius) {
        binding.radiusValue.setText(String.format("%.1f km", radius));
    }

    private void setupNotificationSwitch() {
        boolean notificationsEnabled = preferences.getBoolean("notifications_enabled", false);
        binding.notificationSwitch.setChecked(notificationsEnabled);

        binding.notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("notifications_enabled", isChecked).apply();
            
            Intent serviceIntent = new Intent(requireContext(), EventMonitoringService.class);
            if (isChecked) {
                requireContext().startService(serviceIntent);
                Snackbar.make(binding.getRoot(), "Notifications activées", Snackbar.LENGTH_SHORT).show();
            } else {
                requireContext().stopService(serviceIntent);
                Snackbar.make(binding.getRoot(), "Notifications désactivées", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClearCacheButton() {
        binding.clearCacheButton.setOnClickListener(v -> {
            eventViewModel.clearCache();
            Snackbar.make(binding.getRoot(), "Cache vidé", Snackbar.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 