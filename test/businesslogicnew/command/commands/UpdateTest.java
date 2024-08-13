package businesslogicnew.command.commands;

import businesslogicnew.database.User;
import businesslogicnew.database.UserCredentials;
import businesslogicnew.database.UserDatabase;
import businesslogicnew.users.ActiveUsers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpdateTest {

    @Test
    void testCommandExecutesCorrectly() throws IOException {
        int sessionId = 1;
        int userId = 2;
        boolean isAdmin = false;
        String username = "testUsername";
        String firstName = "testFirstName";
        String lastName = "testLastName";
        String email = "testEmail";
        String passwordHash = "testPasswordHash";

        Map<String, String> userArgs = mock();
        UserDatabase users = mock();
        ActiveUsers activeUsers = mock();
        User user = mock();
        UserCredentials credentials = mock();

        when(credentials.username()).thenReturn(username);
        when(credentials.passwordHash()).thenReturn(passwordHash);
        when(credentials.firstName()).thenReturn(firstName);
        when(credentials.lastName()).thenReturn(lastName);
        when(credentials.email()).thenReturn(email);

        when(user.id()).thenReturn(userId);
        when(user.isAdmin()).thenReturn(isAdmin);
        when(user.credentials()).thenReturn(credentials);

        when(userArgs.getOrDefault("username", credentials.username())).thenReturn(username);
        when(userArgs.getOrDefault("first-name", credentials.firstName())).thenReturn(firstName);
        when(userArgs.getOrDefault("last-name", credentials.lastName())).thenReturn(lastName);
        when(userArgs.getOrDefault("email", credentials.email())).thenReturn(email);

        when(activeUsers.sessionExists(sessionId)).thenReturn(true);
        when(activeUsers.getUserId(sessionId)).thenReturn(userId);

        when(users.getUser(userId)).thenReturn(user);

        Update command = new Update(sessionId, userArgs, users, activeUsers);

        assertEquals("User has been successfully updated", command.execute());

        ArgumentCaptor<User> updatedUserCaptor = ArgumentCaptor.forClass(User.class);
        verify(users).updateUser(updatedUserCaptor.capture());

        User updatedUser = updatedUserCaptor.getValue();

        assertEquals(userId, updatedUser.id());
        assertEquals(isAdmin, updatedUser.isAdmin());

        UserCredentials updatedCredentials = updatedUser.credentials();
        assertEquals(username, updatedCredentials.username());
        assertEquals(passwordHash, updatedCredentials.passwordHash());
        assertEquals(firstName, updatedCredentials.firstName());
        assertEquals(lastName, updatedCredentials.lastName());
        assertEquals(email, updatedCredentials.email());
    }
}
