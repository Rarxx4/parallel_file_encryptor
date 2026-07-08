package com.encryptor;

import javax.crypto.SecretKey;

public class encryptortask implements Runnable {

    private final byte[] chunk;
    private final SecretKey key;
    private final int index;
    private final byte[][] results;

    public encryptortask(byte[] chunk, SecretKey key, int index, byte[][] results) {
        this.chunk = chunk;
        this.key = key;
        this.index = index;
        this.results = results;
    }

    @Override
    public void run() {
        try {
            results[index] = CryptoUtils.encrypt(chunk, key);
            System.out.println("[" + Thread.currentThread().getName() + "] Encrypted chunk " + index
                    + " (" + chunk.length + " bytes)");
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed for chunk " + index, e);
        }
    }
}
