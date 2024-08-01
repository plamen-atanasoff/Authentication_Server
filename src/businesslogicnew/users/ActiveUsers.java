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

    private final Set<Integer> activeUsers = new HashSet<>();

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public ActiveUsers() {
        this(DEFAULT_SESSION_TIMEOUT_SECONDS);
    }
    public ActiveUsers(long sessionTimeout) {
        if (sessionTimeout <= 0) {
            throw new IllegalArgumentException("SessionTimeout must be a positive number");
        }

        this.sessionTimeout = sessionTimeout;
    }

    public int addSession() {
        activeUsers.add(sessionId);
        scheduleSessionExpiration(sessionId);

        return sessionId++;
    }

    public boolean removeSession(int sessionId) {
        return activeUsers.remove(sessionId);
    }

    private void scheduleSessionExpiration(int sessionId) {
        executor.schedule(() -> {
            activeUsers.remove(sessionId);
        }, sessionTimeout, TimeUnit.SECONDS);
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
