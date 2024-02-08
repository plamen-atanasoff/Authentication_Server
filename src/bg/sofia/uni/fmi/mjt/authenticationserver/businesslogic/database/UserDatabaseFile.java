package bg.sofia.uni.fmi.mjt.authenticationserver.businesslogic.database;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDatabaseFile {
    private static final String IO_EXCEPTION_MESSAGE = "a problem occurred while reading from the file";
    private final Path fileDatabasePath;

    public UserDatabaseFile(Path fileDatabasePath) {
        this.fileDatabasePath = fileDatabasePath;
        if (Files.notExists(fileDatabasePath)) {
            try (var writer = Files.newBufferedWriter(fileDatabasePath)) {
                writer.write("username,\"password\",firstName,lastName,email,adminStatus" + System.lineSeparator());
                writer.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Set<UserCredentials> readUsers() {
        try (var reader = Files.newBufferedReader(fileDatabasePath)) {
            return reader.lines()
                .skip(1)
                .map(UserCredentials::of)
                .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new UncheckedIOException(IO_EXCEPTION_MESSAGE, e);
        }
    }

    public void addUser(UserCredentials userCredentials) {
        try (var writer = Files.newBufferedWriter(fileDatabasePath, StandardOpenOption.APPEND)) {
            String line = userCredentials.toString() + System.lineSeparator();
            writer.write(line);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeUser(String username) {
        try (var reader = Files.newBufferedReader(fileDatabasePath)) {
            String line = reader.readLine();

            StringBuilder text = new StringBuilder(line).append(System.lineSeparator());
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(username + ",")) {
                    continue;
                }

                text.append(line).append(System.lineSeparator());
            }

            var writer = Files.newBufferedWriter(fileDatabasePath);
            writer.write(String.valueOf(text));

            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
