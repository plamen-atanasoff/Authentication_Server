package businesslogic.database;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UserCredentialsTest {
    @Test
    void testOfInitializesCorrectInstance() {
        String line = "bebi1234,\"222222\",Bancho,Stamenov,email@dir.bg,0";
        String username = "bebi1234";
        String password = "222222";
        String firstName = "Bancho";
        String lastName = "Stamenov";
        String email = "email@dir.bg";
        boolean isAdmin = false;

        UserCredentials userCredentials = UserCredentials.of(line);

        assertEquals(username, userCredentials.username());
        assertEquals(password, userCredentials.password());
        assertEquals(firstName, userCredentials.firstName());
        assertEquals(lastName, userCredentials.lastName());
        assertEquals(email, userCredentials.email());
        assertEquals(isAdmin, userCredentials.isAdmin());
    }

    @Test
    void testEqualsBehavesCorrectly() {
        String line1 = "bebi1234,\"222222\",Bancho,Stamenov,email@dir.bg,0";
        String line2 = "vik40,\"123456\",Viktor,Atanasov,email@abv.bg,0";
        UserCredentials userCredentials1 = UserCredentials.of(line1);
        UserCredentials userCredentials2 = UserCredentials.of(line2);
        UserCredentials userCredentials3 = UserCredentials.of(line1);

        assertEquals(userCredentials1, userCredentials3);
        assertNotEquals(userCredentials1, userCredentials2);
    }

    @Test
    void testHashCodeBehavesCorrectly() {
        String line1 = "bebi1234,\"222222\",Bancho,Stamenov,email@dir.bg,0";
        String line2 = "vik40,\"123456\",Viktor,Atanasov,email@abv.bg,0";
        UserCredentials userCredentials1 = UserCredentials.of(line1);
        UserCredentials userCredentials2 = UserCredentials.of(line2);
        UserCredentials userCredentials3 = UserCredentials.of(line1);

        assertEquals(userCredentials1.hashCode(), userCredentials3.hashCode());
        assertNotEquals(userCredentials1.hashCode(), userCredentials2.hashCode());
    }

    @Test
    void testToStringReturnsCorrectString() {
        String line = "bebi1234,\"222222\",Bancho,Stamenov,email@dir.bg,0";
        UserCredentials userCredentials = UserCredentials.of(line);

        String res = userCredentials.toString();

        assertEquals(line, res);
    }
}

