package businesslogicnew.database;

import java.io.IOException;
import java.nio.file.Path;

public class UserDatabase {

    private static final Path DEFAULT_DATABASE_PATH = Path.of("userDatabase.txt");

    private final FileManager fileManager;

    public UserDatabase() {
        this(new FileManager(DEFAULT_DATABASE_PATH));
    }

    public UserDatabase(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public void addUser(UserCredentials user) throws IOException {
        fileManager.writeUser(user);
    }

    public void updateUser(UserCredentials user) throws IOException {
        fileManager.updateUser(user);
    }

    public void deleteUser(int userId) throws IOException {
        fileManager.deleteUser(userId);
    }
}
