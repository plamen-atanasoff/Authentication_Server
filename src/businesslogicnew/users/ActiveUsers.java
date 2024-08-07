package businesslogicnew.users;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ActiveUsers {
    private static final long DEFAULT_SESSION_TIMEOUT_SECONDS = 60 * 5;

    private int sessionId = 1;

    private final long sessionTimeout;

    private final Map<Integer, Integer> sessions;

    private final ScheduledExecutorService executor;

    public ActiveUsers() {
        this(DEFAULT_SESSION_TIMEOUT_SECONDS);
    }

    public ActiveUsers(long sessionTimeout) {
        this(sessionTimeout, new HashMap<>());
    }

    public ActiveUsers(long sessionTimeout, Map<Integer, Integer> activeUsers) {
        this(sessionTimeout, activeUsers, Executors.newSingleThreadScheduledExecutor());
    }

    public ActiveUsers(long sessionTimeout, Map<Integer, Integer> activeUsers, ScheduledExecutorService executor) {
        if (sessionTimeout <= 0) {
            throw new IllegalArgumentException("SessionTimeout must be a positive number");
        }

        this.sessionTimeout = sessionTimeout;
        this.sessions = activeUsers;
        this.executor = executor;
    }

    public int addSession(int userId) {
        sessions.put(userId, sessionId);
        scheduleSessionExpiration(userId);

        return sessionId++;
    }

    public void removeSession(int userId) {
        sessions.remove(userId);
    }

    public boolean userIsLoggedIn(int userId) {
        return sessions.containsKey(userId);
    }

    public boolean sessionExists(int sessionId) {
        return sessions.containsValue(sessionId);
    }

    private void scheduleSessionExpiration(int userId) {
        executor.schedule(() -> removeSession(userId), sessionTimeout, TimeUnit.SECONDS);
    }

    public void shutdown() {
        executor.shutdown();
        try {
            //noinspection ResultOfMethodCallIgnored
            executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch(InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
