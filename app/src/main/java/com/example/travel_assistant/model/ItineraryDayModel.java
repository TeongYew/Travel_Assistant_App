package com.example.travel_assistant.model;

public class ItineraryDayModel {

    public String locationName, locationDate, locationTimeFrom, locationTimeTo, notes;

    public ItineraryDayModel(String locationName, String locationDate, String locationTimeFrom, String locationTimeTo, String notes) {
        this.locationName = locationName;
        this.locationDate = locationDate;
        this.locationTimeFrom = locationTimeFrom;
        this.locationTimeTo = locationTimeTo;
        this.notes = notes;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationDate() {
        return locationDate;
    }

    public void setLocationDate(String locationDate) {
        this.locationDate = locationDate;
    }

    public String getLocationTimeFrom() {
        return locationTimeFrom;
    }

    public void setLocationTimeFrom(String locationTimeFrom) {
        this.locationTimeFrom = locationTimeFrom;
    }

    public String getLocationTimeTo() {
        return locationTimeTo;
    }

    public void setLocationTimeTo(String locationTimeTo) {
        this.locationTimeTo = locationTimeTo;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
