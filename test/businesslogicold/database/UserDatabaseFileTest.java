package businesslogicold.database;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDatabaseFileTest {
    private static final Path filePath = Path.of("UserDatabaseFileTest.txt");
    private UserDatabaseFile userDatabaseFile;

    private void setup(String... args) {
        try (var writer = Files.newBufferedWriter(filePath)) {
            writer.write("username,\"password\",firstName,lastName,email,isAdmin" + System.lineSeparator());
            for (String line : args) {
                writer.write(line);
            }
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        userDatabaseFile = new UserDatabaseFile(filePath);
    }

    @AfterAll
    static void cleanup() {
        try {
            Files.delete(filePath);
        } catch (IOException ignored) {}
    }

    @Test
    void testReadUsersCreatesCorrectSet() {
        String line1 = "plambataBG,\"123456\",Plamen,Atanasov,email@abv.bg,0" + System.lineSeparator();
        String line2 = "vik40,\"222222\",Viktor,Atanasov,email@abv.bg,0" + System.lineSeparator();
        String line3 = "bebi1234,\"555555\",Bancho,Stamenov,email@dir.bg,0" + System.lineSeparator();

        setup(line1, line2, line3);

        Set<UserCredentials> res = userDatabaseFile.readUsers();

        assertEquals(3, res.size());
    }

    @Test
    void testAddUserAddsNewUserCredentialsToFile() {
        String line1 = "plambataBG,\"123456\",Plamen,Atanasov,email@abv.bg,0" + System.lineSeparator();
        String line2 = "vik40,\"222222\",Viktor,Atanasov,email@abv.bg,0" + System.lineSeparator();
        UserCredentials userCredentials = UserCredentials.of("bebi1234,\"555555\",Bancho,Stamenov,email@dir.bg,0");

        setup(line1, line2);

        userDatabaseFile.addUser(userCredentials);

        Set<UserCredentials> users;
        try (var reader = Files.newBufferedReader(filePath)) {
            users = reader.lines()
                .skip(1)
                .map(UserCredentials::of)
                .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        assertEquals(3, users.size());
        assertTrue(users.contains(userCredentials));
    }

    @Test
    void testRemoveUserRemovesExistingUserFromDatabaseFile() {
        String line1 = "plambataBG,\"123456\",Plamen,Atanasov,email@abv.bg,0" + System.lineSeparator();
        String line2 = "vik40,\"222222\",Viktor,Atanasov,email@abv.bg,0" + System.lineSeparator();
        String username = "plambataBG";

        setup(line1, line2);

        userDatabaseFile.removeUser(username);

        Set<UserCredentials> users;
        try (var reader = Files.newBufferedReader(filePath)) {
            users = reader.lines()
                .skip(1)
                .map(UserCredentials::of)
                .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        assertEquals(1, users.size());
        assertTrue(users.stream().noneMatch(u -> u.username().equals(username)));
    }
}
