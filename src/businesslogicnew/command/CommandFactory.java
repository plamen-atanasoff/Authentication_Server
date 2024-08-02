package businesslogicnew.command;

import businesslogicnew.command.commands.Creator;
import businesslogicnew.database.UserDatabase;
import businesslogicnew.tokenizer.Tokenizer;
import businesslogicnew.users.ActiveUsers;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {
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

    public Command createCommand(String input, UserDatabase users, ActiveUsers activeUsers) {
        int separatingIndex = input.indexOf(DELIMITER);

        if (separatingIndex == -1) {
            throw new IllegalArgumentException("Input is not in the right format");
        }

        CommandType type = CommandType.getAsType(input.substring(0, separatingIndex));

        if (type == null) {
            throw new RuntimeException("Command does not exist");
        }

        Creator.CommandCreator creator = creators.get(type);

        if (creator == null) {
            throw new RuntimeException(String.format(FORMAT_STRING, type));
        }

        return creator.create(
                Tokenizer.tokenizeFromSocketChannel(
                    input.substring(separatingIndex).trim().substring(SEPARATOR_SYMBOLS_COUNT)),
                users, activeUsers);
    }

    private CommandFactory() {
        creators = new HashMap<>();
        for (Creator creator : Creator.values()) {
            Creator.CommandCreator commandCreator = creator.getCreator();
            creators.put(commandCreator.getType(), commandCreator);
        }
    }
}
