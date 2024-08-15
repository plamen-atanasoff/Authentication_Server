package businesslogicnew.command.commands;

import businesslogicnew.command.Command;
import businesslogicnew.database.User;
import businesslogicnew.database.UserCredentials;
import businesslogicnew.database.UserDatabase;
import businesslogicnew.logger.ServerLogger;
import businesslogicnew.users.ActiveUsers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RemoveAdminTest {

    @Test
    void testCommandExecutesCorrectly() throws IOException {
        int sessionId = 1;
        int adminUserId = 2;
        int userId = 3;
        String username = "testUsername";
        UserDatabase users = mock();
        ActiveUsers activeUsers = mock();
        ServerLogger logger = mock();
        User adminUser = mock();
        User user = mock();
        UserCredentials userCredentials = mock();

        when(activeUsers.sessionExists(sessionId)).thenReturn(true);
        when(activeUsers.getUserId(sessionId)).thenReturn(adminUserId);

        when(users.getUser(adminUserId)).thenReturn(adminUser);
        when(users.getUser(username)).thenReturn(user);

        when(adminUser.isAdmin()).thenReturn(true);
        when(user.id()).thenReturn(userId);
        when(user.isAdmin()).thenReturn(true);
        when(user.credentials()).thenReturn(userCredentials);

        Command command = new RemoveAdmin(sessionId, username, users, activeUsers, logger);
        String res = command.execute();

        ArgumentCaptor<User> updatedUserCaptor = ArgumentCaptor.forClass(User.class);
        verify(users).updateUser(updatedUserCaptor.capture());

        User updatedUser = updatedUserCaptor.getValue();

        assertEquals(userId, updatedUser.id());
        assertFalse(updatedUser.isAdmin());
        assertEquals(userCredentials, updatedUser.credentials());

        verify(logger, atMostOnce()).log(username + " denied admin status");

        assertEquals("The user has successfully been denied admin status", res);
    }
}
