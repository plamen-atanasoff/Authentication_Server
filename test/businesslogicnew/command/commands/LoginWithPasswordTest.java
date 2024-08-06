package businesslogicnew.command.commands;

import businesslogicnew.passwordencryptor.PasswordEncryptor;
import businesslogicnew.database.User;
import businesslogicnew.database.UserCredentials;
import businesslogicnew.database.UserDatabase;
import businesslogicnew.users.ActiveUsers;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoginWithPasswordTest {
    @Test
    void testExecuteWorksCorrectly() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String username = "pavel123";
        String password = "myPass";
        int sessionId = 2;
        String passwordHash = PasswordEncryptor.generateHash(password);
        UserCredentials credentials = mock();
        when(credentials.passwordHash()).thenReturn(passwordHash);
        User u = mock();
        when(u.credentials()).thenReturn(credentials);
        UserDatabase db = mock();
        when(db.getUser(username)).thenReturn(u);
        ActiveUsers au = mock();
        when(au.addSession()).thenReturn(sessionId);

        LoginWithPassword command = new LoginWithPassword(username, password, db, au);

        String res = command.execute();

        assertEquals(sessionId, Integer.parseInt(res));
    }
}
