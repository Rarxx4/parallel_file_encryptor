package com.encryptor;

import static org.junit.Assert.*;
import org.junit.*;

import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import javax.crypto.SecretKey;

public class AppTest {

    @Test
    public void testAESKeyGeneration() throws Exception {
        SecretKey key = CryptoUtils.generateAESKey();
        assertNotNull(key);
        assertEquals("AES", key.getAlgorithm());
        assertEquals(32, key.getEncoded().length); // 256-bit = 32 bytes
    }

    @Test
    public void testEncryptDecryptRoundtrip() throws Exception {
        SecretKey key = CryptoUtils.generateAESKey();
        byte[] original = "Hello, Parallel World!".getBytes();
        byte[] encrypted = CryptoUtils.encrypt(original, key);
        byte[] decrypted = CryptoUtils.decrypt(encrypted, key);
        assertArrayEquals(original, decrypted);
    }

    @Test
    public void testEncryptProducesDifferentBytes() throws Exception {
        SecretKey key = CryptoUtils.generateAESKey();
        byte[] original = "Test data for encryption".getBytes();
        byte[] encrypted = CryptoUtils.encrypt(original, key);
        assertFalse("Ciphertext must differ from plaintext", Arrays.equals(original, encrypted));
    }

    @Test
    public void testEncryptIVRandomness() throws Exception {
        SecretKey key = CryptoUtils.generateAESKey();
        byte[] data = "Same plaintext".getBytes();
        byte[] enc1 = CryptoUtils.encrypt(data, key);
        byte[] enc2 = CryptoUtils.encrypt(data, key);
        // Two encryptions of the same data should produce different ciphertexts (random IV)
        assertFalse("Each encryption should use a different IV", Arrays.equals(enc1, enc2));
    }

    @Test
    public void testFileSplitterBasic() {
        byte[] data = new byte[10000];
        Arrays.fill(data, (byte) 0xAB);
        List<byte[]> chunks = filesplitter.split(data);

        // ceil(10000 / 4096) = 3 chunks
        assertEquals(3, chunks.size());
        assertEquals(4096, chunks.get(0).length);
        assertEquals(4096, chunks.get(1).length);
        assertEquals(10000 - 2 * 4096, chunks.get(2).length);
    }

    @Test
    public void testFileSplitterExactChunk() {
        byte[] data = new byte[filesplitter.CHUNK_SIZE];
        List<byte[]> chunks = filesplitter.split(data);
        assertEquals(1, chunks.size());
        assertEquals(filesplitter.CHUNK_SIZE, chunks.get(0).length);
    }

    @Test
    public void testFileSplitterSmallData() {
        byte[] data = "tiny".getBytes();
        List<byte[]> chunks = filesplitter.split(data);
        assertEquals(1, chunks.size());
        assertArrayEquals(data, chunks.get(0));
    }

    @Test
    public void testFileSplitterReassembly() {
        byte[] original = new byte[9000];
        for (int i = 0; i < original.length; i++) original[i] = (byte) i;

        List<byte[]> chunks = filesplitter.split(original);

        // Reassemble and verify data integrity
        int totalLen = chunks.stream().mapToInt(c -> c.length).sum();
        byte[] reassembled = new byte[totalLen];
        int pos = 0;
        for (byte[] chunk : chunks) {
            System.arraycopy(chunk, 0, reassembled, pos, chunk.length);
            pos += chunk.length;
        }
        assertArrayEquals(original, reassembled);
    }

    @Test
    public void testParallelEncryptDecrypt() throws Exception {
        Path input = Files.createTempFile("enc_test_input", ".txt");
        Path encrypted = Files.createTempFile("enc_test_enc", ".bin");
        Path output = Files.createTempFile("enc_test_output", ".txt");
        try {
            // Create content larger than one chunk to exercise parallel paths
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 500; i++) sb.append("Line ").append(i).append(": parallel encryption test\n");
            byte[] content = sb.toString().getBytes();
            Files.write(input, content);

            SecretKey key = CryptoUtils.generateAESKey();
            FileEncryptor.encryptFile(input.toString(), encrypted.toString(), key);
            FileEncryptor.decryptFile(encrypted.toString(), output.toString(), key);

            assertArrayEquals(content, Files.readAllBytes(output));
        } finally {
            Files.deleteIfExists(input);
            Files.deleteIfExists(encrypted);
            Files.deleteIfExists(output);
        }
    }

    @Test
    public void testEncryptedFileIsDifferentFromPlaintext() throws Exception {
        Path input = Files.createTempFile("enc_diff_test", ".txt");
        Path encrypted = Files.createTempFile("enc_diff_enc", ".bin");
        try {
            byte[] content = "Sensitive data that must be encrypted".getBytes();
            Files.write(input, content);

            SecretKey key = CryptoUtils.generateAESKey();
            FileEncryptor.encryptFile(input.toString(), encrypted.toString(), key);

            byte[] encBytes = Files.readAllBytes(encrypted);
            assertFalse("Encrypted file must differ from original", Arrays.equals(content, encBytes));
        } finally {
            Files.deleteIfExists(input);
            Files.deleteIfExists(encrypted);
        }
    }

    @Test
    public void testSaveAndLoadKey() throws Exception {
        Path keyFile = Files.createTempFile("test_key", ".bin");
        try {
            SecretKey original = CryptoUtils.generateAESKey();
            CryptoUtils.saveKey(original, keyFile.toString());
            SecretKey loaded = CryptoUtils.loadKey(keyFile.toString());

            assertArrayEquals(original.getEncoded(), loaded.getEncoded());
            assertEquals(original.getAlgorithm(), loaded.getAlgorithm());
        } finally {
            Files.deleteIfExists(keyFile);
        }
    }

    @Test
    public void testFullDemoFlow() throws Exception {
        Path input = Files.createTempFile("demo_input", ".txt");
        Path output = Files.createTempFile("demo_output", ".txt");
        Path encBin = Path.of("encrypted.bin");
        try {
            byte[] content = "Full end-to-end demo content for testing.".getBytes();
            Files.write(input, content);

            FileEncryptor.encryptAndDecryptDemo(input.toString(), output.toString());

            assertArrayEquals(content, Files.readAllBytes(output));
        } finally {
            Files.deleteIfExists(input);
            Files.deleteIfExists(output);
            Files.deleteIfExists(encBin);
        }
    }
}
