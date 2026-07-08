package com.encryptor;

import java.io.*;
import java.security.SecureRandom;
import javax.crypto.*;
import javax.crypto.spec.*;

public class CryptoUtils {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    static final int IV_SIZE = 16; // AES block size in bytes

    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return keyGen.generateKey();
    }

    /** Encrypts data and returns [IV (16 bytes) | ciphertext]. */
    public static byte[] encrypt(byte[] data, SecretKey key) throws Exception {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] ciphertext = cipher.doFinal(data);

        byte[] result = new byte[IV_SIZE + ciphertext.length];
        System.arraycopy(iv, 0, result, 0, IV_SIZE);
        System.arraycopy(ciphertext, 0, result, IV_SIZE, ciphertext.length);
        return result;
    }

    /** Decrypts data produced by {@link #encrypt}. Expects [IV (16 bytes) | ciphertext]. */
    public static byte[] decrypt(byte[] ivAndCiphertext, SecretKey key) throws Exception {
        byte[] iv = new byte[IV_SIZE];
        System.arraycopy(ivAndCiphertext, 0, iv, 0, IV_SIZE);
        byte[] ciphertext = new byte[ivAndCiphertext.length - IV_SIZE];
        System.arraycopy(ivAndCiphertext, IV_SIZE, ciphertext, 0, ciphertext.length);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher.doFinal(ciphertext);
    }

    /** Saves an AES SecretKey to a file as raw bytes. */
    public static void saveKey(SecretKey key, String keyPath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(keyPath)) {
            fos.write(key.getEncoded());
        }
    }

    /** Loads an AES SecretKey from a file saved by {@link #saveKey}. */
    public static SecretKey loadKey(String keyPath) throws Exception {
        byte[] encoded;
        try (FileInputStream fis = new FileInputStream(keyPath)) {
            encoded = fis.readAllBytes();
        }
        return new SecretKeySpec(encoded, "AES");
    }
}
