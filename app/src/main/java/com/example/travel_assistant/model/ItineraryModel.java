package com.example.travel_assistant.model;

public class ItineraryModel {

    private String itineraryName;
    private int itineraryRating;
    private int itineraryImage;

    // Constructor
    public ItineraryModel(String itineraryName, int itineraryRating, int itineraryImage) {
        this.itineraryName = itineraryName;
        this.itineraryRating = itineraryRating;
        this.itineraryImage = itineraryImage;
    }

    // Getter and Setter
    public String getItineraryName() {
        return itineraryName;
    }

    public void setItineraryName(String itineraryName) {
        this.itineraryName = itineraryName;
    }

    public int getItineraryRating() {
        return itineraryRating;
    }

    public void setItineraryRating(int itineraryRating) {
        this.itineraryRating = itineraryRating;
    }

    public int getItineraryImage() {
        return itineraryImage;
    }

    public void setItineraryImage(int itineraryImage) {
        this.itineraryImage = itineraryImage;
    }
}
