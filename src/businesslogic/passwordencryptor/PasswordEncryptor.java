package businesslogic.passwordencryptor;

import businesslogic.exception.CipherException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class PasswordEncryptor {
    private static final String CIPHER_EXCEPTION_MESSAGE = "encrypt operation cannot be completed successfully";
    private static final int KILOBYTE = 1024;
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String KEY_FILE_PATH = "secret.key";
    private static final int KEY_SIZE_IN_BITS = 128;
    private static final SecretKey SECRET_KEY;

    static {
        try {
            if (Files.notExists(Path.of(KEY_FILE_PATH))) {
                SECRET_KEY = generateSecretKey();
                persistSecretKey();
            } else {
                SECRET_KEY = loadSecretKey();
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new CipherException(CIPHER_EXCEPTION_MESSAGE, e);
        }

    }

    private static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
        keyGenerator.init(KEY_SIZE_IN_BITS);

        return keyGenerator.generateKey();
    }

    private static void persistSecretKey() throws IOException {
        byte[] keyBytes = SECRET_KEY.getEncoded();

        Files.write(Path.of(KEY_FILE_PATH), keyBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static SecretKey loadSecretKey() throws IOException {
        byte[] keyBytes = Files.readAllBytes(Path.of(KEY_FILE_PATH));
        return new SecretKeySpec(keyBytes, ENCRYPTION_ALGORITHM);
    }

    public static String encryptPassword(String password) {
        String encryptedPassword;
        try (var inputStream = new ByteArrayInputStream(password.getBytes());
             var outputStream = new ByteArrayOutputStream()) {
            encrypt(inputStream, outputStream);
            encryptedPassword = outputStream.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return encryptedPassword;
    }

    public static void encrypt(InputStream inputStream, OutputStream outputStream) {
        try {
            encryptData(inputStream, outputStream);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            throw new CipherException(CIPHER_EXCEPTION_MESSAGE, e);
        }
    }

    private static void encryptData(InputStream inputStream, OutputStream outputStream)
        throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        manipulateData(inputStream, outputStream, Cipher.ENCRYPT_MODE);
    }

    public static void decrypt(InputStream inputStream, OutputStream outputStream) {
        try {
            decryptData(inputStream, outputStream);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            throw new CipherException(CIPHER_EXCEPTION_MESSAGE, e);
        }
    }

    private static void decryptData(InputStream inputStream, OutputStream outputStream)
        throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        manipulateData(inputStream, outputStream, Cipher.DECRYPT_MODE);
    }

    private static void manipulateData(InputStream inputStream, OutputStream outputStream, int mode)
        throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(mode, loadSecretKey());

        try (var outputStreamCipher = new CipherOutputStream(outputStream, cipher)) {
            byte[] buffer = new byte[KILOBYTE];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStreamCipher.write(buffer, 0, bytesRead);
            }
        }
    }
}

