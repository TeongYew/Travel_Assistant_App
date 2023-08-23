package com.example.travel_assistant.model;

public class HotelModel {

    private String hotelName;
    private int hotelRating;
    private int hotelImage;

    // Constructor
    public HotelModel(String hotelName, int hotelRating, int hotelImage) {
        this.hotelName = hotelName;
        this.hotelRating = hotelRating;
        this.hotelImage = hotelImage;
    }

    // Getter and Setter
    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public int getHotelRating() {
        return hotelRating;
    }

    public void setHotelRating(int hotelRating) {
        this.hotelRating = hotelRating;
    }

    public int getHotelImage() {
        return hotelImage;
    }

    public void setHotelImage(int hotelImage) {
        this.hotelImage = hotelImage;
    }

}
