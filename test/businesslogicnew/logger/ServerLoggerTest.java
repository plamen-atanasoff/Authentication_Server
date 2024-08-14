package businesslogicnew.logger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;

import java.util.logging.FileHandler;
import java.util.logging.Formatter;

import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ServerLoggerTest {
    @Test
    void testLogExecutesCorrectly() {
        String message = "log test message";
        Formatter formatter = mock();
        FileHandler handler = mock();
        java.util.logging.Logger logger = mock();

        ServerLogger.setup(formatter, handler, logger);

        assertDoesNotThrow(() -> ServerLogger.getInstance().log(message));

        verify(logger, atMostOnce()).info(message);
    }
}
