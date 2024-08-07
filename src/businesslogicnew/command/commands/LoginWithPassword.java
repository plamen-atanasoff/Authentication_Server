package businesslogicnew.command.commands;

import businesslogicnew.passwordencryptor.PasswordEncryptor;
import businesslogicnew.command.Command;
import businesslogicnew.command.CommandType;
import businesslogicnew.database.User;
import businesslogicnew.database.UserDatabase;
import businesslogicnew.users.ActiveUsers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

public class LoginWithPassword implements Command {
    private static final String INVALID_LOGIN_CREDENTIALS_MESSAGE = "Invalid login credentials";

    private static final String USER_DOES_NOT_EXIST_MESSAGE = "User does not exist";

    private final String username;

    private final String password;

    private final UserDatabase users;

    private final ActiveUsers activeUsers;

    public LoginWithPassword(String username, String password, UserDatabase users, ActiveUsers activeUsers) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username or password is null");
        }

        this.username = username;
        this.password = password;
        this.users = users;
        this.activeUsers = activeUsers;
    }

    @Override
    public String execute() {
        // check if user exists
        User user;
        try {
            user = users.getUser(username);
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }

        if (user == null) {
            return USER_DOES_NOT_EXIST_MESSAGE;
        }

        // check if passwordHash is valid
        String passwordHashRequest;
        try {
            passwordHashRequest = PasswordEncryptor.generateHash(password);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        if (!passwordHashRequest.equals(user.credentials().passwordHash())) {
            return INVALID_LOGIN_CREDENTIALS_MESSAGE;
        }

        // get valid session id
        int sessionId = activeUsers.addSession(user.id());

        return String.valueOf(sessionId);
    }

    public static class LoginWithPasswordCreator extends Creator.CommandCreator {
        private static final int ARGS_COUNT = 2; // username, password

        protected LoginWithPasswordCreator() {
            super(CommandType.LOGIN_PASSWORD);
        }
        @Override
        public Command create(Map<String, String> input, UserDatabase users, ActiveUsers activeUsers) {
            if (input.size() != ARGS_COUNT) {
                throw new RuntimeException(String.format(FORMAT_STRING, ARGS_COUNT));
            }

            if (!input.containsKey("username") || !input.containsKey("password")) {
                throw new RuntimeException("Username or password is missing");
            }

            return new LoginWithPassword(input.get("username"), input.get("password"), users, activeUsers);
        }
    }
}
