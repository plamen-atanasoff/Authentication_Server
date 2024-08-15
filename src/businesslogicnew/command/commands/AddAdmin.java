package businesslogicnew.command.commands;

import businesslogicnew.command.Command;
import businesslogicnew.command.CommandType;
import businesslogicnew.command.commands.creator.Creator;
import businesslogicnew.database.UserDatabase;
import businesslogicnew.logger.ServerLogger;
import businesslogicnew.users.ActiveUsers;

import java.nio.channels.SelectionKey;
import java.util.Map;

public class AddAdmin extends ChangeAdminStatus implements Command {

    public AddAdmin(int sessionId, String username, UserDatabase users, ActiveUsers activeUsers, ServerLogger logger) {
        super(sessionId, username, users, activeUsers, logger);
    }

    @Override
    public String execute() {
        return changeAdminStatus(true);
    }

    public static class AddAdminCreator extends Creator.CommandCreator {

        public AddAdminCreator() {
            super(CommandType.ADD_ADMIN);
        }

        @Override
        public Command create(Map<String, String> input, UserDatabase users, ActiveUsers activeUsers, SelectionKey key,
                              ServerLogger serverLogger) {
            ChangeAdminStatusValidator.validate(input);

            return new AddAdmin(Integer.parseInt(input.get(ChangeAdminStatusValidator.SESSION_ID_STRING)),
                input.get(ChangeAdminStatusValidator.USERNAME_STRING), users, activeUsers, serverLogger);
        }
    }
}
