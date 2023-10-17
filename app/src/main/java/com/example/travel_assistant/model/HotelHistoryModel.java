package com.example.travel_assistant.model;

public class HotelHistoryModel {

    public String hotelBookingId, hotelId, hotelName, offerId, hotelCheckIn, hotelCheckOut, hotelPrice, description;

    public HotelHistoryModel(String hotelBookingId, String hotelId, String hotelName, String offerId, String hotelCheckIn, String hotelCheckOut, String hotelPrice, String description) {
        this.hotelBookingId = hotelBookingId;
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.offerId = offerId;
        this.hotelCheckIn = hotelCheckIn;
        this.hotelCheckOut = hotelCheckOut;
        this.hotelPrice = hotelPrice;
        this.description = description;
    }

    public String getHotelBookingId() {
        return hotelBookingId;
    }

    public void setHotelBookingId(String hotelBookingId) {
        this.hotelBookingId = hotelBookingId;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getHotelCheckIn() {
        return hotelCheckIn;
    }

    public void setHotelCheckIn(String hotelCheckIn) {
        this.hotelCheckIn = hotelCheckIn;
    }

    public String getHotelCheckOut() {
        return hotelCheckOut;
    }

    public void setHotelCheckOut(String hotelCheckOut) {
        this.hotelCheckOut = hotelCheckOut;
    }

    public String getHotelPrice() {
        return hotelPrice;
    }

    public void setHotelPrice(String hotelPrice) {
        this.hotelPrice = hotelPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
