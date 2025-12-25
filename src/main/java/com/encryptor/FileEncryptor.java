package com.encryptor;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.crypto.SecretKey;

public class FileEncryptor {

    public static void encryptAndDecryptDemo(String inputPath, String outputPath) throws Exception {

        // 1. Read file
        byte[] fileData = Files.readAllBytes(Path.of(inputPath));

        // 2. Generate key
        SecretKey key = CryptoUtils.generateAESKey();

        // 3. Encrypt
        byte[] encrypted = CryptoUtils.encrypt(fileData, key);
        Files.write(Path.of("encrypted.bin"), encrypted);

        // 4. Decrypt
        byte[] decrypted = CryptoUtils.decrypt(encrypted, key);
        Files.write(Path.of(outputPath), decrypted);
    }
}

