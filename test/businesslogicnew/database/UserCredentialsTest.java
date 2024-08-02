package businesslogicnew.database;

import businesslogic.passwordencryptor.PasswordEncryptor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class UserCredentialsTest {
    @Test
    void testOfWorksCorrectly() {
        String passwordHashExpected = PasswordEncryptor.encryptPassword("pass");

        String line = "bobby,\"" + passwordHashExpected + "\",Borislav,Petrov,bobi@abv.bg,0";

        UserCredentials user = UserCredentials.of(line);

        assertEquals("bobby", user.username());
        assertEquals(passwordHashExpected, user.passwordHash());
        assertEquals("Borislav", user.firstName());
        assertEquals("Petrov", user.lastName());
        assertEquals("bobi@abv.bg", user.email());
    }
}
