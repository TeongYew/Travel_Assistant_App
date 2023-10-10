package com.example.travel_assistant.model;

import java.io.Serializable;
import java.util.Date;

public class ItineraryDayModel implements Serializable {

    public String itineraryId, locationName, locationDate, locationTimeFrom, locationTimeTo, notes;
    public Date locationFrom, locationTo;

    public ItineraryDayModel(String itineraryId, String locationName, String locationDate, String locationTimeFrom, String locationTimeTo, String notes, Date locationFrom, Date locationTo) {
        this.itineraryId = itineraryId;
        this.locationName = locationName;
        this.locationDate = locationDate;
        this.locationTimeFrom = locationTimeFrom;
        this.locationTimeTo = locationTimeTo;
        this.notes = notes;
        this.locationFrom = locationFrom;
        this.locationTo = locationTo;
    }

    public String getItineraryId() {
        return itineraryId;
    }

    public void setItineraryId(String itineraryId) {
        this.itineraryId = itineraryId;
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

    public Date getLocationFrom() {
        return locationFrom;
    }

    public void setLocationFrom(Date locationFrom) {
        this.locationFrom = locationFrom;
    }

    public Date getLocationTo() {
        return locationTo;
    }

    public void setLocationTo(Date locationTo) {
        this.locationTo = locationTo;
    }
}
