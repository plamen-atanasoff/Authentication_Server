package businesslogicnew.command;

import businesslogicnew.database.UserDatabase;
import businesslogicnew.users.ActiveUsers;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

public class CommandFactoryTest {
    @Test
    void testCommandIsCreatedCorrectly() throws IOException {

        UserDatabase users = mock();
        ActiveUsers activeUsers = mock();

        Command command = CommandFactory.getInstance().createCommand(
                "login -–username plamen100 --password password", users, activeUsers, null);

        assertNotNull(command, "CommandCreator does not exist");

    }
}
