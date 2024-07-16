package businesslogic.users;

import businesslogic.exception.InvalidSessionIdException;
import businesslogic.exception.UserNotFoundException;
import businesslogic.exception.InvalidSessionIdException;
import businesslogic.exception.UserNotFoundException;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ActiveUsers {
    private final long sessionTimeout;
    private Map<Integer, User> activeUsers;

    public ActiveUsers(long invalidationInterval, long sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
        this.activeUsers = new HashMap<>();

        Thread invalidationThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(invalidationInterval);
                    invalidateExpiredSessions();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        invalidationThread.setDaemon(true);
        invalidationThread.start();
    }

    private void invalidateExpiredSessions() {
        Iterator<Map.Entry<Integer, User>> iterator = activeUsers.entrySet().iterator();
        Instant currentTime = Instant.now();

        while (iterator.hasNext()) {
            Map.Entry<Integer, User> entry = iterator.next();
            User user = entry.getValue();
            Duration elapsedTime = Duration.between(user.getLoginTime(), currentTime);

            if (elapsedTime.toMillis() > sessionTimeout) {
                iterator.remove();
            }
        }
    }

    public void add(User user) {
        if (contains(user.getUserSessionId())) {
            return;
        }

        activeUsers.put(user.getUserSessionId(), user);
    }

    public void remove(int sessionId) {
        if (!contains(sessionId)) {
            return;
        }

        activeUsers.remove(sessionId);
    }

    public boolean contains(String username) {
        return activeUsers.values().stream()
            .map(User::getUsername)
            .anyMatch(u -> u.equals(username));
    }

    public void remove(String username) {
        if (!contains(username)) {
            return;
        }

        activeUsers.remove(getUser(username).getUserSessionId());
    }

    public boolean contains(int sessionId) {
        return activeUsers.containsKey(sessionId);
    }

    public Map<Integer, User> getActiveUsers() {
        return activeUsers;
    }

    public User getUser(int sessionId) {
        for (User user : activeUsers.values()) {
            if (user.getUserSessionId() == sessionId) {
                return user;
            }
        }

        throw new InvalidSessionIdException("No such sessionId in the system");
    }

    public User getUser(String username) {
        for (User user : activeUsers.values()) {
            if (username.equals(user.getUsername())) {
                return user;
            }
        }

        throw new UserNotFoundException("user with given username is not logged in");
    }
}
