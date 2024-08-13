package businesslogicnew.command.commands;

import businesslogicnew.command.Command;
import businesslogicnew.command.CommandType;
import businesslogicnew.database.UserDatabase;
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
                                       SelectionKey key);

        public CommandType getType() {
            return type;
        }
    }
}
