package businesslogicnew.logger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;

import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;

import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ServerLoggerTest {
    @Test
    void testLogExecutesCorrectly() {
        String message = "log test message";
        Formatter formatter = mock();
        FileHandler handler = mock();
        Logger logger = mock();

        ServerLogger serverLogger = new ServerLogger(logger);

        assertDoesNotThrow(() -> serverLogger.log(message));

        verify(logger, atMostOnce()).info(message);
    }
}
