package businesslogicnew.users;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ActiveUsersTest {
    @Test
    void testAddSessionWorksCorrectly() {
        Set<Integer> sessions = mock();

        ActiveUsers activeUsers = new ActiveUsers(300, sessions);

        activeUsers.addSession();

        verify(sessions, atMostOnce()).add(anyInt());
    }

    @Test
    void testRemoveSessionWorksCorrectly() {
        Set<Integer> sessions = mock();

        ActiveUsers activeUsers = new ActiveUsers(300, sessions);

        int id = activeUsers.addSession();
        activeUsers.removeSession(id);

        verify(sessions, atMostOnce()).add(anyInt());
        verify(sessions, atMostOnce()).remove(id);
    }

    @Test
    void testSessionExistsWorksCorrectly() {
        Set<Integer> sessions = mock();

        ActiveUsers activeUsers = new ActiveUsers(300, sessions);

        int id = activeUsers.addSession();

        when(sessions.contains(id)).thenReturn(true);

        assertTrue(activeUsers.sessionExists(id));

        verify(sessions, atMostOnce()).add(anyInt());
    }

    @Test
    void testShutdownWorksCorrectly() {
        Set<Integer> sessions = mock();
        ScheduledExecutorService executor = mock();

        ActiveUsers activeUsers = new ActiveUsers(300, sessions, executor);

        activeUsers.shutdown();

        verify(executor, atMostOnce()).shutdown();
    }
}
