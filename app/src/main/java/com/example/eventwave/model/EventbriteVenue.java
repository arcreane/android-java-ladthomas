package com.example.eventwave.model;

import com.google.gson.annotations.SerializedName;

public class EventbriteVenue {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;
    
    @SerializedName("capacity")
    private Integer capacity;
    
    @SerializedName("address")
    private Address address;

    public static class Address {
        @SerializedName("address_1")
        private String address1;
        
        @SerializedName("address_2")
        private String address2;
        
        @SerializedName("city")
        private String city;
        
        @SerializedName("region")
        private String region;
        
        @SerializedName("postal_code")
        private String postalCode;
        
        @SerializedName("country")
        private String country;
        
        @SerializedName("localized_address_display")
        private String localizedAddressDisplay;

        public String getAddress1() {
            return address1;
        }

        public String getAddress2() {
            return address2;
        }

        public String getCity() {
            return city;
        }

        public String getRegion() {
            return region;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public String getCountry() {
            return country;
        }

        public String getLocalizedAddressDisplay() {
            return localizedAddressDisplay;
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        if (latitude != null) {
            try {
                return Double.parseDouble(latitude);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }

    public double getLongitude() {
        if (longitude != null) {
            try {
                return Double.parseDouble(longitude);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }

    public int getCapacity() {
        return capacity != null ? capacity : 0;
    }

    public Address getAddress() {
        return address;
    }
    
    public String getFullAddressDisplay() {
        if (address != null && address.localizedAddressDisplay != null) {
            return address.localizedAddressDisplay;
        }
        StringBuilder sb = new StringBuilder();
        
        if (address != null) {
            if (address.address1 != null && !address.address1.isEmpty()) {
                sb.append(address.address1);
            }
            if (address.address2 != null && !address.address2.isEmpty()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(address.address2);
            }
            if (address.city != null && !address.city.isEmpty()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(address.city);
            }
            if (address.region != null && !address.region.isEmpty()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(address.region);
            }
            if (address.postalCode != null && !address.postalCode.isEmpty()) {
                if (sb.length() > 0) sb.append(" ");
                sb.append(address.postalCode);
            }
            if (address.country != null && !address.country.isEmpty()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(address.country);
            }
        }
        
        return sb.length() > 0 ? sb.toString() : "Adresse non spécifiée";
    }
} 