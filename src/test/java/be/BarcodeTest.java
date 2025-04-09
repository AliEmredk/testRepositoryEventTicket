package be;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BarcodeTest {

    @Test
    public void testGenerateCode() {
        Barcode barcode = new Barcode(); // Constructor
        String result = barcode.generateCode("event");
        assertEquals("CODE-EVENT", result); // Result
    }
}