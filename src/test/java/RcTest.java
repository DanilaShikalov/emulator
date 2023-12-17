import come.example.utitled.emulator.Emulator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RcTest {

    @Test
    public void binaryToDecimalTest() {
        assertEquals(Emulator.binaryToDecimal("0111"), "7");
    }
}
