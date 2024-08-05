package businesslogicnew.database;

import java.util.Objects;

public record UserCredentials(String username, String passwordHash, String firstName, String lastName, String email) {

    private static final String DELIMITER = ",";

    private static final int FIELDS_COUNT = 5;

    private static final int USERNAME_POSITION = 0;

    private static final int PASSWORD_HASH_POSITION = 1;

    private static final int FIRST_NAME_POSITION = 2;

    private static final int LAST_NAME_POSITION = 3;

    private static final int EMAIL_POSITION = 4;

    public static UserCredentials of(String line) {
        String[] tokens = line.split(DELIMITER);
        String passwordHash = tokens[PASSWORD_HASH_POSITION]
            .substring(1, tokens[PASSWORD_HASH_POSITION].length() - 1); // remove enclosing quotation marks

        return new UserCredentials(tokens[USERNAME_POSITION], passwordHash, tokens[FIRST_NAME_POSITION],
            tokens[LAST_NAME_POSITION], tokens[EMAIL_POSITION]);
    }

    public int getFieldsCount() {
        return FIELDS_COUNT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCredentials that = (UserCredentials) o;

        return Objects.equals(username, that.username) &&
            Objects.equals(passwordHash, that.passwordHash) &&
            Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) &&
            Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, passwordHash, firstName, lastName, email);
    }
}
