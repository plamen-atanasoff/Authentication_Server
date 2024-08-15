package businesslogicnew.command.commands;

import businesslogicnew.command.Command;
import businesslogicnew.command.CommandType;
import businesslogicnew.command.commands.creator.Creator;
import businesslogicnew.database.User;
import businesslogicnew.database.UserDatabase;
import businesslogicnew.logger.ServerLogger;
import businesslogicnew.users.ActiveUsers;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.Map;

public class Delete implements Command {

    private static final String INVALID_SESSION_MESSAGE = "Session is not valid";

    private static final String USER_DELETED_MESSAGE = "User has been successfully deleted";

    private static final String NON_EXISTENT_USER_MESSAGE = "User does not exist in database";

    private static final String NOT_AUTHORIZED_ERROR_MESSAGE = "User is not authorized to execute command";

    private final int sessionId;

    private final String username;

    private final UserDatabase users;

    private final ActiveUsers activeUsers;

    public Delete(int sessionId, String username, UserDatabase users, ActiveUsers activeUsers) {
        this.sessionId = sessionId;
        this.username = username;
        this.users = users;
        this.activeUsers = activeUsers;
    }

    @Override
    public String execute() {
        // validate session id
        if (!activeUsers.sessionExists(sessionId)) {
            return INVALID_SESSION_MESSAGE;
        }

        // validate session id corresponds to admin
        int userId = activeUsers.getUserId(sessionId);
        User user;
        try {
            user = users.getUser(userId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!user.isAdmin()) {
            throw new RuntimeException(NOT_AUTHORIZED_ERROR_MESSAGE);
        }

        // invalidate deleted user's session
        User userToBeDeleted;
        try {
            userToBeDeleted = users.getUser(username);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        activeUsers.removeSessionByUserId(userToBeDeleted.id());

        // delete user
        try {
            if (users.deleteUser(userToBeDeleted.id())) {
                return USER_DELETED_MESSAGE;
            } else {
                return NON_EXISTENT_USER_MESSAGE;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class DeleteCreator extends Creator.CommandCreator {

        private static final String MISSING_ARGUMENTS_MESSAGE = "Missing argument keys";

        private static final String SESSION_ID_STRING = "session-id";

        private static final String USERNAME_STRING = "username";

        private static final int ARGS_COUNT = 2; // session-id, username


        public DeleteCreator() {
            super(CommandType.DELETE);
        }

        @Override
        public Command create(Map<String, String> input, UserDatabase users, ActiveUsers activeUsers,
                              SelectionKey key, ServerLogger serverLogger) {
            if (input.size() != ARGS_COUNT) {
                throw new RuntimeException();
            }

            if (!input.containsKey(SESSION_ID_STRING) || !input.containsKey(USERNAME_STRING)) {
                throw new RuntimeException(MISSING_ARGUMENTS_MESSAGE);
            }

            return new Delete(Integer.parseInt(input.get(SESSION_ID_STRING)),
                input.get(USERNAME_STRING), users, activeUsers);
        }
    }
}
