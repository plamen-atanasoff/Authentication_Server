package businesslogicnew.database;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class FileManager {

    private final String fileName;

    private static final String SEPARATOR = ",";

    public FileManager(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("File name is null");
        }

        this.fileName = fileName;
    }

    public void writeUser(UserCredentials user) throws IOException {
        try (var bufferedWriter = Files.newBufferedWriter(
                Path.of(fileName), StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            String[] values = user.getValues();
            bufferedWriter.write(String.join(SEPARATOR, values));
            bufferedWriter.newLine();
        }
    }

    public boolean userExists(int userId) throws IOException {
        try (var bufferedReader = Files.newBufferedReader(Path.of(fileName))) {
            return bufferedReader.lines()
                    .skip(1)
                    .map(s -> Integer.parseInt(s.substring(0, s.indexOf(SEPARATOR))))
                    .anyMatch(i -> i.equals(userId));
        }
    }

}
