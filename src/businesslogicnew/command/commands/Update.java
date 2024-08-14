package businesslogicnew.command.commands;

import businesslogicnew.command.Command;
import businesslogicnew.command.CommandType;
import businesslogicnew.command.commands.creator.Creator;
import businesslogicnew.database.User;
import businesslogicnew.database.UserCredentials;
import businesslogicnew.database.UserDatabase;
import businesslogicnew.users.ActiveUsers;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.Map;

public class Update implements Command {

    private static final String USERNAME_STRING = "username";

    private static final String FIRST_NAME_STRING = "first-name";

    private static final String LAST_NAME_STRING = "last-name";

    private static final String EMAIL_STRING = "email";

    private static final String INVALID_SESSION_MESSAGE = "Session is not valid";

    private static final String USER_UPDATED_MESSAGE = "User has been successfully updated";

    private static final String NON_EXISTENT_USER_MESSAGE = "User does not exist in database";

    private final int sessionId;

    private final Map<String, String> userArgs;

    private final UserDatabase users;

    private final ActiveUsers activeUsers;

    public Update(int sessionId, Map<String, String> userArgs, UserDatabase users, ActiveUsers activeUsers) {
        this.sessionId = sessionId;
        this.userArgs = userArgs;
        this.users = users;
        this.activeUsers = activeUsers;
    }

    @Override
    public String execute() {
        // check if user is active
        if (!activeUsers.sessionExists(sessionId)) {
            return INVALID_SESSION_MESSAGE;
        }

        // get user
        int userId = activeUsers.getUserId(sessionId);
        User user;
        try {
            user = users.getUser(userId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // check if user exists
        if (user == null) {
            return NON_EXISTENT_USER_MESSAGE;
        }

        // create updated user instance
        UserCredentials oldCredentials = user.credentials();
        String newUsername = userArgs.getOrDefault(USERNAME_STRING, oldCredentials.username());
        String newFirstName = userArgs.getOrDefault(FIRST_NAME_STRING, oldCredentials.firstName());
        String newLastName = userArgs.getOrDefault(LAST_NAME_STRING, oldCredentials.lastName());
        String newEmail = userArgs.getOrDefault(EMAIL_STRING, oldCredentials.email());

        UserCredentials newCredentials = new UserCredentials(newUsername, oldCredentials.passwordHash(),
            newFirstName, newLastName, newEmail);
        User newUser = new User(user.id(), user.isAdmin(), newCredentials);

        // update user
        try {
            users.updateUser(newUser);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return USER_UPDATED_MESSAGE;
    }

    public static class UpdateCreator extends Creator.CommandCreator {

        private static final String MISSING_ARGUMENTS_MESSAGE = "Missing argument keys";

        private static final String SESSION_ID_STRING = "session-id";

        private static final int ARGS_COUNT_MAX = 5;


        public UpdateCreator() {
            super(CommandType.UPDATE);
        }

        @Override
        public Command create(Map<String, String> input, UserDatabase users, ActiveUsers activeUsers, SelectionKey key) {
            if (input.isEmpty() || input.size() > ARGS_COUNT_MAX) {
                throw new RuntimeException();
            }

            if (!input.containsKey(SESSION_ID_STRING)) {
                throw new RuntimeException(MISSING_ARGUMENTS_MESSAGE);
            }

            return new Update(Integer.parseInt(input.get(SESSION_ID_STRING)), input, users, activeUsers);
        }
    }
}
