package com.example.eventwave.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.eventwave.service.EventMonitoringService;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            boolean notificationsEnabled = preferences.getBoolean("notifications_enabled", false);
            
            if (notificationsEnabled) {
                Intent serviceIntent = new Intent(context, EventMonitoringService.class);
                context.startService(serviceIntent);
            }
        }
    }
} 