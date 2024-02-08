package bg.sofia.uni.fmi.mjt.authenticationserver.businesslogic.users;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActiveUsersTest {
    private static final long INVALIDATION_INTERVAL = 5 * 60 * 1000;
    private static final long SESSION_TIMEOUT = 5 * 60 * 1000;

    @Test
    void testAddBehavesCorrectly() {
        ActiveUsers activeUsers = new ActiveUsers(INVALIDATION_INTERVAL, SESSION_TIMEOUT);

        String username1 = "plamen40";
        String username2 = "vikito";
        String username3 = "pavel123";
        User user1 = new User(username1, false);
        User user2 = new User(username2, false);
        User user3 = new User(username3, false);

        activeUsers.add(user1);
        activeUsers.add(user2);
        activeUsers.add(user3);

        Map<Integer, User> users = activeUsers.getActiveUsers();

        assertEquals(3, users.size());
        assertTrue(users.containsKey(user1.getUserSessionId()));
        assertTrue(users.containsKey(user2.getUserSessionId()));
        assertTrue(users.containsKey(user3.getUserSessionId()));
    }

    @Test
    void testAddDoesNothingWhenPassedExistingUsername() {
        ActiveUsers activeUsers = new ActiveUsers(INVALIDATION_INTERVAL, SESSION_TIMEOUT);

        String username1 = "plamen40";
        String username2 = "vikito";
        User user1 = new User(username1, false);
        User user2 = new User(username2, false);

        activeUsers.add(user1);
        activeUsers.add(user2);

        activeUsers.add(user1);

        Map<Integer, User> users = activeUsers.getActiveUsers();

        assertEquals(2, users.size());
        assertTrue(users.containsKey(user1.getUserSessionId()));
        assertTrue(users.containsKey(user2.getUserSessionId()));
    }

    @Test
    void testRemoveBehavesCorrectly() {
        ActiveUsers activeUsers = new ActiveUsers(INVALIDATION_INTERVAL, SESSION_TIMEOUT);

        String username1 = "plamen40";
        String username2 = "vikito";
        User user1 = new User(username1, false);
        User user2 = new User(username2, false);

        activeUsers.add(user1);
        activeUsers.add(user2);

        activeUsers.remove(user1.getUserSessionId());

        Map<Integer, User> users = activeUsers.getActiveUsers();

        assertEquals(1, users.size());
        assertTrue(users.containsKey(user2.getUserSessionId()));
    }

    @Test
    void testRemoveDoesNothingWhenPassedNonExistingUserCredentials() {
        ActiveUsers activeUsers = new ActiveUsers(INVALIDATION_INTERVAL, SESSION_TIMEOUT);

        String username1 = "plamen40";
        String username2 = "vikito";
        User user1 = new User(username1, false);
        User user2 = new User(username2, false);

        activeUsers.add(user1);
        activeUsers.add(user2);

        int invalidSessionId = 99999;
        activeUsers.remove(invalidSessionId);

        Map<Integer, User> users = activeUsers.getActiveUsers();

        assertEquals(2, users.size());
        assertTrue(users.containsKey(user1.getUserSessionId()));
        assertTrue(users.containsKey(user2.getUserSessionId()));
    }

    @Test
    void testContainsBehavesCorrectly() {
        ActiveUsers activeUsers = new ActiveUsers(INVALIDATION_INTERVAL, SESSION_TIMEOUT);

        String username1 = "plamen40";
        String username2 = "vikito";
        String username3 = "notexisting";
        User user1 = new User(username1, false);
        User user2 = new User(username2, false);
        User user3 = new User(username3, false);

        activeUsers.add(user1);
        activeUsers.add(user2);

        assertTrue(activeUsers.contains(user1.getUserSessionId()));
        assertTrue(activeUsers.contains(user2.getUserSessionId()));
        assertFalse(activeUsers.contains(user3.getUserSessionId()));
    }

    @Disabled
    @Test
    void testSessionTimeoutRemovesInvalidUser() throws InterruptedException {
        ActiveUsers activeUsers = new ActiveUsers(500, 1000);

        String username1 = "plamen40";
        String username2 = "vikito";
        User user1 = new User(username1, false);
        User user2 = new User(username2, false);

        activeUsers.add(user1);
        Thread.sleep(2000);
        activeUsers.add(user2);

        assertFalse(activeUsers.contains(user1.getUserSessionId()));
        assertTrue(activeUsers.contains(user2.getUserSessionId()));
    }

    @Test
    void testGetUserByUsernameReturnsCorrectUser() {
        ActiveUsers activeUsers = new ActiveUsers(INVALIDATION_INTERVAL, SESSION_TIMEOUT);

        String username1 = "plamen40";
        String username2 = "vikito";
        User user1 = new User(username1, false);
        User user2 = new User(username2, false);

        activeUsers.add(user1);
        activeUsers.add(user2);

        User res = activeUsers.getUser(username1);

        assertEquals(user1, res);
    }

    @Test
    void testGetUserBySessionIdReturnsCorrectUser() {
        ActiveUsers activeUsers = new ActiveUsers(INVALIDATION_INTERVAL, SESSION_TIMEOUT);

        String username1 = "plamen40";
        String username2 = "vikito";
        User user1 = new User(username1, false);
        User user2 = new User(username2, false);

        activeUsers.add(user1);
        activeUsers.add(user2);

        User res = activeUsers.getUser(user1.getUserSessionId());

        assertEquals(user1, res);
    }
}
