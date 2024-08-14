package businesslogicnew.command.commands.creator;

import businesslogicnew.command.Command;
import businesslogicnew.command.CommandType;
import businesslogicnew.command.commands.ChangePassword;
import businesslogicnew.command.commands.LoginWithPassword;
import businesslogicnew.command.commands.LoginWithSessionId;
import businesslogicnew.command.commands.Logout;
import businesslogicnew.command.commands.Register;
import businesslogicnew.command.commands.Update;
import businesslogicnew.database.UserDatabase;
import businesslogicnew.logger.ServerLogger;
import businesslogicnew.users.ActiveUsers;

import java.nio.channels.SelectionKey;
import java.util.Map;

public enum Creator {

    LOGIN_PASSWORD(new LoginWithPassword.LoginWithPasswordCreator()),

    REGISTER(new Register.RegisterCreator()),

    LOGIN_SESSION_ID(new LoginWithSessionId.LoginWithSessionIdCreator()),

    UPDATE(new Update.UpdateCreator()),

    CHANGE_PASSWORD(new ChangePassword.ChangePasswordCreator());

    private final CommandCreator creator;

    Creator(CommandCreator creator) {
        this.creator = creator;
    }

    public CommandCreator getCreator() {
        return creator;
    }

    public static abstract class CommandCreator {
        protected static final String FORMAT_STRING = "Input tokens must be %d";

        private final CommandType type;

        protected CommandCreator(CommandType type) {
            this.type = type;
        }

        public abstract Command create(Map<String, String> input, UserDatabase users, ActiveUsers activeUsers,
                                       SelectionKey key, ServerLogger serverLogger);

        public CommandType getType() {
            return type;
        }
    }
}