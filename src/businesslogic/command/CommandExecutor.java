package businesslogic.command;

import businesslogic.database.UserCredentials;
import businesslogic.database.UserDatabase;
import businesslogic.exception.InvalidCredentialsException;
import businesslogic.exception.InvalidSessionIdException;
import businesslogic.exception.UserAlreadyRegisteredException;
import businesslogic.logger.Logger;
import businesslogic.passwordencryptor.PasswordEncryptor;
import businesslogic.users.ActiveUsers;
import businesslogic.users.User;
import businesslogic.database.UserCredentials;
import businesslogic.exception.InvalidCredentialsException;
import businesslogic.exception.InvalidSessionIdException;
import businesslogic.exception.UserAlreadyRegisteredException;
import businesslogic.logger.Logger;
import businesslogic.passwordencryptor.PasswordEncryptor;
import businesslogic.users.User;

import java.time.Instant;
import java.util.Map;

public class CommandExecutor {
    private static final int REGISTER_ARGS_COUNT = 5;
    private static final int LOGOUT_ARGS_COUNT = 1;
    private static final int LOGIN_WITH_PASSWORD_ARGS_COUNT = 2;
    private static final int LOGIN_WITH_SESSION_ID_ARGS_COUNT = 1;
    private static final int RESET_PASSWORD_ARGS_COUNT = 4;
    private static final int ADMIN_COMMAND_ARGS_COUNT = 2;
    private static final String REGISTER = "register";
    private static final String LOGOUT = "logout";
    private static final String LOGIN = "login";
    private static final String UPDATE_USER = "update-user";
    private static final String RESET_PASSWORD = "reset-password";
    private static final String MAKE_ADMIN = "add-admin-user";
    private static final String REMOVE_ADMIN = "remove-admin-user";
    private static final String DELETE_USER = "delete-user";
    private static final String SESSION_ID = "session-id";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String FIRST_NAME = "first-name";
    private static final String LAST_NAME = "last-name";
    private static final String EMAIL = "email";
    private static final String OLD_PASSWORD = "old-password";
    private static final String NEW_PASSWORD = "new-password";
    private static final String NEW_USERNAME = "new-username";
    private static final String NEW_FIRST_NAME = "new-first-name";
    private static final String NEW_LAST_NAME = "new-last-name";
    private static final String NEW_EMAIL = "new-email";

    private UserDatabase users;
    private ActiveUsers activeUsers;

    public CommandExecutor(UserDatabase users, ActiveUsers activeUsers) {
        this.users = users;
        this.activeUsers = activeUsers;
    }

    public String execute(Command cmd) {
        return switch (cmd.command()) {
            case REGISTER -> register(cmd.arguments());
            case LOGIN -> login(cmd.arguments());
            case LOGOUT -> logout(cmd.arguments());
            case UPDATE_USER -> updateUser(cmd.arguments());
            case RESET_PASSWORD -> resetPassword(cmd.arguments());
            case MAKE_ADMIN -> makeUserAdmin(cmd.arguments());
            case REMOVE_ADMIN -> removeUserAdmin(cmd.arguments());
            case DELETE_USER -> deleteUser(cmd.arguments());
            default -> null;
        };
    }

    private String register(Map<String, String> args) {
        validateArgsCount(REGISTER_ARGS_COUNT, args.size());

        if (users.exists(args.get(USERNAME))) {
            throw new UserAlreadyRegisteredException("user has been already registered");
        }

        String encryptedPassword = PasswordEncryptor.encryptPassword(args.get(PASSWORD));

        UserCredentials userCredentials = new UserCredentials(args.get(USERNAME), encryptedPassword,
            args.get(FIRST_NAME), args.get(LAST_NAME), args.get(EMAIL), false);
        users.add(userCredentials);
        User user = new User(args.get(USERNAME), false);
        activeUsers.add(user);

        return String.valueOf(user.getUserSessionId());
    }

    private String logout(Map<String, String> args) {
        validateArgsCount(LOGOUT_ARGS_COUNT, args.size());

        int sessionId = Integer.parseInt(args.get(SESSION_ID));
        validateSessionId(sessionId);

        activeUsers.remove(sessionId);

        return "";
    }

