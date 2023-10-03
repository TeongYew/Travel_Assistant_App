package com.example.travel_assistant.model;

public class LocationModel {
    public String iata, location, cityName;

    public LocationModel(String iata, String location, String cityName) {
        this.iata = iata;
        this.location = location;
        this.cityName = cityName;
    }
}
