package businesslogicnew.passwordencryptor;

import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class PasswordEncryptorTest {
    @Test
    void testGenerateHash() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String password = "mypass";

        String res1 = PasswordEncryptor.generateHash(password);
        String res2 = PasswordEncryptor.generateHash(password);

        assertEquals(res1, res2);
    }
}
