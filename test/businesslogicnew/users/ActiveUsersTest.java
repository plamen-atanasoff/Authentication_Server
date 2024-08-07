package businesslogicnew.users;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ActiveUsersTest {
    @Test
    void testAddSessionWorksCorrectly() {
        Map<Integer, Integer> sessions = mock();
        int userId = 2;

        ActiveUsers activeUsers = new ActiveUsers(300, sessions);

        activeUsers.addSession(userId);

        verify(sessions, atMostOnce()).put(eq(userId), anyInt());
    }

    @Test
    void testRemoveSessionWorksCorrectly() {
        Map<Integer, Integer> sessions = mock();
        int userId = 2;

        ActiveUsers activeUsers = new ActiveUsers(300, sessions);

        activeUsers.addSession(userId);
        activeUsers.removeSession(userId);

        verify(sessions, atMostOnce()).put(eq(userId), anyInt());
        verify(sessions, atMostOnce()).remove(userId);
    }

    @Test
    void testSessionExistsWorksCorrectly() {
        Map<Integer, Integer> sessions = mock();
        int userId = 2;

        ActiveUsers activeUsers = new ActiveUsers(300, sessions);

        activeUsers.addSession(userId);

        when(sessions.containsKey(userId)).thenReturn(true);

        assertTrue(activeUsers.userIsLoggedIn(userId));

        verify(sessions, atMostOnce()).put(eq(userId), anyInt());
    }

    @Test
    void testShutdownWorksCorrectly() {
        Map<Integer, Integer> sessions = mock();
        ScheduledExecutorService executor = mock();

        ActiveUsers activeUsers = new ActiveUsers(300, sessions, executor);

        activeUsers.shutdown();

        verify(executor, atMostOnce()).shutdown();
    }

    @Test
    void testSessionTimeoutWorksCorrectly() throws InterruptedException {
        int sessionTimeout = 1;
        Map<Integer, Integer> sessions = mock();
        ScheduledExecutorService executor = mock();
        int userId = 2;

        ActiveUsers activeUsers = new ActiveUsers(sessionTimeout, sessions, executor);

        activeUsers.addSession(userId);
        Thread.sleep(sessionTimeout * 1000);

        verify(executor, atMostOnce()).schedule(any(Runnable.class), eq(sessionTimeout), eq(TimeUnit.SECONDS));
        verify(sessions, atMostOnce()).put(eq(userId), anyInt());
        verify(sessions, atMostOnce()).remove(userId);
    }
}
