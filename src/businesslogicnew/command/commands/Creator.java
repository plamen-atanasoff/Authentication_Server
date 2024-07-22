package businesslogicnew.command.commands;

import businesslogicnew.command.Command;
import businesslogicnew.command.CommandType;
import businesslogic.database.UserDatabase;
import businesslogicnew.users.ActiveUsers;

import java.util.Map;

public enum Creator {
    LOGIN(new LoginWithPassword.LoginWithPasswordCreator());

    private final CommandCreator creator;

    Creator(CommandCreator creator) {
        this.creator = creator;
    }

    public CommandCreator getCreator() {
        return creator;
    }

    public static abstract class CommandCreator {
        private final CommandType type;

        protected CommandCreator(CommandType type) {
            this.type = type;
        }

        public abstract Command create(Map<String, String> input, UserDatabase users, ActiveUsers activeUsers);

        public CommandType getType() {
            return type;
        }
    }
}
