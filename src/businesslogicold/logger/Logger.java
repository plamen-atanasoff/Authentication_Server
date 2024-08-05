package businesslogicold.logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Logger {
    private static int logId;
    private static final String LOGS_DIRECTORY = "logs";
    private static final String LOG_ID_FILE = "logId.txt";

    static {
        Path dir = Path.of(LOGS_DIRECTORY);
        Path path = Path.of(LOG_ID_FILE);

        try {
            if (!Files.exists(dir)) {
                Files.createDirectory(dir);
                Files.createFile(path);
                logId = 0;
            } else {
                var reader = Files.newBufferedReader(path);
                logId = Integer.parseInt(reader.readLine());
                reader.close();
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public static void createLog(String messageLog) {
        Path filePath = Path.of(LOGS_DIRECTORY + File.separator + logId++ + ".txt");

        try (var bufferedWriterLog = Files.newBufferedWriter(filePath);
             var bufferedWriterLogId = Files.newBufferedWriter(Path.of(LOG_ID_FILE))) {
            bufferedWriterLog.write(messageLog);
            bufferedWriterLog.flush();

            bufferedWriterLogId.write(String.valueOf(logId));
            bufferedWriterLog.flush();
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while writing to a file", e);
        }
    }
}
