package businesslogic.commandnew;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

class CommandFactory {
    private static CommandFactory instance;
    private final Map<CommandType, Command.CommandCreator> creators;

    public static CommandFactory getInstance() {
        if (instance == null) {
            instance = new CommandFactory();
        }

        return instance;
    }

    public void addCreator(CommandType type, Command.CommandCreator creator) {
        creators.put(type, creator);
    }

    public Command createCommand(String input) {
        String[] tokens = input.split(" ");
        CommandType type = CommandType.getAsType(tokens[0]);

        Command.CommandCreator creator = creators.get(type);
        if (creator == null) {
            throw new RuntimeException("Creator with " + type + " type does not exist");
        }

        // there is no need for a copy, just a view of tokens without the first element
        return creator.create(Arrays.copyOfRange(tokens, 1, tokens.length));
    }

    private CommandFactory() {
        creators = new EnumMap<>(CommandType.class);
    }
}
