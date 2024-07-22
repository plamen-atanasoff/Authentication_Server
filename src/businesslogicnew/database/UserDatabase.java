package businesslogicnew.database;

public class UserDatabase {
    private final String DATABASE_FILE_NAME;

    public UserDatabase() {
        this("userDatabase.txt");
    }

    public UserDatabase(String databaseFileName) {
        this.DATABASE_FILE_NAME = databaseFileName;
    }
}
