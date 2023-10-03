package com.example.travel_assistant.model;

public class HotelListModel {

    public String hotelName, hotelId;

    public HotelListModel(String hotelName, String hotelId) {
        this.hotelName = hotelName;
        this.hotelId = hotelId;
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
}
