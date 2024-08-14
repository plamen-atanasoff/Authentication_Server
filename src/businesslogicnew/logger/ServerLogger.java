package businesslogicnew.logger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

public class ServerLogger {

    private static final String ILLEGAL_ARGUMENT_MESSAGE = "Handler or logger is null";

    private static final String LOG_FILE_NAME = "logs.txt";

    private static java.util.logging.Logger logger;

    private static ServerLogger serverLogger;

    public static ServerLogger getInstance() throws IOException {
        if (serverLogger == null) {
            serverLogger = new ServerLogger();
        }

        return serverLogger;
    }

    public static void setup() throws IOException {
        setup(LOG_FILE_NAME);
    }

    public static void setup(String logFileName) throws IOException {
        setup(new SimpleFormatter(), new FileHandler(logFileName), java.util.logging.Logger.getLogger(ServerLogger.class.getName()));
    }

    public static void setup(Formatter formatter, FileHandler handler, java.util.logging.Logger logger) {
        if (handler == null || logger == null) {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_MESSAGE);
        }

        if (ServerLogger.logger != null) {
            return;
        }

        handler.setFormatter(formatter);
        logger.addHandler(handler);
        logger.setLevel(Level.ALL);

        ServerLogger.logger = logger;
    }

    public void log(String message) throws IOException {
        logger.info(message);
    }

    private ServerLogger() throws IOException {
        setup();
    }
}
