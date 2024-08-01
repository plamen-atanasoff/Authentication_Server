package businesslogicnew.database;

public record UserCredentials(int id, String username, String passwordHash, String firstName, String lastName,
                              String email, boolean isAdmin) {

    private static final String DELIMITER = ",";

    private static final String IS_ADMIN = "1";

    private static final String IS_NOT_ADMIN = "0";

    private static final int FIELDS_COUNT = 7;

    public static UserCredentials of(String line) {
        String[] tokens = line.split(DELIMITER);
        return new UserCredentials(
                Integer.parseInt(tokens[0]), tokens[1], tokens[2], tokens[3], tokens[4], tokens[5],
                tokens[6].equals(IS_ADMIN));
    }

    public String[] getValues() {
        String[] values = new String[FIELDS_COUNT];
        values[0] = String.valueOf(id);
        values[1] = username;
        values[2] = passwordHash;
        values[3] = firstName;
        values[4] = lastName;
        values[5] = email;
        values[6] = isAdmin ? IS_ADMIN : IS_NOT_ADMIN;

        return values;
    }
}
