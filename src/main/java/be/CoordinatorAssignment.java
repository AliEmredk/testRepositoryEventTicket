package be;

public class CoordinatorAssignment {
    private String username;
    private String eventName;

    public CoordinatorAssignment(String username, String eventName) {
        this.username = username;
        this.eventName = eventName;
    }

    public String getUsername() {
        return username;
    }

    public String getEventName() {
        return eventName;
    }

    @Override
    public String toString() {
        return username + " -> " + eventName;
    }

}
