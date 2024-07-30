package businesslogicnew.command;

import businesslogic.database.UserDatabase;
import businesslogicnew.users.ActiveUsers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

public class CommandFactoryTest {
    @Test
    void testCommandIsCreatedCorrectly() {
        UserDatabase users = mock();
        ActiveUsers activeUsers = mock();

        Command command = CommandFactory.getInstance().createCommand(
                "login -–username plamen100 --password password", users, activeUsers);

        assertNotNull(command, "CommandCreator does not exist");

    }
}
