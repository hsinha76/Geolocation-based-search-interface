package com.example.assignment2;

import com.example.assignment2.model.PlaceData;

public class DataHandler {
    private static DataHandler dataHandler;

    public static DataHandler getInstance() {
        if (dataHandler == null)
            dataHandler = new DataHandler();
        return dataHandler;
    }

    private Double latitude;
    private Double longitude;
    private PlaceData selectedPlace;

    public PlaceData getSelectedPlace() {
        return selectedPlace;
    }

    public void setSelectedPlace(PlaceData selectedPlace) {
        this.selectedPlace = selectedPlace;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
