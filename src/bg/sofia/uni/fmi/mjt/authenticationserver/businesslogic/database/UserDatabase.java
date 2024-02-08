package bg.sofia.uni.fmi.mjt.authenticationserver.businesslogic.database;

import bg.sofia.uni.fmi.mjt.authenticationserver.businesslogic.exception.UserNotFoundException;

import java.util.Collections;
import java.util.Set;

public class UserDatabase {
    private int admins;
    private UserDatabaseFile usersFile;
    private Set<UserCredentials> users;

    public UserDatabase(UserDatabaseFile usersFile) {
        this.usersFile = usersFile;
        this.users = usersFile.readUsers();

        admins = countAdmins();
    }

    private int countAdmins() {
        return (int) users.stream()
            .filter(UserCredentials::isAdmin)
            .count();
    }

    public void add(UserCredentials userCredentials) {
        if (exists(userCredentials.username())) {
            return;
        }

        usersFile.addUser(userCredentials);

        users.add(userCredentials);
    }

    public boolean exists(String username) {
        return users.stream()
            .map(UserCredentials::username)
            .anyMatch(username::equals);
    }

    public void remove(String username) {
        boolean userExists = users.removeIf(u -> u.username().equals(username));
        if (userExists) {
            usersFile.removeUser(username);
        }
    }

    public Set<UserCredentials> getUsers() {
        return Collections.unmodifiableSet(users);
    }

    public UserCredentials getUserCredentialsByUsername(String username) {
        for (UserCredentials userCredentials : users) {
            if (userCredentials.username().equals(username)) {
                return userCredentials;
            }
        }

        throw new UserNotFoundException("user with given username does not exist in database");
    }

    public int getAdminsCount() {
        return admins;
    }

    public void addAdmin() {
        admins++;
    }

    public void removeAdmin() {
        admins--;
    }
}