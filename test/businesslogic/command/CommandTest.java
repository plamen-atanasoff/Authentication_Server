package businesslogic.command;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommandTest {
    @Test
    void testOfReturnsCorrectCommand() {
        String line = "register --username usernameTest --password passwordTest " +
            "--first-name firstNameTest --last-name lastNameTest --email emailTest";

        Command res = Command.of(line);
        Map<String, String> args = res.arguments();

        assertEquals("register", res.command());
        assertEquals(5, args.size());
        assertTrue(args.containsKey("username"));
        assertTrue(args.containsKey("password"));
        assertTrue(args.containsKey("first-name"));
        assertTrue(args.containsKey("last-name"));
        assertTrue(args.containsKey("email"));
        assertEquals("usernameTest", args.get("username"));
        assertEquals("passwordTest", args.get("password"));
        assertEquals("firstNameTest", args.get("first-name"));
        assertEquals("lastNameTest", args.get("last-name"));
        assertEquals("emailTest", args.get("email"));
    }
}
