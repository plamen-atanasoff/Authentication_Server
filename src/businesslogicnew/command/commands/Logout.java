package businesslogicnew.command.commands;

import businesslogicnew.command.Command;
import businesslogicnew.command.CommandType;
import businesslogicnew.command.commands.creator.Creator;
import businesslogicnew.database.UserDatabase;
import businesslogicnew.logger.ServerLogger;
import businesslogicnew.users.ActiveUsers;

import java.nio.channels.SelectionKey;
import java.util.Map;

public class Logout implements Command {

    private static final String INVALID_SESSION_MESSAGE = "Session is not valid";

    private static final String USER_LOGGED_OUT_MESSAGE = "User has been successfully logged out";

    private final int sessionId;

    private final ActiveUsers activeUsers;

    public Logout(int sessionId, ActiveUsers activeUsers) {
        this.sessionId = sessionId;
        this.activeUsers = activeUsers;
    }

    @Override
    public String execute() {
        // invalidate session id
        Integer result = activeUsers.removeSession(sessionId);
        if (result == null) {
            return INVALID_SESSION_MESSAGE;
        }

        return USER_LOGGED_OUT_MESSAGE;
    }

    public static class LogoutCreator extends Creator.CommandCreator {

        private static final String MISSING_ARGUMENTS_MESSAGE = "Missing argument keys";

        private static final String SESSION_ID_STRING = "session-id";

        private static final int ARGS_COUNT = 1; // session-id


        public LogoutCreator() {
            super(CommandType.LOGOUT);
        }

        @Override
        public Command create(Map<String, String> input, UserDatabase users, ActiveUsers activeUsers,
                              SelectionKey key, ServerLogger serverLogger) {
            if (input.size() != ARGS_COUNT) {
                throw new RuntimeException(String.format(FORMAT_STRING, ARGS_COUNT));
            }

            if (!input.containsKey(SESSION_ID_STRING)) {
                throw new RuntimeException(MISSING_ARGUMENTS_MESSAGE);
            }

            return new Logout(Integer.parseInt(input.get(SESSION_ID_STRING)), activeUsers);
        }
    }
}
