package businesslogicnew.logger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ServerLogger {

    private final Logger logger;

    public ServerLogger(Logger logger) {
        this.logger = logger;
    }

    public void log(String message) throws IOException {
        logger.info(message);
    }

    public static class ServerLoggerBuilder {

        private static final String ILLEGAL_ARGUMENT_MESSAGE = "Argument is null";

        private String logFileName = "logs.txt";

        private Formatter formatter = new SimpleFormatter();

        public ServerLoggerBuilder setLogFileName(String logFileName) {
            validate(logFileName);

            this.logFileName = logFileName;

            return this;
        }

        public ServerLoggerBuilder setFormatter(Formatter formatter) {
            validate(formatter);

            this.formatter = formatter;

            return this;
        }

        private void validate(Object o) {
            if (o == null) {
                throw new IllegalArgumentException(ILLEGAL_ARGUMENT_MESSAGE);
            }
        }

        public ServerLogger build() throws IOException {
            FileHandler handler = new FileHandler(logFileName);
            Logger logger = Logger.getLogger(ServerLogger.class.getName());

            handler.setFormatter(formatter);
            logger.addHandler(handler);
            logger.setLevel(Level.ALL);

            return new ServerLogger(logger);
        }
    }
}
