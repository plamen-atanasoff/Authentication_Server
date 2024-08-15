package businesslogicnew.command;

import java.util.Arrays;

public enum CommandType {
    // login -–username <username> --password <password>
    LOGIN_PASSWORD("login"),

    // register --username <username> --password <password> --first-name <firstName> --last-name <lastName> --email <email>
    REGISTER("register"),

    // login --session-id <sessionId>
    LOGIN_SESSION_ID("login"),

    // only session-id is a required parameter
    // update-user --session-id <sessionId>  --new-username <newUsername> --new-first-name <newFirstName> --new-last-name <newLastName> --new-email <email>
    UPDATE("update-user"),


    // change-password --session-id <sessionId> --username <username> --old-password <oldPassword> --new-password <newPassword>
    CHANGE_PASSWORD("change-password"),

    // logout --session-id <sessionId>
    LOGOUT("logout"),

    // add-admin-user --session-id <sessionId> --username <username>
    ADD_ADMIN("add-admin-user"),

    // remove-admin-user --session-id <sessionId> --username <username>
    REMOVE_ADMIN("remove-admin-user");

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
