package businesslogicnew.command.commands;

import businesslogicnew.command.Command;
import businesslogicnew.users.ActiveUsers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LogoutTest {
    @Test
    void testCommandExecutesCorrectly() {
        int sessionId = 1;
        ActiveUsers activeUsers = mock();

        when(activeUsers.removeSession(sessionId)).thenReturn(sessionId);

        Command command = new Logout(sessionId, activeUsers);

        assertEquals("User has been successfully logged out", command.execute());
    }
}
