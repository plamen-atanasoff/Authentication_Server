package businesslogic.database;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class UserDatabaseTest {
    @Test
    void testAddBehavesCorrectly() {
        UserDatabaseFile mockedUserDatabaseFile = mock();
        UserDatabase userDatabase = new UserDatabase(mockedUserDatabaseFile);

        String line1 = "plambataBG,123456,Plamen,Atanasov,email@abv.bg,0";
        UserCredentials userCredentials1 = UserCredentials.of(line1);
        String line2 = "vik40,123456,Viktor,Atanasov,email@abv.bg,0";
        UserCredentials userCredentials2 = UserCredentials.of(line2);
        String line3 = "bebi1234,222222,Bancho,Stamenov,email@dir.bg,0";
        UserCredentials userCredentials3 = UserCredentials.of(line3);

        userDatabase.add(userCredentials1);
        userDatabase.add(userCredentials2);
        userDatabase.add(userCredentials3);

        Set<UserCredentials> res = userDatabase.getUsers();

        assertEquals(3, res.size());
        assertTrue(res.contains(userCredentials1));
        assertTrue(res.contains(userCredentials2));
        assertTrue(res.contains(userCredentials3));
    }

    @Test
    void testAddDoesNothingWhenPassedExistingUserCredentials() {
        UserDatabaseFile mockedUserDatabaseFile = mock();
        UserDatabase userDatabase = new UserDatabase(mockedUserDatabaseFile);

        String line1 = "plambataBG,123456,Plamen,Atanasov,email@abv.bg,0";
        UserCredentials userCredentials1 = UserCredentials.of(line1);
        String line2 = "vik40,123456,Viktor,Atanasov,email@abv.bg,0";
        UserCredentials userCredentials2 = UserCredentials.of(line2);

        userDatabase.add(userCredentials1);
        userDatabase.add(userCredentials2);

        userDatabase.add(userCredentials1);

        Set<UserCredentials> res = userDatabase.getUsers();

        assertEquals(2, res.size());
        assertTrue(res.contains(userCredentials1));
        assertTrue(res.contains(userCredentials2));
    }

    @Test
    void testRemoveBehavesCorrectly() {
        UserDatabaseFile mockedUserDatabaseFile = mock();
        UserDatabase userDatabase = new UserDatabase(mockedUserDatabaseFile);

        String line1 = "plambataBG,123456,Plamen,Atanasov,email@abv.bg,0";
        UserCredentials userCredentials1 = UserCredentials.of(line1);
        String line2 = "vik40,123456,Viktor,Atanasov,email@abv.bg,0";
        UserCredentials userCredentials2 = UserCredentials.of(line2);

        userDatabase.add(userCredentials1);
        userDatabase.add(userCredentials2);

        userDatabase.remove("plambataBG");

        Set<UserCredentials> res = userDatabase.getUsers();

        assertEquals(1, res.size());
        assertTrue(res.contains(userCredentials2));
    }

    @Test
    void testRemoveDoesNothingWhenPassedNonExistingUserCredentials() {
        UserDatabaseFile mockedUserDatabaseFile = mock();
        UserDatabase userDatabase = new UserDatabase(mockedUserDatabaseFile);

        String line1 = "plambataBG,123456,Plamen,Atanasov,email@abv.bg,0";
        UserCredentials userCredentials1 = UserCredentials.of(line1);
        String line2 = "vik40,123456,Viktor,Atanasov,email@abv.bg,0";
        UserCredentials userCredentials2 = UserCredentials.of(line2);

        userDatabase.add(userCredentials1);

        userDatabase.remove("vik40");

        Set<UserCredentials> res = userDatabase.getUsers();

        assertEquals(1, res.size());
        assertTrue(res.contains(userCredentials1));
        assertFalse(res.contains(userCredentials2));
    }

    @Test
    void testGetUserCredentialsByUsernameReturnsCorrectUserCredentials() {
        UserDatabaseFile mockedUserDatabaseFile = mock();
        UserDatabase userDatabase = new UserDatabase(mockedUserDatabaseFile);

        String line1 = "plambataBG,123456,Plamen,Atanasov,email@abv.bg,0";
        UserCredentials userCredentials1 = UserCredentials.of(line1);
        String line2 = "vik40,123456,Viktor,Atanasov,email@abv.bg,0";
        UserCredentials userCredentials2 = UserCredentials.of(line2);

        userDatabase.add(userCredentials1);
        userDatabase.add(userCredentials2);

        UserCredentials res = userDatabase.getUserCredentialsByUsername(userCredentials2.username());

        assertEquals(userCredentials2, res);
    }
}

