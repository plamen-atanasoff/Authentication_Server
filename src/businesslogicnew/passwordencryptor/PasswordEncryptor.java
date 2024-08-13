package businesslogicnew.passwordencryptor;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PasswordEncryptor {

    private static final int ITERATIONS = 65_536;

    private static final int KEY_LENGTH = 256;

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    private static final int SALT_LENGTH = 16;

    private static final String SALT_FILE_NAME = "salt.bin";

    private final byte[] salt;

    private static PasswordEncryptor passwordEncryptor;

    public static PasswordEncryptor getInstance() {
        if (passwordEncryptor == null) {
            passwordEncryptor = new PasswordEncryptor();
        }

        return passwordEncryptor;
    }

    public String generateHash(String password)
        throws NoSuchAlgorithmException, InvalidKeySpecException {

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hash = skf.generateSecret(spec).getEncoded();

        return Base64.getEncoder().encodeToString(hash);
    }

    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);

        return salt;
    }

    private PasswordEncryptor() {
        File file = new File(SALT_FILE_NAME);
        if (file.exists()) {
            try (var fis = new FileInputStream(file)) {
                salt = fis.readAllBytes();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try (var fos = new FileOutputStream(file)) {
                salt = generateSalt();
                fos.write(salt);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
