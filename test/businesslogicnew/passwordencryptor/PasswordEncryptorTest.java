package businesslogicnew.passwordencryptor;

import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class PasswordEncryptorTest {
    @Test
    void testGenerateHash() throws NoSuchAlgorithmException, InvalidKeySpecException {
        PasswordEncryptor passwordEncryptor = PasswordEncryptor.getInstance();

        String password = "mypass";

        String res1 = passwordEncryptor.generateHash(password);
        String res2 = passwordEncryptor.generateHash(password);

        assertEquals(res1, res2);
    }
}
