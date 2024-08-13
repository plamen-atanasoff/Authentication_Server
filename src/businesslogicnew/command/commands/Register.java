package businesslogicnew.command.commands;

import businesslogicnew.passwordencryptor.PasswordEncryptor;
import businesslogicnew.command.Command;
import businesslogicnew.command.CommandType;
import businesslogicnew.database.UserCredentials;
import businesslogicnew.database.UserDatabase;
import businesslogicnew.users.ActiveUsers;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

public class Register implements Command {
    private static final String USER_REGISTERED_MESSAGE = "User has been successfully registered";
    private static final String USER_ALREADY_EXISTS_MESSAGE = "User already exists";
    private final String username;

    private final String password;

    private final String firstName;

    private final String lastName;

    private final String email;

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
        String passwordHash;
        try {
            passwordHash = PasswordEncryptor.getInstance().generateHash(password);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

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

        private static final String MISSING_ARGUMENTS_MESSAGE = "Missing argument keys";

        private static final String USERNAME_STRING = "username";

        private static final String PASSWORD_STRING = "password";

        private static final String FIRST_NAME_STRING = "first-name";

        private static final String LAST_NAME_STRING = "last-name";

        private static final String EMAIL_STRING = "email";

        private static final int ARGS_COUNT = 5; // username, password, first-name, last-name, email

        protected RegisterCreator() {
            super(CommandType.REGISTER);
        }

        @Override
        public Command create(Map<String, String> input, UserDatabase users, ActiveUsers activeUsers, SelectionKey key) {
            if (input.size() != ARGS_COUNT) {
                throw new RuntimeException(String.format(FORMAT_STRING, ARGS_COUNT));
            }

            if (!input.containsKey(USERNAME_STRING) || !input.containsKey(PASSWORD_STRING) ||
                !input.containsKey(FIRST_NAME_STRING) || !input.containsKey(LAST_NAME_STRING) ||
                !input.containsKey(EMAIL_STRING)) {
                throw new RuntimeException(MISSING_ARGUMENTS_MESSAGE);
            }

            return new Register(input.get(USERNAME_STRING), input.get(PASSWORD_STRING), input.get(FIRST_NAME_STRING),
                input.get(LAST_NAME_STRING), input.get(EMAIL_STRING), users);
        }
    }
}
