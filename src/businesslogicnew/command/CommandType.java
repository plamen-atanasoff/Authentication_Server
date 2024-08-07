package businesslogicnew.command;

import java.util.Arrays;

public enum CommandType {
    // login -–username <username> --password <password>
    LOGIN_PASSWORD("login"),

    // register --username <username> --password <password> --first-name <firstName> --last-name <lastName> --email <email>
    REGISTER("register"),

    // login -–session-id <sessionId>
    LOGIN_SESSION_ID("login");

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
