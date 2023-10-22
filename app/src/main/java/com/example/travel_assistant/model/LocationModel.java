package com.example.travel_assistant.model;

public class LocationModel {
    public String iata, location, cityName;

    public LocationModel(String iata, String location, String cityName) {
        this.iata = iata;
        this.location = location;
        this.cityName = cityName;
    }

    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
