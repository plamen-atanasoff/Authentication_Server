package businesslogicnew.database;

import java.io.IOException;

public class UserDatabase {

    private static final String DEFAULT_DATABASE_NAME = "userDatabase.txt";

    private final FileManager fileManager;

    public UserDatabase() {
        this(new FileManager(DEFAULT_DATABASE_NAME));
    }

    public UserDatabase(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public void addUser(UserCredentials user) throws IOException {
        fileManager.writeUser(user);
    }

    public void updateUser(UserCredentials user) {

    }

    public void deleteUser(int userId) {

    }
}
