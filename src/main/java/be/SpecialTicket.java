package be;

public class SpecialTicket {

    private Integer specialTicketId;
    private String specialTicketDetails;
    private Integer eventId;
    private byte[] barcodeImage;// BarCode format 128
    private int barcodeId;
    private String barcodeString;

    public SpecialTicket(Integer specialTicketId, String specialTicketDetails, Integer eventId, byte[] barcodeImage, int barcodeId, String barcodeString) {
        this.specialTicketId = specialTicketId;
        this.specialTicketDetails = specialTicketDetails;
        this.eventId = eventId;
        this.barcodeImage = barcodeImage;
        this.barcodeId = barcodeId;
        this.barcodeString = barcodeString;
    }

    public SpecialTicket() {}

    @Override
    public String toString() {
        return "SpecialTicket{" +
                "specialTicketId=" + specialTicketId +
                ", specialTicketDetails" + specialTicketDetails +
                ", eventId=" + eventId +
                ", barcodeId" + barcodeId + '\'' +
                ", barcodeString" + barcodeString + '\'' +
                "}";
    }

    // Getters
    public Integer getSpecialTicketId() {
        return specialTicketId;
    }

    public String getSpecialTicketDetails() {
        return specialTicketDetails;
    }

    public Integer getEventId() {
        return eventId;
    }

    public byte[] getBarcodeImage() {
        return barcodeImage;
    }

    public int getBarcodeId() {
        return barcodeId;
    }

    public String getBarcodeString() {
        return barcodeString;
    }

    // Setters
    public void setSpecialTicketId(Integer specialTicketId) {
        this.specialTicketId = specialTicketId;
    }

    public void setSpecialTicketDetails(String specialTicketDetails) {
        this.specialTicketDetails = specialTicketDetails;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public void setBarcodeImage(byte[] barcodeImage) {
        this.barcodeImage = barcodeImage;
    }

    public void setBarcodeId(int barcodeId) {
        this.barcodeId = barcodeId;
    }

    public void setBarcodeString(String barcodeString) {
        this.barcodeString = barcodeString;
    }
}