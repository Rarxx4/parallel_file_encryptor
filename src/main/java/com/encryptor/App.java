package com.encryptor;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
       String inputFile = "input.txt";
        String outputFile = "encrypted.bin";

        FileEncryptor encryptor = new FileEncryptor();
        encryptor.encryptFile(inputFile, outputFile);

        System.out.println("Encryption completed.");
    }
}
