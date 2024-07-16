package businesslogic.database;

import businesslogic.tokenizer.Tokenizer;

public record UserCredentials(String username, String password, String firstName, String lastName,
                              String email, boolean isAdmin) {
    private static final int USERNAME_POS = 0;
    private static final int PASSWORD_POS = 1;
    private static final int FIRST_NAME_POS = 2;
    private static final int LAST_NAME_POS_POS = 3;
    private static final int EMAIL_POS = 4;
    private static final int ADMIN_STATUS_POS = 5;

    public static UserCredentials of(String line) {
        String[] tokens = Tokenizer.tokenizeFromDatabase(line);

        boolean isAdmin = Integer.parseInt(tokens[ADMIN_STATUS_POS].strip()) == 1;

        return new UserCredentials(tokens[USERNAME_POS], tokens[PASSWORD_POS], tokens[FIRST_NAME_POS],
            tokens[LAST_NAME_POS_POS], tokens[EMAIL_POS], isAdmin);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        UserCredentials other = (UserCredentials) obj;
        return username.equals(other.username);
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = 31 * result + username.hashCode();

        return result;
    }

    @Override
    public String toString() {
        String passwordInQuotes = "\"" + password + "\"";
        String isAdminStr = String.valueOf((isAdmin ? 1 : 0));
        return String.join(",", username, passwordInQuotes, firstName, lastName, email, isAdminStr);
    }
}

