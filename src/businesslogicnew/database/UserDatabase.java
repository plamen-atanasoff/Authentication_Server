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

        id = fileManager.readId() + 1;
    }

    public User getUser(String username) throws IOException {
        return fileManager.readUser(username);
    }

    public User addUser(UserCredentials userCredentials) throws IOException {
        User user = new User(id++, false, userCredentials);

        fileManager.writeUser(user);

        return user;
    }

    public User updateUser(User user) throws IOException {
        if (fileManager.userExists(user.id())) {
            fileManager.updateUser(user);
        } else {
            fileManager.writeUser(user);
        }

        return user;
    }

    public boolean deleteUser(int userId) throws IOException {
        if (fileManager.userExists(userId)) {
            fileManager.deleteUser(userId);

            return true;
        }

        return false;
    }
}
