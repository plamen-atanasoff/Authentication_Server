package businesslogicnew.command.commands;

import businesslogicold.passwordencryptor.PasswordEncryptor;
import businesslogicnew.command.Command;
import businesslogicnew.command.CommandType;
import businesslogicnew.database.UserCredentials;
import businesslogicnew.database.UserDatabase;
import businesslogicnew.users.ActiveUsers;

import java.util.Map;

public class Register implements Command {
    private static final String USER_REGISTERED_MESSAGE = "User has been successfully registered";
    private final String username;

    private final String password;

    private final String firstName;

    private final String lastName;

    private final String email;

    private final UserDatabase users;

    private final ActiveUsers activeUsers;

    public Register(String username, String password, String firstName, String lastName, String email,
                    UserDatabase users, ActiveUsers activeUsers) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.users = users;
        this.activeUsers = activeUsers;
    }

    @Override
    public String execute() {
        // add user to the database
        UserCredentials user = new UserCredentials(username, password, firstName, lastName, email);

        return USER_REGISTERED_MESSAGE;
    }

    public static class RegisterCreator extends Creator.CommandCreator {
        private static final int ARGS_COUNT = 5; // username, password, first-name, last-name, email

        protected RegisterCreator() {
            super(CommandType.REGISTER);
        }

        @Override
        public Command create(Map<String, String> input, UserDatabase users, ActiveUsers activeUsers) {
            if (input.size() != ARGS_COUNT) {
                throw new RuntimeException(String.format(FORMAT_STRING, ARGS_COUNT));
            }

            if (!input.containsKey("username") || !input.containsKey("password") || !input.containsKey("first-name")
            || !input.containsKey("last-name") || !input.containsKey("email")) {
                throw new RuntimeException("Username or password is missing");
            }

            return new Register(input.get("username"), input.get("password"), input.get("first-name"),
                input.get("last-name"), input.get("email"), users, activeUsers);
        }
    }
}
