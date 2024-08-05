package businesslogicnew.database;

import businesslogicold.passwordencryptor.PasswordEncryptor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class UserTest {
    @Test
    void testOfWorksCorrectly() {
        String passwordHash = PasswordEncryptor.encryptPassword("mypass");
        String credentialsLine = "plamen40,\"" + passwordHash + "\",Plamen,Petrov,email@abv.bg";
        String line = "1,0," + credentialsLine;
        UserCredentials credentials = UserCredentials.of(credentialsLine);

        User user = User.of(line);

        assertEquals(1, user.id());
        assertFalse(user.isAdmin());
        assertEquals(credentials, user.credentials());
    }

    @Test
    void testToStringReturnsCorrectValue() {
        String passwordHash = PasswordEncryptor.encryptPassword("mypass");
        String credentialsLine = "plamen40,\"" + passwordHash + "\",Plamen,Petrov,email@abv.bg";
        String line = "1,0," + credentialsLine;

        User user = User.of(line);

        assertEquals(line, user.toString());
    }
}
