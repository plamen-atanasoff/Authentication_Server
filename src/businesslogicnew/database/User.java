package businesslogicnew.database;

public record User(int id, boolean isAdmin, UserCredentials credentials) {
    private static final char SEPARATOR = ',';

    private static final String IS_ADMIN = "1";

    private static final String IS_NOT_ADMIN = "0";

    private static final int FIELDS_COUNT = 3;

    private static final int ID_POSITION = 0;

    private static final int ADMIN_STATUS_POSITION = 1;

    private static final int CREDENTIALS_POSITION = 2;

    public static User of(String line) {
        int first = line.indexOf(SEPARATOR);
        int second = line.indexOf(SEPARATOR, first + 1);
        UserCredentials userCredentials = UserCredentials.of(line.substring(second + 1));
        boolean adminStatus = line.substring(first + 1, second).equals(IS_ADMIN);

        return new User(Integer.parseInt(line.substring(0, first)), adminStatus, userCredentials);
    }

    public String[] getValues() {
        // size is credentials' fields count plus id and isAdmin
        int credentialsFieldsCount = credentials().getFieldsCount();
        String[] values = new String[credentialsFieldsCount + FIELDS_COUNT - 1];

        values[ID_POSITION] = String.valueOf(id);
        values[ADMIN_STATUS_POSITION] = isAdmin ? IS_ADMIN : IS_NOT_ADMIN;
        System.arraycopy(credentials.getValues(), 0, values, CREDENTIALS_POSITION, credentialsFieldsCount);

        return values;
    }
}