    private void validateArgsCount(int expectedCount, int actualCount) {
        if (actualCount != expectedCount) {
            throw new IllegalArgumentException("invalid count of args");
        }
    }

    private String login(Map<String, String> args) {
        if (args.size() == LOGIN_WITH_PASSWORD_ARGS_COUNT) {
            return loginWithPassword(args);
        } else if (args.size() == LOGIN_WITH_SESSION_ID_ARGS_COUNT) {
            return loginWithSessionId(args);
        } else {
            throw new IllegalArgumentException("invalid count of args");
        }
    }

    private String loginWithSessionId(Map<String, String> args) {
        int sessionId = Integer.parseInt(args.get(SESSION_ID));
        validateSessionId(sessionId);

        return "";
    }

    private void validateSessionId(int sessionId) {
        if (!activeUsers.contains(sessionId)) {
            throw new InvalidSessionIdException("invalid sessionId");
        }
    }

    private String loginWithPassword(Map<String, String> args) {
        String username = args.get(USERNAME);

        UserCredentials userCredentials = users.getUserCredentialsByUsername(username);

        String encryptedPassword = PasswordEncryptor.encryptPassword(args.get(PASSWORD));
        validatePassword(userCredentials.password(), encryptedPassword);

        User user = new User(username, userCredentials.isAdmin());
        if (activeUsers.contains(username)) {
            activeUsers.remove(username);
        }
        activeUsers.add(user);

        return String.valueOf(user.getUserSessionId());
    }

    private void validatePassword(String expectedPassword, String actualPassword) {
        if (!actualPassword.equals(expectedPassword)) {
            throw new InvalidCredentialsException("invalid login credentials");
        }
    }

    private String updateUser(Map<String, String> args) {
        int sessionId = Integer.parseInt(args.get(SESSION_ID));
        validateSessionId(sessionId);

        User user = activeUsers.getUser(sessionId);

        UserCredentials oldUserCredentials = users.getUserCredentialsByUsername(user.getUsername());
        users.remove(oldUserCredentials.username());

        String username = args.containsKey(NEW_USERNAME) ? args.get(NEW_USERNAME) : oldUserCredentials.username();
        String firstName = args.containsKey(NEW_FIRST_NAME) ? args.get(NEW_FIRST_NAME) : oldUserCredentials.firstName();
        String lastName = args.containsKey(NEW_LAST_NAME) ? args.get(NEW_LAST_NAME) : oldUserCredentials.lastName();
        String email = args.containsKey(NEW_EMAIL) ? args.get(NEW_EMAIL) : oldUserCredentials.email();

        UserCredentials newUserCredentials = new UserCredentials(username, oldUserCredentials.password(),
            firstName, lastName, email, oldUserCredentials.isAdmin());
        users.add(newUserCredentials);

        if (args.containsKey(NEW_USERNAME)) {
            user.changeUsername(newUserCredentials.username());
        }

        return "";
    }

    private String resetPassword(Map<String, String> args) {
        validateArgsCount(RESET_PASSWORD_ARGS_COUNT, args.size());

        int sessionId = Integer.parseInt(args.get(SESSION_ID));
        validateSessionId(sessionId);

        String oldPassword = PasswordEncryptor.encryptPassword(args.get(OLD_PASSWORD));
        String newPassword = PasswordEncryptor.encryptPassword(args.get(NEW_PASSWORD));

        User user = activeUsers.getUser(sessionId);

        UserCredentials oldUserCredentials = users.getUserCredentialsByUsername(user.getUsername());

        validatePassword(oldUserCredentials.password(), oldPassword);

        users.remove(oldUserCredentials.username());
        UserCredentials newUserCredentials = new UserCredentials(oldUserCredentials.username(),
            String.valueOf(newPassword), oldUserCredentials.firstName(), oldUserCredentials.lastName(),
            oldUserCredentials.email(), oldUserCredentials.isAdmin());
        users.add(newUserCredentials);

        return "";
    }

