package com.example.travel_assistant.model;

public class FlightModel {

    private String flightName;
    private int flightRating;
    private int flightImage;

    // Constructor
    public FlightModel(String flightName, int flightRating, int flightImage) {
        this.flightName = flightName;
        this.flightRating = flightRating;
        this.flightImage = flightImage;
    }

    // Getter and Setter
    public String getFlightName() {
        return flightName;
    }

    public void setFlightName(String itineraryName) {
        this.flightName = flightName;
    }

    public int getFlightRating() {
        return flightRating;
    }

    public void setFlightRating(int flightRating) {
        this.flightRating = flightRating;
    }

    public int getFlightImage() {
        return flightImage;
    }

    public void setFlightImage(int flightImage) {
        this.flightImage = flightImage;
    }
}
