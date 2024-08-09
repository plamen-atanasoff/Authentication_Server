package businesslogicnew.command.commands;

import businesslogicnew.command.Command;
import businesslogicnew.command.CommandType;
import businesslogicnew.database.UserDatabase;
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
        private static final int ARGS_COUNT = 1; // session-id

        protected LoginWithSessionIdCreator() {
            super(CommandType.LOGIN_SESSION_ID);
        }
        @Override
        public Command create(Map<String, String> input, UserDatabase users, ActiveUsers activeUsers, SelectionKey key) {
            if (input.size() != ARGS_COUNT) {
                throw new RuntimeException(String.format(FORMAT_STRING, ARGS_COUNT));
            }

            if (!input.containsKey("session-id")) {
                throw new RuntimeException("SessionId is missing");
            }

            return new LoginWithSessionId(Integer.parseInt(input.get("session-id")), activeUsers);
        }
    }
}
