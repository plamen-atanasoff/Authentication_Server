package businesslogic.commandnew;

import java.util.Arrays;

public enum CommandType {
    TEMP1("temp1"), TEMP2("temp2");

    private final String name;

    private CommandType(String name) {
        this.name = name;
    }

    public static CommandType getAsType(String s) {
        return Arrays.stream(CommandType.values()).filter(t -> t.name.equals(s)).findFirst().orElse(null);
    }
}
