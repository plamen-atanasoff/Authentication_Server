package businesslogicnew.command.commands;

import businesslogicnew.command.Command;
import businesslogicnew.command.CommandType;
import businesslogic.database.UserDatabase;
import businesslogic.exception.InvalidCredentialsException;
import businesslogicnew.users.ActiveUsers;

import java.util.Map;

public class LoginWithPassword implements Command {
    private static final String INVALID_LOGIN_CREDENTIALS_MESSAGE = "invalid login credentials";
    private static final String FORMAT_STRING = "Input tokens must be %d";
    private static final int ARGS_COUNT = 2;
    private final String username;
    private final String password;
    private final UserDatabase users;
    private final ActiveUsers activeUsers;

    public LoginWithPassword(String username, String password, UserDatabase users, ActiveUsers activeUsers) {
        this.username = username;
        this.password = password;
        this.users = users;
        this.activeUsers = activeUsers;
    }

    @Override
    public String execute() {
        return null;
    }

    private void validatePassword(String expectedPassword, String actualPassword) {
        if (!actualPassword.equals(expectedPassword)) {
            throw new InvalidCredentialsException(INVALID_LOGIN_CREDENTIALS_MESSAGE);
        }
    }

    public static class LoginWithPasswordCreator extends Creator.CommandCreator {
        public LoginWithPasswordCreator() {
            super(CommandType.LOGIN);
        }
        @Override
        public Command create(Map<String, String> input, UserDatabase users, ActiveUsers activeUsers) {
            if (input.size() != ARGS_COUNT) {
                throw new IllegalArgumentException(String.format(FORMAT_STRING, ARGS_COUNT));
            }

            return new LoginWithPassword(input.get("username"), input.get("password"), users, activeUsers);
        }
    }
}
