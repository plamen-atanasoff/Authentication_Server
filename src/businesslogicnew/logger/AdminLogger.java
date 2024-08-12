package businesslogicnew.logger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class AdminLogger {

    private static final String ILLEGAL_ARGUMENT_MESSAGE = "handler or logger is null";

    private static final String LOG_FILE_NAME = "logs.txt";

    private static Logger logger;

    public static void setup() throws IOException {
        setup(LOG_FILE_NAME);
    }

    public static void setup(String logFileName) throws IOException {
        setup(new SimpleFormatter(), new FileHandler(logFileName), Logger.getLogger(AdminLogger.class.getName()));
    }

    public static void setup(Formatter formatter, FileHandler handler, Logger logger) {
        if (handler == null || logger == null) {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_MESSAGE);
        }

        if (AdminLogger.logger != null) {
            return;
        }

        handler.setFormatter(formatter);
        logger.addHandler(handler);
        logger.setLevel(Level.ALL);

        AdminLogger.logger = logger;
    }

    public static void log(String message) throws IOException {
        if (logger == null) {
            setup();
        }

        logger.info(message);
    }

}
