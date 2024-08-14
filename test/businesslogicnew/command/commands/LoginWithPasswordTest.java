package businesslogicnew.command.commands;

import businesslogicnew.logger.ServerLogger;
import businesslogicnew.passwordencryptor.PasswordEncryptor;
import businesslogicnew.database.User;
import businesslogicnew.database.UserCredentials;
import businesslogicnew.database.UserDatabase;
import businesslogicnew.users.ActiveUsers;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoginWithPasswordTest {
    @Test
    void testCommandExecutesCorrectly() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String username = "pavel123";
        String password = "myPass";
        int sessionId = 2;
        int userId = 3;
        String passwordHash = PasswordEncryptor.getInstance().generateHash(password);
        UserCredentials credentials = mock();
        when(credentials.passwordHash()).thenReturn(passwordHash);
        User u = mock();
        when(u.credentials()).thenReturn(credentials);
        when(u.id()).thenReturn(userId);
        UserDatabase db = mock();
        when(db.getUser(username)).thenReturn(u);
        ActiveUsers au = mock();
        when(au.addSession(userId)).thenReturn(sessionId);
        SelectionKey key = mock();

        LoginWithPassword command = new LoginWithPassword(username, password, db, au, key);

        String res = command.execute();

        verify(key, atMostOnce()).attachment();
        verify(key, atMostOnce()).attach(0);

        assertEquals(sessionId, Integer.parseInt(res));
    }

    @Test
    void testMultipleFailedLoggingAttemptsLogs() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String username = "pavel123";
        String password = "myPass";
        int userId = 3;
        String wrongPasswordHash = "testWrongPasswordHash";
        UserCredentials credentials = mock();
        when(credentials.passwordHash()).thenReturn(wrongPasswordHash);
        User u = mock();
        when(u.credentials()).thenReturn(credentials);
        when(u.id()).thenReturn(userId);
        UserDatabase db = mock();
        when(db.getUser(username)).thenReturn(u);
        ActiveUsers au = mock();
        SocketChannel channel = mock();
        SocketAddress socketAddress = mock();
        SelectionKey key = mock();
        when(key.isValid()).thenReturn(true);
        when(key.channel()).thenReturn(channel);
        when(channel.getRemoteAddress()).thenReturn(socketAddress);
        ServerLogger serverLogger = mock();

        LoginWithPassword command = new LoginWithPassword(username, password, db, au, key, serverLogger);

        String res = command.execute();

        verify(serverLogger, atMostOnce()).log(String.format(
            "%s is locked because of multiple failed login attempts", socketAddress));

        assertEquals("Invalid login credentials", res);
    }
}
