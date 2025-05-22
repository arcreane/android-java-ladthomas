package com.example.eventwave.model;

import com.google.gson.annotations.SerializedName;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EventbriteDateTime {
    @SerializedName("timezone")
    private String timezone;
    
    @SerializedName("utc")
    private String utc;
    
    @SerializedName("local")
    private String local;
    
    private static final SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
    
    static {
        ISO_8601_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public String getUtcString() {
        return utc;
    }
    
    public String getLocalString() {
        return local;
    }
    
    public Date getUtc() {
        try {
            if (utc != null) {
                return ISO_8601_FORMAT.parse(utc);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }
    
    public Date getLocal() {
        try {
            if (local != null) {
                SimpleDateFormat localFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                if (timezone != null) {
                    localFormat.setTimeZone(TimeZone.getTimeZone(timezone));
                }
                return localFormat.parse(local);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }
} 