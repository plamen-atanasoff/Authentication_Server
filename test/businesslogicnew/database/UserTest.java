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
    void testGetValuesReturnsCorrectValues() {
        String passwordHash = PasswordEncryptor.encryptPassword("mypass");
        String credentialsLine = "plamen40,\"" + passwordHash + "\",Plamen,Petrov,email@abv.bg";
        String line = "1,0," + credentialsLine;
        UserCredentials credentials = UserCredentials.of(credentialsLine);

        User user = User.of(line);

        String[] values = user.getValues();

        assertEquals(1, Integer.parseInt(values[0]));
        assertFalse(Boolean.parseBoolean(values[1]));
        assertEquals(credentials.username(), values[2]);
        assertEquals(credentials.passwordHash(), values[3]);
        assertEquals(credentials.firstName(), values[4]);
        assertEquals(credentials.lastName(), values[5]);
        assertEquals(credentials.email(), values[6]);
    }
}
