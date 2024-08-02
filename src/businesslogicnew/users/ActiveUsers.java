package businesslogicnew.users;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ActiveUsers {
    private static final long DEFAULT_SESSION_TIMEOUT_SECONDS = 60 * 5;

    private int sessionId = 1;

    private final long sessionTimeout;

    private final Set<Integer> sessions;

    private final ScheduledExecutorService executor;

    public ActiveUsers() {
        this(DEFAULT_SESSION_TIMEOUT_SECONDS);
    }

    public ActiveUsers(long sessionTimeout) {
        this(sessionTimeout, new HashSet<>());
    }

    public ActiveUsers(long sessionTimeout, Set<Integer> activeUsers) {
        this(sessionTimeout, activeUsers, Executors.newSingleThreadScheduledExecutor());
    }

    public ActiveUsers(long sessionTimeout, Set<Integer> activeUsers, ScheduledExecutorService executor) {
        if (sessionTimeout <= 0) {
            throw new IllegalArgumentException("SessionTimeout must be a positive number");
        }

        this.sessionTimeout = sessionTimeout;
        this.sessions = activeUsers;
        this.executor = executor;
    }

    public int addSession() {
        sessions.add(sessionId);
        scheduleSessionExpiration(sessionId);

        return sessionId++;
    }

    public void removeSession(int sessionId) {
        sessions.remove(sessionId);
    }

    public boolean sessionExists(int sessionId) {
        return sessions.contains(sessionId);
    }

    private void scheduleSessionExpiration(int sessionId) {
        executor.schedule(() -> removeSession(sessionId), sessionTimeout, TimeUnit.SECONDS);
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
