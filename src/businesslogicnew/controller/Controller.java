package businesslogicnew.controller;

import businesslogicnew.command.Command;
import businesslogicnew.command.CommandFactory;
import businesslogic.database.UserDatabase;
import businesslogic.database.UserDatabaseFile;
import businesslogicnew.users.ActiveUsers;

import java.nio.file.Path;

public class Controller {
    private static final String USER_DATABASE_FILE_NAME = "userDatabase.txt";
    private static final String WRONG_COMMAND_MESSAGE = "Wrong command";

    private static Controller controller;
    private final UserDatabase users;
    private final ActiveUsers activeUsers;

    public static Controller getInstance() {
        if (controller == null) {
            controller = new Controller();
        }

        return controller;
    }
    public String execute(String input) {
        Command command = CommandFactory.getInstance().createCommand(input, users, activeUsers);
        if (command == null) {
            return WRONG_COMMAND_MESSAGE;
        }

        return command.execute();
    }

    private Controller() {
        this.users = new UserDatabase(new UserDatabaseFile(Path.of(USER_DATABASE_FILE_NAME)));
        this.activeUsers = new ActiveUsers();
    }
}