    private String makeUserAdmin(Map<String, String> args) {
        validateArgsCount(ADMIN_COMMAND_ARGS_COUNT, args.size());

        int sessionId = Integer.parseInt(args.get(SESSION_ID));
        validateSessionId(sessionId);

        String username = args.get(USERNAME);

        User user = activeUsers.getUser(sessionId);
        validateUserIsAdmin(user);

        UserCredentials oldUserCredentials = users.getUserCredentialsByUsername(username);
        users.remove(oldUserCredentials.username());
        UserCredentials newUserCredentials = new UserCredentials(oldUserCredentials.username(),
            oldUserCredentials.password(), oldUserCredentials.firstName(), oldUserCredentials.lastName(),
            oldUserCredentials.email(), true);
        users.add(newUserCredentials);
        users.addAdmin();

        if (activeUsers.contains(username)) {
            User newAdminUser = activeUsers.getUser(username);
            newAdminUser.makeAdmin();
        }

        String messageLog = Instant.now() + ",makeUserAdmin" + ",configuration change," + username
            + " - added admin permissions" + System.lineSeparator();
        Logger.createLog(messageLog);

        return "";
    }

    private void validateUserIsAdmin(User user) {
        if (!user.isAdmin()) {
            throw new InvalidCredentialsException("user is not admin");
        }
    }

    private String removeUserAdmin(Map<String, String> args) {
        validateArgsCount(ADMIN_COMMAND_ARGS_COUNT, args.size());

        int sessionId = Integer.parseInt(args.get(SESSION_ID));
        validateSessionId(sessionId);

        String username = args.get(USERNAME);

        User user = activeUsers.getUser(sessionId);
        validateUserIsAdmin(user);

        if (users.getAdminsCount() == 1) {
            throw new RuntimeException("1 admin left in server");
        }

        UserCredentials oldUserCredentials = users.getUserCredentialsByUsername(username);
        users.remove(oldUserCredentials.username());
        UserCredentials newUserCredentials = new UserCredentials(oldUserCredentials.username(),
            oldUserCredentials.password(), oldUserCredentials.firstName(), oldUserCredentials.lastName(),
            oldUserCredentials.email(), false);
        users.add(newUserCredentials);
        users.removeAdmin();

        if (activeUsers.contains(username)) {
            User newAdminUser = activeUsers.getUser(username);
            newAdminUser.removeAdmin();
        }

        String messageLog = Instant.now() + ",removeUserAdmin" + ",configuration change," + username
            + " - removed admin permissions" + System.lineSeparator();
        Logger.createLog(messageLog);

        return "";
    }

    private String deleteUser(Map<String, String> args) {
        validateArgsCount(ADMIN_COMMAND_ARGS_COUNT, args.size());

        int sessionId = Integer.parseInt(args.get(SESSION_ID));
        validateSessionId(sessionId);

        String username = args.get(USERNAME);

        User user = activeUsers.getUser(sessionId);
        validateUserIsAdmin(user);

        UserCredentials userCredentials = users.getUserCredentialsByUsername(username);
        users.remove(userCredentials.username());

        if (activeUsers.contains(username)) {
            User userToDelete = activeUsers.getUser(username);
            activeUsers.remove(userToDelete.getUserSessionId());
        }

        return "";
    }

//    public static void main(String[] args) {
//        final long invalidationInterval = 5 * 60 * 1000;
//        final long sessionTimeout = 5 * 60 * 1000;
//        Path filePath = Path.of("F:\\Java\\Projects\\Authentication-Server-FMI-MJT\\userDatabase.txt");
//
//        UserDatabaseFile userDatabaseFile = new UserDatabaseFile(filePath);
//        UserDatabase userDatabase = new UserDatabase(userDatabaseFile);
//        ActiveUsers activeUsers = new ActiveUsers(invalidationInterval, sessionTimeout);
//        CommandExecutor commandExecutor = new CommandExecutor(userDatabase, activeUsers);
//
//        try (var scanner = new Scanner(System.in)) {
//            while (true) {
//                System.out.println("Users in DB: " + userDatabase.getUsers().size());
//                System.out.println("Users in Server: " + activeUsers.getActiveUsers().size());
//
//                System.out.print("Enter message: ");
//                String line = scanner.nextLine();
//
//                if ("quit".equals(line)) {
//                    break;
//                }
//
//                String reply;
//                try {
//                    Command command = Command.of(line);
//                    reply = commandExecutor.execute(command);
//                } catch (Exception e) {
//                    System.out.println(e.toString());
//                    continue;
//                }
//                System.out.println("The server replied <" + reply + ">");
//            }
//        }
//    }
}

