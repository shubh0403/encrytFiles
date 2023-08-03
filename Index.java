import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

public class Index {

    private static final String AES_ALGORITHM = "AES";

    public static SecretKey generateKey(String key) {
        // Pad or truncate the key to 128 bits (16 bytes)
        byte[] keyBytes = Arrays.copyOf(key.getBytes(StandardCharsets.UTF_8), 16);
        return new SecretKeySpec(keyBytes, AES_ALGORITHM);
    }

    public static void encryptFile(File inputFile, File outputFile, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        try (FileInputStream inputStream = new FileInputStream(inputFile);
             FileOutputStream outputStream = new FileOutputStream(outputFile);
             CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                cipherOutputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    public static void decryptFile(File inputFile, File outputFile, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        try (FileInputStream inputStream = new FileInputStream(inputFile);
             CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Do you want to encrypt (E) or decrypt (D) a file? ");
            String action = scanner.nextLine().trim().toUpperCase();

            if (action.equals("E")) {
                System.out.print("Enter the path of the input file: ");
                String inputFilePath = scanner.nextLine().trim();

                File inputFile = new File(inputFilePath);
                if (!inputFile.exists()) {
                    System.out.println("Input file not found.");
                    return;
                }

                System.out.print("Enter the path for the encrypted output file: ");
                String encryptedFilePath = scanner.nextLine().trim();
                File encryptedFile = new File(encryptedFilePath);

                System.out.print("Enter the encryption key (should be 16 characters): ");
                String encryptionKey = scanner.nextLine().trim();
                SecretKey secretKey = generateKey(encryptionKey);

                encryptFile(inputFile, encryptedFile, secretKey);
                System.out.println("File encrypted successfully.");
            } else if (action.equals("D")) {
                System.out.print("Enter the path of the encrypted file: ");
                String encryptedFilePath = scanner.nextLine().trim();

                File encryptedFile = new File(encryptedFilePath);
                if (!encryptedFile.exists()) {
                    System.out.println("Encrypted file not found.");
                    return;
                }

                System.out.print("Enter the path for the decrypted output file: ");
                String decryptedFilePath = scanner.nextLine().trim();
                File decryptedFile = new File(decryptedFilePath);

                System.out.print("Enter the encryption key (should be 16 characters): ");
                String encryptionKey = scanner.nextLine().trim();
                SecretKey secretKey = generateKey(encryptionKey);

                decryptFile(encryptedFile, decryptedFile, secretKey);
                System.out.println("File decrypted successfully.");
            } else {
                System.out.println("Invalid choice. Please enter 'E' for encryption or 'D' for decryption.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
