package be;

public class TicketType {
    private int ticketTypeId;
    private String ticketTypeName;

    public TicketType(int ticketTypeId, String ticketTypeName) {
        this.ticketTypeId = ticketTypeId;
        this.ticketTypeName = ticketTypeName;
    }

    public int getTicketTypeId() {
        return ticketTypeId;
    }

    public String getTicketTypeName() {
        return ticketTypeName;
    }

    public void setTicketTypeId(int ticketTypeId) {
        this.ticketTypeId = ticketTypeId;
    }

    public void setTicketTypeNameName(String ticketTypeName) {
        this.ticketTypeName = ticketTypeName;
    }
}