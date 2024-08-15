package businesslogicnew.command.commands;

import businesslogicnew.command.Command;
import businesslogicnew.command.CommandType;
import businesslogicnew.command.commands.creator.Creator;
import businesslogicnew.database.UserDatabase;
import businesslogicnew.logger.ServerLogger;
import businesslogicnew.users.ActiveUsers;

import java.nio.channels.SelectionKey;
import java.util.Map;

public class RemoveAdmin extends ChangeAdminStatus implements Command {

    public RemoveAdmin(int sessionId, String username, UserDatabase users, ActiveUsers activeUsers,
                       ServerLogger logger) {
        super(sessionId, username, users, activeUsers, logger);
    }
    @Override
    public String execute() {
        return changeAdminStatus(false);
    }

    public static class RemoveAdminCreator extends Creator.CommandCreator {

        public RemoveAdminCreator() {
            super(CommandType.REMOVE_ADMIN);
        }

        @Override
        public Command create(Map<String, String> input, UserDatabase users, ActiveUsers activeUsers, SelectionKey key,
                              ServerLogger serverLogger) {
            ChangeAdminStatusValidator.validate(input);

            return new RemoveAdmin(Integer.parseInt(input.get(ChangeAdminStatusValidator.SESSION_ID_STRING)),
                input.get(ChangeAdminStatusValidator.USERNAME_STRING), users, activeUsers, serverLogger);
        }
    }
}
