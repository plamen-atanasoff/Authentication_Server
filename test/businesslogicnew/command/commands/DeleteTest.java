package businesslogicnew.command.commands;

import businesslogicnew.command.Command;
import businesslogicnew.database.User;
import businesslogicnew.database.UserDatabase;
import businesslogicnew.users.ActiveUsers;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeleteTest {

    @Test
    void testCommandExecutesCorrectly() throws IOException {
        int sessionId = 1;
        int adminUserId = 2;
        int userId = 3;
        String username = "testUsername";
        UserDatabase users = mock();
        ActiveUsers activeUsers = mock();
        User adminUser = mock();
        User user = mock();

        when(activeUsers.sessionExists(sessionId)).thenReturn(true);
        when(activeUsers.getUserId(sessionId)).thenReturn(adminUserId);

        when(users.getUser(adminUserId)).thenReturn(adminUser);
        when(users.getUser(username)).thenReturn(user);
        when(users.deleteUser(userId)).thenReturn(true);

        when(adminUser.isAdmin()).thenReturn(true);
        when(user.id()).thenReturn(userId);

        Command command = new Delete(sessionId, username, users, activeUsers);
        String res = command.execute();

        verify(activeUsers, atMostOnce()).removeSessionByUserId(userId);
        verify(users, atMostOnce()).deleteUser(userId);

        assertEquals("User has been successfully deleted", res);
    }

}
