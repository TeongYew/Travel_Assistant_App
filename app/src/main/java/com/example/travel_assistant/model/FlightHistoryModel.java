package com.example.travel_assistant.model;

public class FlightHistoryModel {

    public String flightBookingId, departureIATA, departureName, departureAt, arrivalIATA, arrivalName, arrivalAt, flightAirline, flightClass, flightCode, flightPrice, adultCount, kidCount;
    public boolean roundtrip;

    public FlightHistoryModel(String flightBookingId, String departureIATA, String departureName, String departureAt, String arrivalIATA, String arrivalName, String arrivalAt, String flightAirline, String flightClass, String flightCode, String flightPrice, String adultCount, String kidCount, boolean roundtrip) {
        this.flightBookingId = flightBookingId;
        this.departureIATA = departureIATA;
        this.departureName = departureName;
        this.departureAt = departureAt;
        this.arrivalIATA = arrivalIATA;
        this.arrivalName = arrivalName;
        this.arrivalAt = arrivalAt;
        this.flightAirline = flightAirline;
        this.flightClass = flightClass;
        this.flightCode = flightCode;
        this.flightPrice = flightPrice;
        this.adultCount = adultCount;
        this.kidCount = kidCount;
        this.roundtrip = roundtrip;
    }

    public String getFlightBookingId() {
        return flightBookingId;
    }

    public void setFlightBookingId(String flightBookingId) {
        this.flightBookingId = flightBookingId;
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

    public String getDepartureAt() {
        return departureAt;
    }

    public void setDepartureAt(String departureAt) {
        this.departureAt = departureAt;
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

    public String getArrivalAt() {
        return arrivalAt;
    }

    public void setArrivalAt(String arrivalAt) {
        this.arrivalAt = arrivalAt;
    }

    public String getFlightAirline() {
        return flightAirline;
    }

    public void setFlightAirline(String flightAirline) {
        this.flightAirline = flightAirline;
    }

    public String getFlightClass() {
        return flightClass;
    }

    public void setFlightClass(String flightClass) {
        this.flightClass = flightClass;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public void setFlightCode(String flightCode) {
        this.flightCode = flightCode;
    }

    public String getFlightPrice() {
        return flightPrice;
    }

    public void setFlightPrice(String flightPrice) {
        this.flightPrice = flightPrice;
    }

    public String getAdultCount() {
        return adultCount;
    }

    public void setAdultCoount(String adultCount) {
        this.adultCount = adultCount;
    }

    public String getKidCount() {
        return kidCount;
    }

    public void setKidCount(String kidCount) {
        this.kidCount = kidCount;
    }

    public boolean isRoundtrip() {
        return roundtrip;
    }

    public void setRoundtrip(boolean roundtrip) {
        this.roundtrip = roundtrip;
    }
}
