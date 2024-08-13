package businesslogicnew.command.commands;

import businesslogicnew.passwordencryptor.PasswordEncryptor;
import businesslogicnew.database.UserCredentials;
import businesslogicnew.database.UserDatabase;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class RegisterTest {
    @Test
    void testCommandExecutesCorrectly() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String username = "pavel123";
        String passwordHash = PasswordEncryptor.getInstance().generateHash("myPass");
        String firstName = "Pavel";
        String lastName = "Pavlovich";
        String email = "myemail@abv.bg";
        UserDatabase db = mock();

        Register command = new Register(username, passwordHash, firstName, lastName, email, db);

        String res = command.execute();
        assertEquals("User has been successfully registered", res);

        verify(db, atMostOnce()).getUser(username);
        verify(db, atMostOnce()).addUser(any(UserCredentials.class));
    }

}
