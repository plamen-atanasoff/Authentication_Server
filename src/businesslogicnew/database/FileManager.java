package businesslogicnew.database;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private static final String NULL_FILE_PATH_MESSAGE = "File path cannot be null";

    private static final String FILE_NOT_FOUND_MESSAGE = "File does not exist";

    private static final String SEPARATOR = ",";

    private static final String FIRST_ROW = "id,adminStatus,username,\"password\",firstName,lastName,email";

    private final Path filePath;

    public FileManager(Path filePath) throws FileNotFoundException {
        if (filePath == null) {
            throw new IllegalArgumentException(NULL_FILE_PATH_MESSAGE);
        }

        if (Files.notExists(filePath)) {
            throw new FileNotFoundException(FILE_NOT_FOUND_MESSAGE);
        }

        this.filePath = filePath;
    }

    public User readUser(String username) throws IOException {
        try (var bufferedReader = Files.newBufferedReader(filePath)) {
            return bufferedReader.lines()
                .skip(1)
                .map(User::of)
                .filter(u -> u.credentials().username().equals(username))
                .findFirst()
                .orElse(null);
        }
    }

    public User readUser(int userId) throws IOException {
        try (var bufferedReader = Files.newBufferedReader(filePath)) {
            return bufferedReader.lines()
                .skip(1)
                .map(User::of)
                .filter(u -> u.id() == userId)
                .findFirst()
                .orElse(null);
        }
    }

    public void writeUser(User user) throws IOException {
        try (var bufferedWriter = Files.newBufferedWriter(
            filePath, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {

            bufferedWriter.write(user.toString());
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

    public void updateUser(User user) throws IOException {
        modifyUser(user.id(), user);
    }

    public void deleteUser(int userId) throws IOException {
        modifyUser(userId, null);
    }

    private void modifyUser(int userId, User user) throws IOException {
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
                bufferedWriter.write(user.toString());
                bufferedWriter.newLine();
            }

            for (int i = count; i < size - 1; i++) {
                bufferedWriter.write(lines.get(i));
                bufferedWriter.newLine();
            }
        }
    }

    public int readId() throws IOException {
        try (var bufferedReader = Files.newBufferedReader(filePath)) {
            return bufferedReader.lines()
                .skip(1)
                .map(l -> Integer.parseInt(l.substring(0, l.indexOf(SEPARATOR))))
                .max(Integer::compareTo)
                .orElse(0);
        }
    }
}
