package businesslogicnew.command.commands;

import businesslogicnew.users.ActiveUsers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoginWithSessionIdTest {
    @Test
    void testExecuteWorksCorrectly() {
        int sessionId = 2;
        ActiveUsers au = mock();
        when(au.sessionExists(sessionId)).thenReturn(true);

        LoginWithSessionId command = new LoginWithSessionId(sessionId, au);

        String res = command.execute();

        assertEquals("Logged in successfully", res);
    }
}
