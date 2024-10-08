package businesslogicnew.command.commands;

import businesslogicnew.command.commands.creator.Creator;
import businesslogicnew.logger.ServerLogger;
import businesslogicnew.passwordencryptor.PasswordEncryptor;
import businesslogicnew.command.Command;
import businesslogicnew.command.CommandType;
import businesslogicnew.database.User;
import businesslogicnew.database.UserDatabase;
import businesslogicnew.users.ActiveUsers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

public class LoginWithPassword implements Command {
    private static final String ILLEGAL_ARGUMENTS_MESSAGE = "Username or password is null";

    private static final int MAX_FAILED_LOGINS_COUNT = 3;

    private static final String INVALID_LOGIN_CREDENTIALS_MESSAGE = "Invalid login credentials";

    private static final String USER_DOES_NOT_EXIST_MESSAGE = "User does not exist";

    private static final String CLIENT_IS_LOCKED_MESSAGE = "Client is locked";

    private static final String MULTIPLE_FAILED_LOGIN_ATTEMPTS_MESSAGE =
        "%s is locked because of multiple failed login attempts";

    private final String username;

    private final String password;

    private final UserDatabase users;

    private final ActiveUsers activeUsers;

    private final SelectionKey key;

    private final ServerLogger logger;

    public LoginWithPassword(String username, String password, UserDatabase users, ActiveUsers activeUsers,
                             SelectionKey key, ServerLogger logger) {
        if (username == null || password == null) {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENTS_MESSAGE);
        }

        this.username = username;
        this.password = password;
        this.users = users;
        this.activeUsers = activeUsers;
        this.key = key;
        this.logger = logger;
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

        // get socket channel
        SocketAddress socketAddress;
        try {
            socketAddress = getSocketAddress(key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // check if user is currently locked
        if (activeUsers.isLocked(socketAddress)) {
            return CLIENT_IS_LOCKED_MESSAGE;
        }

        // validate password
        if(!passwordIsValid(user, socketAddress)) {
            return INVALID_LOGIN_CREDENTIALS_MESSAGE;
        }

        // get valid session id
        int sessionId = activeUsers.addSession(user.id());

        return String.valueOf(sessionId);
    }

    private boolean passwordIsValid(User user, SocketAddress socketAddress) {
        // get password hash
        String passwordHashRequest;
        try {
            passwordHashRequest = PasswordEncryptor.getInstance().generateHash(password);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        // check if password hash is valid
        if (!passwordHashRequest.equals(user.credentials().passwordHash())) {
            // increment failed login attempts
            Object attachment = key.attachment();
            if (attachment == null) {
                key.attach(1);
                return false;
            }

            int failedLoginsCount = (int) attachment;
            assert failedLoginsCount <= MAX_FAILED_LOGINS_COUNT - 1;

            if (failedLoginsCount == MAX_FAILED_LOGINS_COUNT - 1) {
                // log locking of client
                try {
                    logger.log(String.format(MULTIPLE_FAILED_LOGIN_ATTEMPTS_MESSAGE, socketAddress));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                activeUsers.lockClient(socketAddress);

                key.attach(0);
            } else {
                key.attach(failedLoginsCount + 1);
            }

            return false;
        }

        return true;
    }

    private static SocketAddress getSocketAddress(SelectionKey key) throws IOException {
        if (key.isValid() && key.channel() instanceof SocketChannel channel) {
            return channel.getRemoteAddress();
        } else {
            return null;
        }
    }

    public static class LoginWithPasswordCreator extends Creator.CommandCreator {

        private static final String ILLEGAL_ARGUMENTS_MESSAGE = "Username or password is missing";

        private static final String USERNAME_STRING = "username";

        private static final String PASSWORD_STRING = "password";

        private static final int ARGS_COUNT = 2; // username, password

        public LoginWithPasswordCreator() {
            super(CommandType.LOGIN_PASSWORD);
        }
        @Override
        public Command create(Map<String, String> input, UserDatabase users, ActiveUsers activeUsers,
                              SelectionKey key, ServerLogger serverLogger) {
            if (input.size() != ARGS_COUNT) {
                throw new RuntimeException(String.format(FORMAT_STRING, ARGS_COUNT));
            }

            if (!input.containsKey(USERNAME_STRING) || !input.containsKey(PASSWORD_STRING)) {
                throw new RuntimeException(ILLEGAL_ARGUMENTS_MESSAGE);
            }

            return new LoginWithPassword(input.get(USERNAME_STRING), input.get(PASSWORD_STRING), users, activeUsers,
                key, serverLogger);
        }
    }
}
