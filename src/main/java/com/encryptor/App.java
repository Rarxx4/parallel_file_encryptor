package com.encryptor;

public class App {
    public static void main(String[] args) {
        try {
            FileEncryptor.encryptAndDecryptDemo(
                "input.txt",
                "output.txt"
            );
            System.out.println("Encryption and Decryption successful!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
