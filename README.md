# Parallel File Encryptor (Java + Multithreading + AES)

This project implements a high-performance parallel file encryption system using Java, multithreading, and AES cryptography.  
It demonstrates how large files can be encrypted more efficiently by splitting them into chunks and processing each chunk in parallel.

---

## Features

- Parallel encryption using Java multithreading  
- AES encryption (128-bit or 256-bit)  
- Supports encryption of any file type (text, images, PDFs, binary files, etc.)  
- Combines encrypted chunks into a single output file  
- Modular and extensible code structure  
- JUnit support through Maven  
- Decryption module planned for future updates  

---

## How It Works

1. The input file is divided into fixed-size chunks (for example, 4 KB).  
2. Each chunk is assigned to a separate worker thread.  
3. All chunks are encrypted in parallel using AES.  
4. The encrypted chunks are combined into a single output file.  
5. (Upcoming) Decryption reverses the process to reconstruct the original file.

This approach demonstrates real-world concepts in parallel computing, performance optimization, and cryptography.

---

## Project Structure
parallel_file/
├── pom.xml
├── README.md
└── src/
├── main/java/com/encryptor/
│ ├── Main.java
│ ├── FileEncryptor.java
│ ├── FileChunker.java
│ ├── EncryptTask.java
│ └── DecryptTask.java (future implementation)
└── test/java/

## Technologies Used

- Java 22  
- Maven build system  
- AES encryption (`javax.crypto`)  
- Multithreading (Threads, Runnable, ExecutorService)  
- JUnit for testing  

---

## Running the Project

### 1. Compile the project using Maven
```sh
mvn clean install
```

### 2. Run the main program
   java -cp target/parallel_file-1.0-SNAPSHOT.jar com.encryptor.Main

## Example Workflow

-Specify an input file (for example, input.txt).
-The file is divided into chunks.
-Each chunk is encrypted by a separate thread.
-All encrypted chunks are merged into one output file named encrypted.bin.

## Future Enhancements

-Parallel decryption module
-Use of ThreadPoolExecutor for optimized thread management
-Performance benchmarking (parallel vs single-thread)
-AES-256 support
-JavaFX or CLI user interface
-Progress tracking and logging
