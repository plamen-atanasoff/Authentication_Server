package businesslogicnew.command.commands;

import businesslogicnew.command.Command;
import businesslogicnew.database.User;
import businesslogicnew.database.UserCredentials;
import businesslogicnew.database.UserDatabase;
import businesslogicnew.passwordencryptor.PasswordEncryptor;
import businesslogicnew.users.ActiveUsers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChangePasswordTest {
    @Test
    void testCommandExecutesCorrectly() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        int userId = 2;
        int sessionId = 1;
        String username = "testUsername";
        String oldPassword = "testOldPassword";
        String oldPasswordHash = PasswordEncryptor.generateHash(oldPassword);
        String newPassword = "testNewPassword";
        String newPasswordHash = PasswordEncryptor.generateHash(newPassword);
        String firstName = "testFirstName";
        String lastName = "testLastName";
        String email = "testEmail";
        boolean isAdmin = false;

        UserDatabase users = mock();
        ActiveUsers activeUsers = mock();
        User user = mock();
        UserCredentials credentials = mock();

        when(activeUsers.sessionExists(sessionId)).thenReturn(true);
        when(activeUsers.getUserId(sessionId)).thenReturn(userId);

        when(users.getUser(username)).thenReturn(user);

        when(user.id()).thenReturn(userId);
        when(user.credentials()).thenReturn(credentials);
        when(user.isAdmin()).thenReturn(isAdmin);

        when(credentials.username()).thenReturn(username);
        when(credentials.passwordHash()).thenReturn(oldPasswordHash);
        when(credentials.firstName()).thenReturn(firstName);
        when(credentials.lastName()).thenReturn(lastName);
        when(credentials.email()).thenReturn(email);

        Command command = new ChangePassword(sessionId, username, oldPassword, newPassword, users, activeUsers);

        assertEquals("Password has been successfully changed", command.execute());

        ArgumentCaptor<User> updatedUserCaptor = ArgumentCaptor.forClass(User.class);
        verify(users).updateUser(updatedUserCaptor.capture());

        User updatedUser = updatedUserCaptor.getValue();

        assertEquals(userId, updatedUser.id());
        assertEquals(isAdmin, updatedUser.isAdmin());

        UserCredentials updatedCredentials = updatedUser.credentials();
        assertEquals(username, updatedCredentials.username());
        assertEquals(newPasswordHash, updatedCredentials.passwordHash());
        assertEquals(firstName, updatedCredentials.firstName());
        assertEquals(lastName, updatedCredentials.lastName());
        assertEquals(email, updatedCredentials.email());
    }
}
