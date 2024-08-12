package businesslogicnew.database;

public record User(int id, boolean isAdmin, UserCredentials credentials) {

    private static final String PASSWORD_FORMAT = "\"%s\"";

    private static final String SEPARATOR = ",";

    private static final String IS_ADMIN = "1";

    private static final String IS_NOT_ADMIN = "0";

    public static User of(String line) {
        int first = line.indexOf(SEPARATOR);
        int second = line.indexOf(SEPARATOR, first + 1);
        UserCredentials userCredentials = UserCredentials.of(line.substring(second + 1));
        boolean adminStatus = line.substring(first + 1, second).equals(IS_ADMIN);

        return new User(Integer.parseInt(line.substring(0, first)), adminStatus, userCredentials);
    }

    @Override
    public String toString() {
        String passwordInQuotes = String.format(PASSWORD_FORMAT, credentials.passwordHash());
        String isAdminStr = isAdmin ? IS_ADMIN : IS_NOT_ADMIN;

        return String.join(SEPARATOR, String.valueOf(id), isAdminStr, credentials.username(),
            passwordInQuotes, credentials.firstName(), credentials.lastName(), credentials.email());
    }
}
