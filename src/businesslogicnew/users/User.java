package businesslogicnew.users;

import java.time.Instant;
import java.time.LocalDateTime;

public class User {
    private final LocalDateTime loginTime;
    private String username;
    private boolean isAdmin;

    public User(String username, boolean isAdmin) {
        this.loginTime = LocalDateTime.now();
        this.username = username;
        this.isAdmin = isAdmin;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void makeAdmin() {
        isAdmin = true;
    }

    public void removeAdmin() {
        isAdmin = false;
    }

    public void changeUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof User o)) {
            return false;
        }

        return loginTime.equals(o.loginTime) && username.equals(o.username) && (isAdmin == o.isAdmin);
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int res = 1;
        res = prime * res + loginTime.hashCode();
        res = prime * res + username.hashCode();
        res = prime * res + (isAdmin ? 1231 : 1237);

        return res;
    }
}
