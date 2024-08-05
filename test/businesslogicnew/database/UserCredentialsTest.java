package businesslogicnew.database;

import businesslogicold.passwordencryptor.PasswordEncryptor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserCredentialsTest {
    @Test
    void testOfWorksCorrectly() {
        String passwordHashExpected = PasswordEncryptor.encryptPassword("pass");

        String line = "bobby,\"" + passwordHashExpected + "\",Borislav,Petrov,bobi@abv.bg";

        UserCredentials credentials = UserCredentials.of(line);

        assertEquals("bobby", credentials.username());
        assertEquals(passwordHashExpected, credentials.passwordHash());
        assertEquals("Borislav", credentials.firstName());
        assertEquals("Petrov", credentials.lastName());
        assertEquals("bobi@abv.bg", credentials.email());
    }

    @Test
    void testGetFieldsCountReturnsCorrectValue() {
        String passwordHashExpected = PasswordEncryptor.encryptPassword("pass");
        String line = "bobby,\"" + passwordHashExpected + "\",Borislav,Petrov,bobi@abv.bg";

        UserCredentials credentials = UserCredentials.of(line);

        assertEquals(5, credentials.getFieldsCount());
    }

}
