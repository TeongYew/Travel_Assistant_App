package com.example.travel_assistant.activity;

import com.google.gson.JsonArray;

import java.io.Serializable;

public class FlightListModel implements Serializable {

    String departureIATA, departureName, arrivalIATA, arrivalName, departureAt, arrivalAt, priceCurrency, priceTotal, cabin, airline;
    JsonArray flightItinerary;


    public FlightListModel(String departureIATA, String departureName, String arrivalIATA, String arrivalName, String departureAt, String arrivalAt, String priceCurrency, String priceTotal, String cabin, String airline, JsonArray flightItinerary) {
        this.departureIATA = departureIATA;
        this.departureName = departureName;
        this.arrivalIATA = arrivalIATA;
        this.arrivalName = arrivalName;
        this.departureAt = departureAt;
        this.arrivalAt = arrivalAt;
        this.priceCurrency = priceCurrency;
        this.priceTotal = priceTotal;
        this.cabin = cabin;
        this.airline = airline;
        this.flightItinerary = flightItinerary;
    }

    public String getDepartureIATA() {
        return departureIATA;
    }

    public void setDepartureIATA(String departureIATA) {
        this.departureIATA = departureIATA;
    }

    public String getDepartureName() {
        return departureName;
    }

    public void setDepartureName(String departureName) {
        this.departureName = departureName;
    }

    public String getArrivalIATA() {
        return arrivalIATA;
    }

    public void setArrivalIATA(String arrivalIATA) {
        this.arrivalIATA = arrivalIATA;
    }

    public String getArrivalName() {
        return arrivalName;
    }

    public void setArrivalName(String arrivalName) {
        this.arrivalName = arrivalName;
    }

    public String getDepartureAt() {
        return departureAt;
    }

    public void setDepartureAt(String departureAt) {
        this.departureAt = departureAt;
    }

    public String getArrivalAt() {
        return arrivalAt;
    }

    public void setArrivalAt(String arrivalAt) {
        this.arrivalAt = arrivalAt;
    }

    public String getPriceCurrency() {
        return priceCurrency;
    }

    public void setPriceCurrency(String priceCurrency) {
        this.priceCurrency = priceCurrency;
    }

    public String getPriceTotal() {
        return priceTotal;
    }

    public void setPriceTotal(String priceTotal) {
        this.priceTotal = priceTotal;
    }

    public String getCabin() {
        return cabin;
    }

    public void setCabin(String cabin) {
        this.cabin = cabin;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public JsonArray getFlightItinerary() {
        return flightItinerary;
    }

    public void setFlightItinerary(JsonArray flightItinerary) {
        this.flightItinerary = flightItinerary;
    }
}
