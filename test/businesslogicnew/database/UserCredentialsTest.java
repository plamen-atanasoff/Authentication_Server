package businesslogicnew.database;

import businesslogicnew.passwordencryptor.PasswordEncryptor;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserCredentialsTest {
    @Test
    void testOfWorksCorrectly() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String passwordHashExpected = PasswordEncryptor.getInstance().generateHash("pass");

        String line = "bobby,\"" + passwordHashExpected + "\",Borislav,Petrov,bobi@abv.bg";

        UserCredentials credentials = UserCredentials.of(line);

        assertEquals("bobby", credentials.username());
        assertEquals(passwordHashExpected, credentials.passwordHash());
        assertEquals("Borislav", credentials.firstName());
        assertEquals("Petrov", credentials.lastName());
        assertEquals("bobi@abv.bg", credentials.email());
    }

    @Test
    void testGetFieldsCountReturnsCorrectValue() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String passwordHashExpected = PasswordEncryptor.getInstance().generateHash("pass");
        String line = "bobby,\"" + passwordHashExpected + "\",Borislav,Petrov,bobi@abv.bg";

        UserCredentials credentials = UserCredentials.of(line);

        assertEquals(5, credentials.getFieldsCount());
    }

}
