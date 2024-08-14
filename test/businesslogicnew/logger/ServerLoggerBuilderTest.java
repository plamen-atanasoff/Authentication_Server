package businesslogicnew.logger;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ServerLoggerBuilderTest {

    @Test
    void testBuildDoesNotThrowDefault() {

        ServerLogger.ServerLoggerBuilder builder = new ServerLogger.ServerLoggerBuilder();

        assertDoesNotThrow(builder::build);
    }
}
