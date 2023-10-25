package com.example.travel_assistant.model;

public class HotelListModel {

    public String hotelName, hotelId, hotelCurrency, hotelPrice;

    public HotelListModel(String hotelName, String hotelId, String hotelCurrency, String hotelPrice) {
        this.hotelName = hotelName;
        this.hotelId = hotelId;
        this.hotelCurrency = hotelCurrency;
        this.hotelPrice = hotelPrice;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getHotelCurrency() {
        return hotelCurrency;
    }

    public void setHotelCurrency(String hotelCurrency) {
        this.hotelCurrency = hotelCurrency;
    }

    public String getHotelPrice() {
        return hotelPrice;
    }

    public void setHotelPrice(String hotelPrice) {
        this.hotelPrice = hotelPrice;
    }
}
