package com.encryptor;

import javax.crypto.SecretKey;

/**
 * Entry point for the Parallel File Encryptor.
 *
 * Usage:
 *   java -jar parallel_file-1.0-SNAPSHOT.jar                              # demo using input.txt
 *   java -jar parallel_file-1.0-SNAPSHOT.jar encrypt <input> <output.bin> # encrypt file, saves key to key.bin
 *   java -jar parallel_file-1.0-SNAPSHOT.jar decrypt <input.bin> <output> # decrypt using key.bin
 */
public class App {

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                runDemo();
            } else {
                runCli(args);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runCli(String[] args) throws Exception {
        switch (args[0]) {
            case "encrypt" -> {
                if (args.length < 3) {
                    System.out.println("Usage: App encrypt <input_file> <encrypted_output>");
                    return;
                }
                SecretKey key = CryptoUtils.generateAESKey();
                long start = System.currentTimeMillis();
                FileEncryptor.encryptFile(args[1], args[2], key);
                System.out.printf("Encrypted in %d ms -> %s%n", System.currentTimeMillis() - start, args[2]);
                CryptoUtils.saveKey(key, "key.bin");
                System.out.println("AES key saved to key.bin");
            }
            case "decrypt" -> {
                if (args.length < 3) {
                    System.out.println("Usage: App decrypt <encrypted_file> <output_file>");
                    return;
                }
                SecretKey key = CryptoUtils.loadKey("key.bin");
                long start = System.currentTimeMillis();
                FileEncryptor.decryptFile(args[1], args[2], key);
                System.out.printf("Decrypted in %d ms -> %s%n", System.currentTimeMillis() - start, args[2]);
            }
            default -> {
                System.out.println("Unknown command: " + args[0]);
                System.out.println("Commands: encrypt, decrypt");
            }
        }
    }

    private static void runDemo() throws Exception {
        System.out.println("=== Parallel File Encryptor Demo ===");
        System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors());
        System.out.println("Input file: input.txt");

        long start = System.currentTimeMillis();
        FileEncryptor.encryptAndDecryptDemo("input.txt", "output.txt");
        long elapsed = System.currentTimeMillis() - start;

        System.out.println("Done in " + elapsed + " ms");
        System.out.println("input.txt -> encrypted.bin -> output.txt");
    }
}
