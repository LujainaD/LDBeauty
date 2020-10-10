package com.lujaina.ldbeauty.Models;

public class LocationModel {
    private String salonOwnerId;
    private String locationId;
    private String locationName;
    private double latitude;
    private double longitude;

    public String getSalonOwnerId() {
        return salonOwnerId;
    }

    public void setSalonOwnerId(String salonOwnerId) {
        this.salonOwnerId = salonOwnerId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
