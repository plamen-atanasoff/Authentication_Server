package businesslogicnew.tokenizer;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TokenizerTest {
    @Test
    void testTokenizeFromSocketChannelWorksCorrectly() {
        String input = "username pesho123 --password mypass " +
            "--first-name Petur --last-name Velikov --email myemail@gmail.com";

        Map<String, String> res = Tokenizer.tokenizeFromSocketChannel(input);

        assertEquals(5, res.size());

        assertEquals("pesho123", res.get("username"));
        assertEquals("mypass", res.get("password"));
        assertEquals("Petur", res.get("first-name"));
        assertEquals("Velikov", res.get("last-name"));
        assertEquals("myemail@gmail.com", res.get("email"));
    }

    @Test
    void testTokenizeFromSocketChannelThrowsCorrectly() {
        assertThrows(RuntimeException.class, () -> Tokenizer.tokenizeFromSocketChannel("username"));
    }
}
