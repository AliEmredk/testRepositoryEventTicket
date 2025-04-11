package be;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

    public class TicketTest {

        @Test
        public void testTicketCreationAndGetters() {
            // Arrange: prepare test data
            int ticketId = 1;
            TicketType ticketType = new TicketType(2, "VIP");
            int barcodeId = 123;
            byte[] barcodeImage = {0x01, 0x02};
            String barcodeString = "123456789012";
            int eventId = 456;
            String eventName = "Concert";
            String location = "Stadium";
            String eventDate = "2025-04-06";
            String startTime = "19:00";
            String endTime = "22:00";
            String eventNote = "Bring ID";
            int customerId = 789;
            String firstName = "Jane";
            String lastName = "Doe";
            String email = "jane.doe@example.com";
            int discount = 0;
            String details = "Free slippers";

            // create Ticket object
            Ticket ticket = new Ticket(ticketId, ticketType, barcodeId, barcodeImage, barcodeString,
                    eventId, eventName, location, eventDate, startTime, endTime, eventNote,
                    customerId, firstName, lastName, email, discount, details);

            // check if getters return correct values
            assertEquals(ticketId, ticket.getTicketId());
            assertEquals(ticketType, ticket.getTicketType());
            assertEquals(barcodeId, ticket.getBarcodeId());
            assertArrayEquals(barcodeImage, ticket.getBarcodeImage());
            assertEquals(barcodeString, ticket.getBarcodeString());
            assertEquals(eventId, ticket.getEventId());
            assertEquals(eventName, ticket.getEventName());
            assertEquals(location, ticket.getLocation());
            assertEquals(eventDate, ticket.getEventDate());
            assertEquals(startTime, ticket.getStartTime());
            assertEquals(endTime, ticket.getEndTime());
            assertEquals(eventNote, ticket.getEventNote());
            assertEquals(customerId, ticket.getCustomerId());
            assertEquals(firstName, ticket.getFirstName());
            assertEquals(lastName, ticket.getLastName());
            assertEquals(email, ticket.getEmail());
            assertEquals(discount, ticket.getDiscount());
            assertEquals(details, ticket.getDetails());
        }
    }
