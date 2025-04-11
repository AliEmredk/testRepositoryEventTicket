package be;

public class Ticket {

    private int ticketId;
    private String ticketType; // ✅ Updated
    private int barcodeId;
    private byte[] barcodeImage;
    private String barcodeString;
    private int eventId;
    private String eventName;
    private String location;
    private String eventDate;
    private String startTime;
    private String endTime;
    private String eventNote;
    private int customerId;
    private String firstName;
    private String lastName;
    private String email;
    private int discount;
    private String details;

    public Ticket(int ticketId, String ticketType, int barcodeId, byte[] barcodeImage, String barcodeString,
                  int eventId, String eventName, String location, String eventDate,
                  String startTime, String endTime, String eventNote,
                  int customerId, String firstName, String lastName, String email, int discount, String details) {
        this.ticketId = ticketId;
        this.ticketType = ticketType;
        this.barcodeId = barcodeId;
        this.barcodeImage = barcodeImage;
        this.barcodeString = barcodeString;
        this.eventId = eventId;
        this.eventName = eventName;
        this.location = location;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventNote = eventNote;
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.discount = discount;
        this.details = details;
    }

    public Ticket() {
    }

    // Getters
    public int getTicketId() { return ticketId; }
    public String getTicketType() { return ticketType; }
    public int getBarcodeId() { return barcodeId; }
    public byte[] getBarcodeImage() { return barcodeImage; }
    public String getBarcodeString() { return barcodeString; }
    public int getEventId() { return eventId; }
    public String getEventName() { return eventName; }
    public String getLocation() { return location; }
    public String getEventDate() { return eventDate; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getEventNote() { return eventNote; }
    public int getCustomerId() { return customerId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public int getDiscount() {return discount;}
    public String getDetails() { return details; }

    // Setters
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }
    public void setTicketType(String ticketType) { this.ticketType = ticketType; }
    public void setBarcodeId(int barcodeId) { this.barcodeId = barcodeId; }
    public void setBarcodeImage(byte[] barcodeImage) { this.barcodeImage = barcodeImage; }
    public void setBarcodeString(String barcodeString) { this.barcodeString = barcodeString; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public void setLocation(String location) { this.location = location; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public void setEventNote(String eventNote) { this.eventNote = eventNote; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setDiscount(int discount) {this.discount = discount;}
    public void setDetails(String details) { this.details = details; }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId=" + ticketId +
                ", ticketType='" + ticketType + '\'' +
                ", barcodeId=" + barcodeId +
                ", barcodeString='" + barcodeString + '\'' +
                ", eventId=" + eventId +
                ", eventName='" + eventName + '\'' +
                ", location='" + location + '\'' +
                ", eventDate=" + eventDate +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", eventNote='" + eventNote + '\'' +
                ", customerId=" + customerId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}