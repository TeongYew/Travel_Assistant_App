package com.example.travel_assistant.model;

import java.io.Serializable;

public class FlightItineraryListModel implements Serializable {

    public String departureIATA, arrivalIATA, departureAt, arrivalAt, departureTerminal, arrivalTerminal, carrierCode, number, aircraftCode, duration;

    public FlightItineraryListModel(String departureIATA, String arrivalIATA, String departureAt, String arrivalAt, String departureTerminal, String arrivalTerminal, String carrierCode, String number, String aircraftCode, String duration) {
        this.departureIATA = departureIATA;
        this.arrivalIATA = arrivalIATA;
        this.departureAt = departureAt;
        this.arrivalAt = arrivalAt;
        this.departureTerminal = departureTerminal;
        this.arrivalTerminal = arrivalTerminal;
        this.carrierCode = carrierCode;
        this.number = number;
        this.aircraftCode = aircraftCode;
        this.duration = duration;
    }

    public String getDepartureIATA() {
        return departureIATA;
    }

    public void setDepartureIATA(String departureIATA) {
        this.departureIATA = departureIATA;
    }

    public String getArrivalIATA() {
        return arrivalIATA;
    }

    public void setArrivalIATA(String arrivalIATA) {
        this.arrivalIATA = arrivalIATA;
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

    public String getCarrierCode() {
        return carrierCode;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAircraftCode() {
        return aircraftCode;
    }

    public void setAircraftCode(String aircraftCode) {
        this.aircraftCode = aircraftCode;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDepartureTerminal() {
        return departureTerminal;
    }

    public void setDepartureTerminal(String departureTerminal) {
        this.departureTerminal = departureTerminal;
    }

    public String getArrivalTerminal() {
        return arrivalTerminal;
    }

    public void setArrivalTerminal(String arrivalTerminal) {
        this.arrivalTerminal = arrivalTerminal;
    }
}
