package businesslogicnew.database;

import java.io.IOException;
import java.nio.file.Path;

public class UserDatabase {

    private static final Path DEFAULT_DATABASE_PATH = Path.of("userDatabase.txt");

    private int id;

    private final FileManager fileManager;

    public UserDatabase() throws IOException {
        this(new FileManager(DEFAULT_DATABASE_PATH));
    }

    public UserDatabase(Path databaseFilePath) throws IOException {
        this(new FileManager(databaseFilePath));
    }

    public UserDatabase(FileManager fileManager) throws IOException {
        this.fileManager = fileManager;

        id = fileManager.readId();
    }

    public User getUser(String username) throws IOException {
        return fileManager.readUser(username);
    }

    public void addUser(UserCredentials userCredentials) throws IOException {
        User user = new User(id++, userCredentials, false);

        fileManager.writeUser(user);
    }

    public void updateUser(User user) throws IOException {
        fileManager.updateUser(user);
    }

    public void deleteUser(int userId) throws IOException {
        fileManager.deleteUser(userId);
    }
}
