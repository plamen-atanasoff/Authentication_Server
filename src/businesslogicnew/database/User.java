package businesslogicnew.database;

public record User(int id, UserCredentials credentials, boolean isAdmin) {
    public static User of(String line) {
        int first = line.indexOf(',');
        int last = line.lastIndexOf(',');
        UserCredentials userCredentials = UserCredentials.of(line.substring(first + 1, last));

        return new User(Integer.parseInt(line.substring(0, first)), userCredentials,
            !line.substring(last + 1).equals("0"));
    }

    public String[] getValues() {
        String[] credentialsValues = credentials().getValues();
        String[] values = new String[credentialsValues.length + 2];
        values[0] = String.valueOf(id);
        System.arraycopy(credentialsValues, 0, values, 1, credentialsValues.length);
        values[values.length - 1] = isAdmin ? "1" : "0";

        return values;
    }
}
