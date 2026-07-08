package com.encryptor;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import javax.crypto.SecretKey;

public class FileEncryptor {

    /**
     * Splits {@code inputPath} into chunks, encrypts each chunk in parallel using an
     * ExecutorService, and writes a binary file to {@code encryptedPath} with the format:
     *
     * <pre>
     *   [4 bytes: numChunks]
     *   for each chunk:
     *     [4 bytes: encryptedChunkLength]
     *     [encryptedChunkLength bytes: IV + ciphertext]
     * </pre>
     */
    public static void encryptFile(String inputPath, String encryptedPath, SecretKey key)
            throws Exception {
        byte[] fileData = Files.readAllBytes(Path.of(inputPath));
        List<byte[]> chunks = filesplitter.split(fileData);
        int numChunks = chunks.size();
        byte[][] encryptedChunks = new byte[numChunks][];

        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        List<Future<?>> futures = new ArrayList<>(numChunks);

        System.out.println("Encrypting " + numChunks + " chunk(s) across up to " + threads + " thread(s)...");

        for (int i = 0; i < numChunks; i++) {
            futures.add(executor.submit(new encryptortask(chunks.get(i), key, i, encryptedChunks)));
        }

        executor.shutdown();
        if (!executor.awaitTermination(5, TimeUnit.MINUTES)) {
            executor.shutdownNow();
            throw new RuntimeException("Encryption timed out");
        }

        for (Future<?> f : futures) {
            f.get(); // re-throws any task exception wrapped in ExecutionException
        }

        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(encryptedPath)))) {
            out.writeInt(numChunks);
            for (byte[] chunk : encryptedChunks) {
                out.writeInt(chunk.length);
                out.write(chunk);
            }
        }
    }

    /**
     * Reads the encrypted file produced by {@link #encryptFile}, decrypts each chunk in
     * parallel, and writes the reconstructed plaintext to {@code outputPath}.
     */
    public static void decryptFile(String encryptedPath, String outputPath, SecretKey key)
            throws Exception {
        int numChunks;
        byte[][] encryptedChunks;

        try (DataInputStream in = new DataInputStream(new BufferedInputStream(
                new FileInputStream(encryptedPath)))) {
            numChunks = in.readInt();
            encryptedChunks = new byte[numChunks][];
            for (int i = 0; i < numChunks; i++) {
                int len = in.readInt();
                encryptedChunks[i] = new byte[len];
                in.readFully(encryptedChunks[i]);
            }
        }

        byte[][] decryptedChunks = new byte[numChunks][];

        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        List<Future<?>> futures = new ArrayList<>(numChunks);

        System.out.println("Decrypting " + numChunks + " chunk(s) across up to " + threads + " thread(s)...");

        for (int i = 0; i < numChunks; i++) {
            futures.add(executor.submit(new DecryptTask(encryptedChunks[i], key, i, decryptedChunks)));
        }

        executor.shutdown();
        if (!executor.awaitTermination(5, TimeUnit.MINUTES)) {
            executor.shutdownNow();
            throw new RuntimeException("Decryption timed out");
        }

        for (Future<?> f : futures) {
            f.get();
        }

        try (FileOutputStream out = new FileOutputStream(outputPath)) {
            for (byte[] chunk : decryptedChunks) {
                out.write(chunk);
            }
        }
    }

    /** Convenience method: encrypts {@code inputPath} → encrypted.bin, then decrypts back to {@code outputPath}. */
    public static void encryptAndDecryptDemo(String inputPath, String outputPath) throws Exception {
        SecretKey key = CryptoUtils.generateAESKey();
        encryptFile(inputPath, "encrypted.bin", key);
        decryptFile("encrypted.bin", outputPath, key);
    }
}
