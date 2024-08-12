package businesslogicnew.command;

import businesslogicnew.command.commands.Creator;
import businesslogicnew.database.UserDatabase;
import businesslogicnew.tokenizer.Tokenizer;
import businesslogicnew.users.ActiveUsers;

import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Map;

public class CommandFactory {

    private static final String COMMAND_DOES_NOT_EXIST_MESSAGE = "Command does not exist";

    private static final int LOGIN_PASSWORD_TOKENS_COUNT = 2;

    private static final int LOGIN_SESSION_ID_TOKENS_COUNT = 1;

    private static final String WRONG_FORMAT_MESSAGE = "Input is not in the right format";

    private static final String LOGIN_STRING = "login";

    private static final int SEPARATOR_SYMBOLS_COUNT = 2;

    private static final String DELIMITER = " ";

    private static final String FORMAT_STRING = "Creator with %s type does not exist";

    private static CommandFactory instance;

    private final Map<CommandType, Creator.CommandCreator> creators;

    public static CommandFactory getInstance() {
        if (instance == null) {
            instance = new CommandFactory();
        }

        return instance;
    }

    public Command createCommand(String input, UserDatabase users, ActiveUsers activeUsers, SelectionKey key) {
        int separatingIndex = input.indexOf(DELIMITER);

        if (separatingIndex == -1) {
            throw new IllegalArgumentException(WRONG_FORMAT_MESSAGE);
        }

        String command = input.substring(0, separatingIndex);
        Map<String, String> tokens = Tokenizer.tokenizeFromSocketChannel(
            input.substring(separatingIndex).trim().substring(SEPARATOR_SYMBOLS_COUNT));

        CommandType type = null;
        if (command.equals(LOGIN_STRING)) {
            if (tokens.size() == LOGIN_PASSWORD_TOKENS_COUNT) {
                type = CommandType.LOGIN_PASSWORD;
            } else if (tokens.size() == LOGIN_SESSION_ID_TOKENS_COUNT) {
                type = CommandType.LOGIN_SESSION_ID;
            }
        } else {
            type = CommandType.getAsType(command);
        }

        if (type == null) {
            throw new RuntimeException(COMMAND_DOES_NOT_EXIST_MESSAGE);
        }

        Creator.CommandCreator creator = creators.get(type);

        if (creator == null) {
            throw new RuntimeException(String.format(FORMAT_STRING, type));
        }

        return creator.create(tokens, users, activeUsers, key);
    }

    private CommandFactory() {
        creators = new HashMap<>();
        for (Creator creator : Creator.values()) {
            Creator.CommandCreator commandCreator = creator.getCreator();
            creators.put(commandCreator.getType(), commandCreator);
        }
    }
}
