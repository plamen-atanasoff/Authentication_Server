package businesslogicnew.database;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class UserCredentialsTest {
    @Test
    void testOfWorksCorrectly() {
        String line = "2,bobby,\"pass\",Borislav,Petrov,bobi@abv.bg,0";

        UserCredentials user = UserCredentials.of(line);

        assertEquals(2, user.id());
        assertEquals("bobby", user.username());
        assertEquals("Borislav", user.firstName());
        assertEquals("Petrov", user.lastName());
        assertEquals("bobi@abv.bg", user.email());
        assertFalse(user.isAdmin());
    }
}
