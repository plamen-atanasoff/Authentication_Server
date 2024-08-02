package businesslogicnew.command;

import java.util.Arrays;

public enum CommandType {
    LOGIN("login"),
    REGISTER("register");

    private final String name;

    CommandType(String name) {
        this.name = name;
    }

    public static CommandType getAsType(String s) {
        return Arrays.stream(CommandType.values())
                .filter(t -> t.name.equals(s))
                .findFirst()
                .orElse(null);
    }
}
