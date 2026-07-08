package com.encryptor;

import java.util.ArrayList;
import java.util.List;

public class filesplitter {

    public static final int CHUNK_SIZE = 4096; // 4 KB per chunk

    /** Splits {@code data} into fixed-size chunks of {@link #CHUNK_SIZE} bytes (last chunk may be smaller). */
    public static List<byte[]> split(byte[] data) {
        List<byte[]> chunks = new ArrayList<>();
        int offset = 0;
        while (offset < data.length) {
            int size = Math.min(CHUNK_SIZE, data.length - offset);
            byte[] chunk = new byte[size];
            System.arraycopy(data, offset, chunk, 0, size);
            chunks.add(chunk);
            offset += size;
        }
        return chunks;
    }
}
