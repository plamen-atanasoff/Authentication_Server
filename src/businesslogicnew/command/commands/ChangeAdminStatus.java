package businesslogicnew.command.commands;

import businesslogicnew.database.User;
import businesslogicnew.database.UserDatabase;
import businesslogicnew.logger.ServerLogger;
import businesslogicnew.users.ActiveUsers;

import java.io.IOException;
import java.util.Map;

public class ChangeAdminStatus {

    private static final String ADDED_ADMIN_MESSAGE = "The user has successfully been granted admin status";

    private static final String ADDED_ADMIN_LOG_MESSAGE_FORMAT = "%s given admin status";

    private static final String REMOVED_ADMIN_MESSAGE = "The user has successfully been denied admin status";

    private static final String REMOVED_ADMIN_LOG_MESSAGE_FORMAT = "%s denied admin status";

    private static final String NOT_AUTHORIZED_ERROR_MESSAGE = "User is not authorized to execute command";

    private static final String INVALID_SESSION_MESSAGE = "Session is not valid";

    private final int sessionId;

    private final String username;

    private final UserDatabase users;

    private final ActiveUsers activeUsers;

    private final ServerLogger logger;

    public ChangeAdminStatus(int sessionId, String username, UserDatabase users, ActiveUsers activeUsers,
                             ServerLogger logger) {
        this.sessionId = sessionId;
        this.username = username;
        this.users = users;
        this.activeUsers = activeUsers;
        this.logger = logger;
    }

    public String changeAdminStatus(boolean newAdminStatus) {
        // validate session id
        if (!activeUsers.sessionExists(sessionId)) {
            return INVALID_SESSION_MESSAGE;
        }

        // validate user associated with given session id is admin
        int userId = activeUsers.getUserId(sessionId);
        User adminUser;
        try {
            adminUser = users.getUser(userId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!adminUser.isAdmin()) {
            throw new RuntimeException(NOT_AUTHORIZED_ERROR_MESSAGE);
        }

        // validate username is valid
        User user;
        try {
            user = users.getUser(username);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // check if user already has admin status
        if (user.isAdmin() && newAdminStatus) {
            return ADDED_ADMIN_MESSAGE;
        } else if (!user.isAdmin() && !newAdminStatus) {
            return REMOVED_ADMIN_MESSAGE;
        }

        // create new admin user
        User updatedUser = new User(user.id(), newAdminStatus, user.credentials());

        // save newly created admin user
        try {
            users.updateUser(updatedUser);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // log change of admin status
        try {
            logger.log(String.format(
                newAdminStatus ? ADDED_ADMIN_LOG_MESSAGE_FORMAT : REMOVED_ADMIN_LOG_MESSAGE_FORMAT, username));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return newAdminStatus ? ADDED_ADMIN_MESSAGE : REMOVED_ADMIN_MESSAGE;
    }

    public static class ChangeAdminStatusValidator {

        public static final String MISSING_ARGUMENTS_MESSAGE = "Missing argument keys";

        public static final String SESSION_ID_STRING = "session-id";

        public static final String USERNAME_STRING = "username";

        public static final int ARGS_COUNT_MAX = 2; // session-id, username

        public static void validate(Map<String, String> input) {
            if (input.size() != ARGS_COUNT_MAX) {
                throw new RuntimeException();
            }

            if (!input.containsKey(SESSION_ID_STRING) || !input.containsKey(USERNAME_STRING)) {
                throw new RuntimeException(MISSING_ARGUMENTS_MESSAGE);
            }
        }

    }
}
