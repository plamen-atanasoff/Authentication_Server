package businesslogicnew.database;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserDatabaseTest {
    @Test
    void testGetUserWorksCorrectly() throws IOException {
        String username = "test";
        FileManager fileManager = mock();
        User user = mock();
        when(fileManager.readUser(username)).thenReturn(user);

        UserDatabase userDatabase = new UserDatabase(fileManager);

        assertEquals(user, userDatabase.getUser(username));
    }

    @Test
    void testAddUserWorksCorrectly() throws IOException {
        int id = 1;
        UserCredentials credentials = mock();
        FileManager fileManager = mock();

        when(fileManager.readId()).thenReturn(id);

        UserDatabase userDatabase = new UserDatabase(fileManager);

        User user = userDatabase.addUser(credentials);

        verify(fileManager, atMostOnce()).writeUser(any(User.class));

        assertEquals(id + 1, user.id());
        assertFalse(user.isAdmin());
        assertEquals(credentials, user.credentials());
    }

    @Test
    void testUpdateUserWorksCorrectlyWhenUserExists() throws IOException {
        int id = 1;
        FileManager fileManager = mock();
        User user = mock();

        when(user.id()).thenReturn(id);
        when(fileManager.userExists(id)).thenReturn(true);

        UserDatabase userDatabase = new UserDatabase(fileManager);

        User res = userDatabase.updateUser(user);

        verify(fileManager, atMostOnce()).updateUser(user);

        assertEquals(user, res);
    }

    @Test
    void testUpdateUserWorksCorrectlyWhenUserDoesNotExist() throws IOException {
        int id = 1;
        FileManager fileManager = mock();
        User user = mock();

        when(user.id()).thenReturn(id);
        when(fileManager.userExists(id)).thenReturn(false);

        UserDatabase userDatabase = new UserDatabase(fileManager);

        User res = userDatabase.updateUser(user);

        verify(fileManager, atMostOnce()).writeUser(user);

        assertEquals(user, res);
    }

    @Test
    void testDeleteUserWorksCorrectlyWhenUserExists() throws IOException {
        int id = 1;
        FileManager fileManager = mock();

        when(fileManager.userExists(id)).thenReturn(true);

        UserDatabase userDatabase = new UserDatabase(fileManager);

        assertTrue(userDatabase.deleteUser(id));

        verify(fileManager, atMostOnce()).deleteUser(id);
    }

    @Test
    void testDeleteUserWorksCorrectlyWhenUserDoesNotExist() throws IOException {
        int id = 1;
        FileManager fileManager = mock();

        when(fileManager.userExists(id)).thenReturn(false);

        UserDatabase userDatabase = new UserDatabase(fileManager);

        assertFalse(userDatabase.deleteUser(id));

        verify(fileManager, never()).deleteUser(id);
    }
}
