package businesslogicnew.command.commands;

import businesslogicnew.command.Command;
import businesslogicnew.command.CommandType;
import businesslogicnew.database.User;
import businesslogicnew.database.UserCredentials;
import businesslogicnew.database.UserDatabase;
import businesslogicnew.passwordencryptor.PasswordEncryptor;
import businesslogicnew.users.ActiveUsers;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

public class ChangePassword implements Command {

    private static final String PASSWORD_CHANGED_MESSAGE = "Password has been successfully changed";

    private static final String INVALID_SESSION_MESSAGE = "Session is not valid";

    private static final String NON_EXISTENT_USER_MESSAGE = "User does not exist in database";

    private static final String INVALID_PASSWORD_MESSAGE = "Password is not valid";

    private final int sessionId;

    private final String username;

    private final String oldPassword;

    private final String newPassword;

    private final UserDatabase users;

    private final ActiveUsers activeUsers;

    public ChangePassword(int sessionId, String username, String oldPassword, String newPassword, UserDatabase users,
                          ActiveUsers activeUsers) {
        this.sessionId = sessionId;
        this.username = username;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.users = users;
        this.activeUsers = activeUsers;
    }

    @Override
    public String execute() {
        // validate session id
        if (!activeUsers.sessionExists(sessionId)) {
            return INVALID_SESSION_MESSAGE;
        }

        // validate user exists and is corresponding to the session id
        int userId = activeUsers.getUserId(sessionId);
        User user;
        try {
            user = users.getUser(username);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (user == null) {
            return NON_EXISTENT_USER_MESSAGE;
        }

        if (userId != user.id()) {
            return INVALID_SESSION_MESSAGE + " or " + NON_EXISTENT_USER_MESSAGE;
        }

        // validate old password
        String oldPasswordHash;
        try {
            oldPasswordHash = PasswordEncryptor.generateHash(oldPassword);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        if (!oldPasswordHash.equals(user.credentials().passwordHash())) {
            return INVALID_PASSWORD_MESSAGE;
        }

        // change old password with new one
        String newPasswordHash;
        try {
            newPasswordHash = PasswordEncryptor.generateHash(newPassword);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        UserCredentials oldCredentials = user.credentials();
        UserCredentials updatedCredentials = new UserCredentials(oldCredentials.username(), newPasswordHash,
            oldCredentials.firstName(), oldCredentials.lastName(), oldCredentials.email());
        User updatedUser = new User(user.id(), user.isAdmin(), updatedCredentials);

        // update database
        try {
            users.updateUser(updatedUser);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return PASSWORD_CHANGED_MESSAGE;
    }

    public static class ChangePasswordCreator extends Creator.CommandCreator {

        private static final String MISSING_ARGUMENTS_MESSAGE = "Missing argument keys";

        private static final String SESSION_ID_STRING = "session-id";

        private static final String USERNAME_STRING = "username";

        private static final String OLD_PASSWORD_STRING = "old-password";

        private static final String NEW_PASSWORD_STRING = "new-password";

        private static final int ARGS_COUNT = 4;

        protected ChangePasswordCreator() {
            super(CommandType.CHANGE_PASSWORD);
        }

        @Override
        public Command create(Map<String, String> input, UserDatabase users, ActiveUsers activeUsers, SelectionKey key) {
            if (input.size() != ARGS_COUNT) {
                throw new RuntimeException(String.format(FORMAT_STRING, ARGS_COUNT));
            }

            if (!input.containsKey(SESSION_ID_STRING) || !input.containsKey(USERNAME_STRING) ||
                !input.containsKey(OLD_PASSWORD_STRING) || !input.containsKey(NEW_PASSWORD_STRING)) {
                throw new RuntimeException(MISSING_ARGUMENTS_MESSAGE);
            }

            return new ChangePassword(Integer.parseInt(input.get(SESSION_ID_STRING)), input.get(USERNAME_STRING),
                input.get(OLD_PASSWORD_STRING), input.get(NEW_PASSWORD_STRING), users, activeUsers);
        }
    }
}
