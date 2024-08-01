package businesslogicnew.database;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private final Path filePath;

    private static final String SEPARATOR = ",";

    private static final String FIRST_ROW = "username,\"password\",firstName,lastName,email,adminStatus";

    public FileManager(Path filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("File name is null");
        }

        this.filePath = filePath;
    }

    public void writeUser(UserCredentials user) throws IOException {
        try (var bufferedWriter = Files.newBufferedWriter(
            filePath, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            String[] values = user.getValues();
            bufferedWriter.write(String.join(SEPARATOR, values));
            bufferedWriter.newLine();
        }
    }

    public boolean userExists(int userId) throws IOException {
        try (var bufferedReader = Files.newBufferedReader(filePath)) {
            return bufferedReader.lines()
                    .skip(1)
                    .map(s -> Integer.parseInt(s.substring(0, s.indexOf(SEPARATOR))))
                    .anyMatch(i -> i.equals(userId));
        }
    }

    public void updateUser(UserCredentials user) throws IOException {
        modifyUser(user.id(), user);
    }

    public void deleteUser(int userId) throws IOException {
        modifyUser(userId, null);
    }

    private void modifyUser(int userId, UserCredentials user) throws IOException {
        List<String> lines = new ArrayList<>(Files.readAllLines(filePath).stream().skip(1).toList());

        List<Integer> linesUserId = lines.stream()
            .map(s -> Integer.parseInt(s.substring(0, s.indexOf(SEPARATOR))))
            .toList();

        int size = lines.size(), count = 0;
        for (int i = 0; i < size; i++) {
            if (linesUserId.get(i) == userId) {
                lines.remove(i);
                break;
            } else {
                count++;
            }
        }

        try (var bufferedWriter = Files.newBufferedWriter(filePath)) {
            bufferedWriter.write(FIRST_ROW);
            bufferedWriter.newLine();

            for (int i = 0; i < count; i++) {
                bufferedWriter.write(lines.get(i));
                bufferedWriter.newLine();
            }

            if (user != null) {
                String[] values = user.getValues();
                bufferedWriter.write(String.join(SEPARATOR, values));
                bufferedWriter.newLine();
            }

            for (int i = count; i < size - 1; i++) {
                bufferedWriter.write(lines.get(i));
                bufferedWriter.newLine();
            }
        }
    }
}
