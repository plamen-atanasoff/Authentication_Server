package businesslogicnew.users;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ActiveUsers {

    private static final int TERMINATION_TIMEOUT_SECONDS = 60;
    private static final String ILLEGAL_SESSION_TIMEOUT_MESSAGE = "SessionTimeout must be a positive number";

    private static final long DEFAULT_SESSION_TIMEOUT_SECONDS = 10;

    private static final long DEFAULT_LOCK_CLIENT_SECONDS = 15;

    private int sessionId = 1;

    private final long sessionTimeout;

    private final Map<Integer, Integer> sessions;

    private final ScheduledExecutorService executor;

    private final Set<Integer> lockedClients;

    public ActiveUsers() {
        this(DEFAULT_SESSION_TIMEOUT_SECONDS);
    }

    public ActiveUsers(long sessionTimeout) {
        this(sessionTimeout, new ConcurrentHashMap<>(), ConcurrentHashMap.newKeySet());
    }

    public ActiveUsers(long sessionTimeout, Map<Integer, Integer> activeUsers, Set<Integer> lockedUsers) {
        this(sessionTimeout, activeUsers, Executors.newSingleThreadScheduledExecutor(), lockedUsers);
    }

    public ActiveUsers(long sessionTimeout, Map<Integer, Integer> activeUsers, ScheduledExecutorService executor,
                       Set<Integer> lockedUsers) {
        if (sessionTimeout <= 0) {
            throw new IllegalArgumentException(ILLEGAL_SESSION_TIMEOUT_MESSAGE);
        }

        this.sessionTimeout = sessionTimeout;
        this.sessions = activeUsers;
        this.executor = executor;
        this.lockedClients = lockedUsers;
    }

    public int addSession(int userId) {
        sessions.put(userId, sessionId);
        scheduleSessionExpiration(userId, sessionId);

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

    private void scheduleSessionExpiration(int userId, int sessionId) {
        executor.schedule(() -> sessions.remove(userId, sessionId), sessionTimeout, TimeUnit.SECONDS);
    }
    
    public void lockClient(int userId) {
        if (lockedClients.contains(userId)) {
            return;
        }

        lockedClients.add(userId);

        System.out.println(lockedClients);

        executor.schedule(() -> lockedClients.remove(userId), DEFAULT_LOCK_CLIENT_SECONDS, TimeUnit.SECONDS);
    }

    public boolean isLocked(int userId) {
        return lockedClients.contains(userId);
    }

    public void shutdown() {
        executor.shutdown();
        try {
            //noinspection ResultOfMethodCallIgnored
            executor.awaitTermination(TERMINATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch(InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
