package bg.sofia.uni.fmi.mjt.authenticationserver.businesslogic.tokenizer;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TokenizerTest {
    @Test
    void testTokenizeFromSocketChannelCommandWithAllArguments() {
        String line = "update-user --session-id session-idTest  --new-username newUsernameTest " +
            "--new-first-name newFirstNameTest --new-last-name newLastNameTest --new-email newEmailTest";
        Map<String, String> res = Tokenizer.tokenizeFromSocketChannel(line);

        assertTrue(res.containsKey("session-id"));
        assertTrue(res.containsKey("new-username"));
        assertTrue(res.containsKey("new-first-name"));
        assertTrue(res.containsKey("new-last-name"));
        assertTrue(res.containsKey("new-email"));

        assertEquals("session-idTest", res.get("session-id"));
        assertEquals("newUsernameTest", res.get("new-username"));
        assertEquals("newFirstNameTest", res.get("new-first-name"));
        assertEquals("newLastNameTest", res.get("new-last-name"));
        assertEquals("newEmailTest", res.get("new-email"));
    }

    @Test
    void testTokenizeFromSocketChannelCommandWithSomeArguments() {
        String line = "update-user --session-id session-idTest  --new-username newUsernameTest " +
            " --new-last-name newLastNameTest";
        Map<String, String> res = Tokenizer.tokenizeFromSocketChannel(line);

        assertTrue(res.containsKey("session-id"));
        assertTrue(res.containsKey("new-username"));
        assertTrue(res.containsKey("new-last-name"));

        assertEquals("session-idTest", res.get("session-id"));
        assertEquals("newUsernameTest", res.get("new-username"));
        assertEquals("newLastNameTest", res.get("new-last-name"));
    }

    @Test
    void testTokenizeFromDatabaseBehavesCorrectly() {
        String line = "vik40,\"123456\",Viktor,Atanasov,email@abv.bg,0";
        String[] res = Tokenizer.tokenizeFromDatabase(line);

        assertEquals(6, res.length);
    }
}
