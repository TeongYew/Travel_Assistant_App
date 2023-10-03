package com.example.travel_assistant.model;

import java.util.ArrayList;

public class ItineraryModel {

    public String itineraryName, itineraryLocation, itineraryDateFrom, itineraryDateTo;
    public int itineraryDaysCount;
    public ArrayList<ItineraryDayModel> itineraryDays;

    public ItineraryModel(String itineraryName, String itineraryLocation, String itineraryDateFrom, String itineraryDateTo, int itineraryDaysCount, ArrayList<ItineraryDayModel> itineraryDays) {
        this.itineraryName = itineraryName;
        this.itineraryLocation = itineraryLocation;
        this.itineraryDateFrom = itineraryDateFrom;
        this.itineraryDateTo = itineraryDateTo;
        this.itineraryDaysCount = itineraryDaysCount;
        this.itineraryDays = itineraryDays;
    }

    public String getItineraryName() {
        return itineraryName;
    }

    public void setItineraryName(String itineraryName) {
        this.itineraryName = itineraryName;
    }

    public String getItineraryDateFrom() {
        return itineraryDateFrom;
    }

    public void setItineraryDateFrom(String itineraryDateFrom) {
        this.itineraryDateFrom = itineraryDateFrom;
    }

    public String getItineraryDateTo() {
        return itineraryDateTo;
    }

    public void setItineraryDateTo(String itineraryDateTo) {
        this.itineraryDateTo = itineraryDateTo;
    }

    public int getItineraryDaysCount() {
        return itineraryDaysCount;
    }

    public void setItineraryDaysCount(int itineraryDaysCount) {
        this.itineraryDaysCount = itineraryDaysCount;
    }

    public ArrayList<ItineraryDayModel> getItineraryDays() {
        return itineraryDays;
    }

    public void setItineraryDays(ArrayList<ItineraryDayModel> itineraryDays) {
        this.itineraryDays = itineraryDays;
    }

    public String getItineraryLocation() {
        return itineraryLocation;
    }

    public void setItineraryLocation(String itineraryLocation) {
        this.itineraryLocation = itineraryLocation;
    }
}
