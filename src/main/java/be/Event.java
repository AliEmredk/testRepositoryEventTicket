package be;

import java.util.Date;

public class Event {

    private String EventName;
    private String Location;
    private String Date;
    private String StartTime;
    private String EndTime;
    private String Note;
    private int Price;
    private String Location_Guidance;
    private int eventId;

    public Event(String Location, String Date, String StartTime, String EndTime, String Note, int Price, String Location_Guidance, String EventName, int eventId) {
        this.Location = Location;
        this.Date = Date;
        this.StartTime = StartTime;
        this.EndTime = EndTime;
        this.Note = Note;
        this.Price = Price;
        this.Location_Guidance = Location_Guidance;
        this.EventName = EventName;
        this.eventId = eventId;
    }

    public Event(int eventId, String Location, String Date, String StartTime, String EndTime, String Note, int Price, String Location_Guidance, String EventName) {
        this.eventId = eventId;
        this.Location = Location;
        this.Date = Date;
        this.StartTime = StartTime;
        this.EndTime = EndTime;
        this.Note = Note;
        this.Price = Price;
        this.Location_Guidance = Location_Guidance;
        this.EventName = EventName;
    }

    public String getEventName() {
        return EventName;
    }

    public void setEventName(String eventName) {
        EventName = eventName;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        this.Date = date;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public String getLocation_Guidance() {
        return Location_Guidance;
    }

    public void setLocation_Guidance(String location_Guidance) {
        Location_Guidance = location_Guidance;
    }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }


    public String getLocationGuidance() {
        return Location_Guidance;
    }
}
