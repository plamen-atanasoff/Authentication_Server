package businesslogicnew.database;

public record UserCredentials(String username, String passwordHash, String firstName, String lastName, String email) {

    private static final String DELIMITER = ",";

    private static final int FIELDS_COUNT = 5;

    public static UserCredentials of(String line) {
        String[] tokens = line.split(DELIMITER);
        String passwordHash = tokens[1].substring(1, tokens[1].length() - 1); // remove enclosing quotation marks

        return new UserCredentials( tokens[0], passwordHash, tokens[2], tokens[3], tokens[4]);
    }

    public String[] getValues() {
        String[] values = new String[FIELDS_COUNT];
        values[0] = username;
        values[1] = passwordHash;
        values[2] = firstName;
        values[3] = lastName;
        values[4] = email;

        return values;
    }
}
