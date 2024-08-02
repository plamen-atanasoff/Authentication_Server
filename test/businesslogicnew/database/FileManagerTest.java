package businesslogicnew.database;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileManagerTest {
    @Test
    void testUserExistsWorksCorrectly() throws IOException {
        String line0 = "username,\"password\",firstName,lastName,email,adminStatus";
        String line1 = "1,plamen40,\"pass\",Plamen,Petrov,plam@abv.bg,1";
        String line2 = "2,bobby,\"pass\",Borislav,Petrov,bobi@abv.bg,0";
        String line3 = "3,teddy,\"pass\",Tony,Petrov,tony@abv.bg,0";

        Path tempFilePath = Files.createTempFile("tempUserDatabase", ".txt");

        try (var bw = Files.newBufferedWriter(tempFilePath)) {
            bw.write(line0);
            bw.newLine();

            bw.write(line1);
            bw.newLine();
            bw.write(line2);
            bw.newLine();
            bw.write(line3);
            bw.newLine();
        }

        FileManager fm = new FileManager(tempFilePath);
        assertTrue(fm.userExists(1));
        assertTrue(fm.userExists(2));
        assertTrue(fm.userExists(3));
        assertFalse(fm.userExists(4));
        assertFalse(fm.userExists(0));

        Files.delete(tempFilePath);
    }

    @Test
    void testWriteUserWorksCorrectly() throws IOException {
        String line0 = "username,\"password\",firstName,lastName,email,adminStatus";
        String line1 = "1,plamen40,\"pass\",Plamen,Petrov,plam@abv.bg,1";
        String line2 = "2,bobby,\"pass\",Borislav,Petrov,bobi@abv.bg,0";
        String line3 = "3,teddy,\"pass\",Tony,Petrov,tony@abv.bg,0";

        Path tempFilePath = Files.createTempFile("tempUserDatabase", ".txt");

        try (var bw = Files.newBufferedWriter(tempFilePath)) {
            bw.write(line0);
            bw.newLine();

            bw.write(line1);
            bw.newLine();
            bw.write(line2);
            bw.newLine();
        }

        FileManager fm = new FileManager(tempFilePath);
        fm.writeUser(User.of(line3));

        assertTrue(fm.userExists(1));
        assertTrue(fm.userExists(2));
        assertTrue(fm.userExists(3));
        assertFalse(fm.userExists(4));
        assertFalse(fm.userExists(0));

        Files.delete(tempFilePath);
    }

    @Test
    void testUpdateUserWorksCorrectly() throws IOException {
        String line0 = "username,\"password\",firstName,lastName,email,adminStatus";
        String line1 = "1,plamen40,\"pass\",Plamen,Petrov,plam@abv.bg,1";
        String line2 = "2,bobby,\"pass\",Borislav,Petrov,bobi@abv.bg,0";
        String line3 = "3,teddy,\"pass\",Tony,Petrov,tony@abv.bg,0";

        Path tempFilePath = Files.createTempFile("tempUserDatabase", ".txt");

        try (var bw = Files.newBufferedWriter(tempFilePath)) {
            bw.write(line0);
            bw.newLine();

            bw.write(line1);
            bw.newLine();
            bw.write(line2);
            bw.newLine();
            bw.write(line3);
            bw.newLine();
        }

        FileManager fm = new FileManager(tempFilePath);

        fm.updateUser(User.of("2,bobbynew,\"pass\",Borislav,Petrov,bobi@abv.bg,0"));

        assertTrue(fm.userExists(1));
        assertTrue(fm.userExists(2));
        assertTrue(fm.userExists(3));
        assertFalse(fm.userExists(4));
        assertFalse(fm.userExists(0));

        List<String> lines = Files.readAllLines(tempFilePath).stream().skip(1).toList(); // skip data description line
        UserCredentials updatedUser = UserCredentials.of(lines.get(1)); // second line is updated user
        assertEquals("bobbynew", updatedUser.username());

        Files.delete(tempFilePath);
    }

    @Test
    void testDeleteUserWorksCorrectly() throws IOException {
        String line0 = "username,\"password\",firstName,lastName,email,adminStatus";
        String line1 = "1,plamen40,\"pass\",Plamen,Petrov,plam@abv.bg,1";
        String line2 = "2,bobby,\"pass\",Borislav,Petrov,bobi@abv.bg,0";
        String line3 = "3,teddy,\"pass\",Tony,Petrov,tony@abv.bg,0";

        Path tempFilePath = Files.createTempFile("tempUserDatabase", ".txt");

        try (var bw = Files.newBufferedWriter(tempFilePath)) {
            bw.write(line0);
            bw.newLine();

            bw.write(line1);
            bw.newLine();
            bw.write(line2);
            bw.newLine();
            bw.write(line3);
            bw.newLine();
        }

        FileManager fm = new FileManager(tempFilePath);

        fm.deleteUser(2);

        assertTrue(fm.userExists(1));
        assertFalse(fm.userExists(2));
        assertTrue(fm.userExists(3));
        assertFalse(fm.userExists(4));
        assertFalse(fm.userExists(0));
    }
}
