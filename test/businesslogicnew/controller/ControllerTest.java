package businesslogicnew.controller;

import businesslogicnew.command.Command;
import businesslogicnew.database.UserDatabase;
import businesslogicnew.users.ActiveUsers;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ControllerTest {
    @Test
    void testExecuteReturnsCorrectValue() throws IOException {
        UserDatabase userDatabase = mock();
        ActiveUsers activeUsers = mock();
        Controller.create(userDatabase, activeUsers);

        Controller controller = Controller.getInstance();
        String value = "Test value";
        Command command = mock();
        when(command.execute()).thenReturn(value);

        assertEquals(value, controller.execute(command));
    }

}
