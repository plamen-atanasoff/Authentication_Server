package businesslogicnew.users;

import java.net.SocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ActiveUsers {

    private static final int TERMINATION_TIMEOUT_SECONDS = 60;

    private static final String ILLEGAL_SESSION_TIMEOUT_MESSAGE = "SessionTimeout must be a positive number";

    private static final long DEFAULT_SESSION_TIMEOUT_SECONDS = 300; // 5 minutes

    private static final long DEFAULT_LOCK_CLIENT_SECONDS = 15;

    private int sessionId = 1;

    private final long sessionTimeout;

    private final Map<Integer, Integer> sessions;

    private final ScheduledExecutorService executor;

    private final Set<SocketAddress> lockedClients;

    public ActiveUsers() {
        this(DEFAULT_SESSION_TIMEOUT_SECONDS);
    }

    public ActiveUsers(long sessionTimeout) {
        this(sessionTimeout, new ConcurrentHashMap<>(), ConcurrentHashMap.newKeySet());
    }

    public ActiveUsers(long sessionTimeout, Map<Integer, Integer> activeUsers, Set<SocketAddress> lockedClients) {
        this(sessionTimeout, activeUsers, Executors.newSingleThreadScheduledExecutor(), lockedClients);
    }

    public ActiveUsers(long sessionTimeout, Map<Integer, Integer> activeUsers, ScheduledExecutorService executor,
                       Set<SocketAddress> lockedClients) {
        if (sessionTimeout <= 0) {
            throw new IllegalArgumentException(ILLEGAL_SESSION_TIMEOUT_MESSAGE);
        }

        this.sessionTimeout = sessionTimeout;
        this.sessions = activeUsers;
        this.executor = executor;
        this.lockedClients = lockedClients;
    }

    public int addSession(int userId) {
        sessions.put(sessionId, userId);
        scheduleSessionExpiration(sessionId, userId);

        return sessionId++;
    }

    public boolean sessionExists(int sessionId) {
        return sessions.containsKey(sessionId);
    }

    public int getUserId(int sessionId) {
        return sessions.get(sessionId);
    }

    private void scheduleSessionExpiration(int sessionId, int userId) {
        executor.schedule(() -> sessions.remove(sessionId, userId), sessionTimeout, TimeUnit.SECONDS);
    }
    
    public void lockClient(SocketAddress socketAddress) {
        if (lockedClients.contains(socketAddress)) {
            return;
        }

        lockedClients.add(socketAddress);

        executor.schedule(() -> lockedClients.remove(socketAddress), DEFAULT_LOCK_CLIENT_SECONDS, TimeUnit.SECONDS);
    }

    public boolean isLocked(SocketAddress socketAddress) {
        return lockedClients.contains(socketAddress);
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
