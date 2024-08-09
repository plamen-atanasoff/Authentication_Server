package businesslogicnew.controller;

import businesslogicnew.command.Command;
import businesslogicnew.command.CommandFactory;
import businesslogicnew.database.UserDatabase;
import businesslogicnew.users.ActiveUsers;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.file.Path;

public class Controller {
    private static final String USER_DATABASE_FILE_NAME = "userDatabase.txt";

    private static final String WRONG_COMMAND_MESSAGE = "Wrong command";

    private static Controller controller;

    private final UserDatabase users;

    private final ActiveUsers activeUsers;

    public static void create(UserDatabase userDatabase, ActiveUsers activeUsers) {
        if (controller != null) {
            return;
        }

        controller = new Controller(userDatabase, activeUsers);
    }

    public static Controller getInstance() throws IOException {
        if (controller == null) {
            create(new UserDatabase(Path.of(USER_DATABASE_FILE_NAME)), new ActiveUsers());
        }

        return controller;
    }

    public String execute(String input, SelectionKey key) {
        return execute(CommandFactory.getInstance().createCommand(input, users, activeUsers, key));
    }

    public String execute(Command command) {
        if (command == null) {
            return WRONG_COMMAND_MESSAGE;
        }

        return command.execute();
    }

    private Controller(UserDatabase users, ActiveUsers activeUsers) {
        if (users == null || activeUsers == null) {
            throw new IllegalArgumentException("users or activeUsers is null");
        }

        this.users = users;
        this.activeUsers = activeUsers;
    }
}
