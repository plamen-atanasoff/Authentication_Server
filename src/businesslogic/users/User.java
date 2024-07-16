package businesslogic.users;

import java.time.Instant;

public class User {
    private static int sessionId = 0;
    private int userSessionId;
    private Instant loginTime;
    private String username;
    private boolean isAdmin;

    public User(String username, boolean isAdmin) {
        this.username = username;
        this.userSessionId = getSessionId();
        this.loginTime = Instant.now();
        this.isAdmin = isAdmin;
    }

    private int getSessionId() {
        return sessionId++;
    }

    public int getUserSessionId() {
        return userSessionId;
    }

    public Instant getLoginTime() {
        return loginTime;
    }

    public String getUsername() {
        return username;
    }

    public void makeAdmin() {
        isAdmin = true;
    }

    public void removeAdmin() {
        isAdmin = false;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void changeUsername(String username) {
        this.username = username;
    }
}
