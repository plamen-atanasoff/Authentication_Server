package businesslogicnew.command;

import businesslogicnew.command.commands.Creator;
import businesslogic.database.UserDatabase;
import businesslogicnew.tokenizer.Tokenizer;
import businesslogicnew.users.ActiveUsers;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {
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
        CommandType type = CommandType.getAsType(input.substring(0, separatingIndex));

        Creator.CommandCreator creator = creators.get(type);
        if (creator == null) {
            throw new RuntimeException(String.format(FORMAT_STRING, type));
        }

        return creator.create(
                Tokenizer.tokenizeFromSocketChannel(
                        input.substring(separatingIndex + 1)), users, activeUsers);
    }

    private CommandFactory() {
        creators = new HashMap<>();
        for (Creator creator : Creator.values()) {
            Creator.CommandCreator commandCreator = creator.getCreator();
            creators.put(commandCreator.getType(), commandCreator);
        }
    }
}
