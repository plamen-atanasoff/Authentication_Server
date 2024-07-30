package businesslogicnew.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandTypeTest {
    @Test
    void testGetAsTypeReturnsCorrectly() {
        CommandType res = CommandType.getAsType("login");

        assertEquals(CommandType.LOGIN, res, "Returned type should be correct");
    }
}
