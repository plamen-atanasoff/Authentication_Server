package businesslogicnew.command.commands;

import businesslogicold.passwordencryptor.PasswordEncryptor;
import businesslogicnew.command.Command;
import businesslogicnew.command.CommandType;
import businesslogicnew.database.UserCredentials;
import businesslogicnew.database.UserDatabase;
import businesslogicnew.users.ActiveUsers;

import java.io.IOException;
import java.util.Map;

public class Register implements Command {
    private static final String USER_REGISTERED_MESSAGE = "User has been successfully registered";
    private static final String USER_ALREADY_EXISTS_MESSAGE = "User already exists";
    private final String username;

    private final String password;

    private final String firstName;

    private final String lastName;

    private final String email;

    @SuppressWarnings("FieldCanBeLocal")
    private final UserDatabase users;

    public Register(String username, String password, String firstName, String lastName, String email,
                    UserDatabase users) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.users = users;
    }

    @Override
    public String execute() {
        // check if user already exists
        try {
            if (users.getUser(username) != null) {
                return USER_ALREADY_EXISTS_MESSAGE;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // hash the password
        String passwordHash = PasswordEncryptor.encryptPassword(password);

        // add user to the database
        UserCredentials user = new UserCredentials(username, passwordHash, firstName, lastName, email);
        try {
            users.addUser(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
                input.get("last-name"), input.get("email"), users);
        }
    }
}
