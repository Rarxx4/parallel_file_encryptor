package com.encryptor;

import javax.crypto.SecretKey;

public class DecryptTask implements Runnable {

    private final byte[] encryptedChunk;
    private final SecretKey key;
    private final int index;
    private final byte[][] results;

    public DecryptTask(byte[] encryptedChunk, SecretKey key, int index, byte[][] results) {
        this.encryptedChunk = encryptedChunk;
        this.key = key;
        this.index = index;
        this.results = results;
    }

    @Override
    public void run() {
        try {
            results[index] = CryptoUtils.decrypt(encryptedChunk, key);
            System.out.println("[" + Thread.currentThread().getName() + "] Decrypted chunk " + index);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed for chunk " + index, e);
        }
    }
}
