package businesslogicnew.users;

import java.util.HashMap;
import java.util.Map;

public class ActiveUsers {
    private int sessionId = 1;
    private final long INVALIDATION_INTERVAL;
    private final long SESSION_TIMEOUT;
    private final Map<Integer, User> activeUsers;

    public ActiveUsers() {
        this(1000 * 30, 1000 * 60 * 5);
    }
    public ActiveUsers(long invalidationInterval, long sessionTimeout) {
        this.activeUsers = new HashMap<>();
        this.INVALIDATION_INTERVAL = invalidationInterval;
        this.SESSION_TIMEOUT = sessionTimeout;
    }
}
