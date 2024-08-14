package businesslogicnew.command.commands;

import businesslogicnew.command.Command;
import businesslogicnew.command.CommandType;
import businesslogicnew.command.commands.creator.Creator;
import businesslogicnew.database.UserDatabase;
import businesslogicnew.logger.ServerLogger;
import businesslogicnew.users.ActiveUsers;

import java.nio.channels.SelectionKey;
import java.util.Map;

public class LoginWithSessionId implements Command {

    private static final String INVALID_SESSION_ID_MESSAGE = "Invalid sessionId";

    private static final String SUCCESSFUL_LOGIN_MESSAGE = "Logged in successfully";

    private final int sessionId;


    private final ActiveUsers activeUsers;

    public LoginWithSessionId(int sessionId, ActiveUsers activeUsers) {
        this.sessionId = sessionId;
        this.activeUsers = activeUsers;
    }

    @Override
    public String execute() {
        // check if sessionId is valid
        if (!activeUsers.sessionExists(sessionId)) {
            return INVALID_SESSION_ID_MESSAGE;
        }

        return SUCCESSFUL_LOGIN_MESSAGE;
    }

    public static class LoginWithSessionIdCreator extends Creator.CommandCreator {

        private static final String ILLEGAL_ARGUMENTS_MESSAGE = "SessionId is missing";

        private static final String SESSION_ID_STRING = "session-id";

        private static final int ARGS_COUNT = 1; // session-id

        public LoginWithSessionIdCreator() {
            super(CommandType.LOGIN_SESSION_ID);
        }
        @Override
        public Command create(Map<String, String> input, UserDatabase users, ActiveUsers activeUsers,
                              SelectionKey key, ServerLogger serverLogger) {
            if (input.size() != ARGS_COUNT) {
                throw new RuntimeException(String.format(FORMAT_STRING, ARGS_COUNT));
            }

            if (!input.containsKey(SESSION_ID_STRING)) {
                throw new RuntimeException(ILLEGAL_ARGUMENTS_MESSAGE);
            }

            return new LoginWithSessionId(Integer.parseInt(input.get(SESSION_ID_STRING)), activeUsers);
        }
    }
}
