package com.example.travel_assistant.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ItineraryModel implements Serializable {

    public String itineraryId, itineraryName, itineraryLocation, itineraryDateFrom, itineraryDateTo;
    public int itineraryDaysCount;
    public ArrayList<ItineraryItemModel> itineraryDays;

    public ItineraryModel(String itineraryId, String itineraryName, String itineraryLocation, String itineraryDateFrom, String itineraryDateTo, int itineraryDaysCount, ArrayList<ItineraryItemModel> itineraryDays) {
        this.itineraryId = itineraryId;
        this.itineraryName = itineraryName;
        this.itineraryLocation = itineraryLocation;
        this.itineraryDateFrom = itineraryDateFrom;
        this.itineraryDateTo = itineraryDateTo;
        this.itineraryDaysCount = itineraryDaysCount;
        this.itineraryDays = itineraryDays;
    }

    public String getItineraryId() {
        return itineraryId;
    }

    public void setItineraryId(String itineraryId) {
        this.itineraryId = itineraryId;
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

    public ArrayList<ItineraryItemModel> getItineraryDays() {
        return itineraryDays;
    }

    public void setItineraryDays(ArrayList<ItineraryItemModel> itineraryDays) {
        this.itineraryDays = itineraryDays;
    }

    public String getItineraryLocation() {
        return itineraryLocation;
    }

    public void setItineraryLocation(String itineraryLocation) {
        this.itineraryLocation = itineraryLocation;
    }

}
