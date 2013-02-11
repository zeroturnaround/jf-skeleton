package ee.ut.jf2013.homework1;

import java.io.IOException;
import java.io.RandomAccessFile;

public class BinaryFileReverser {

    public static final String PREFIX = "output_";

    public String reverseBinaryFileContent(String fileName) throws IOException {
        String outputFileName = PREFIX + fileName;
        try (RandomAccessFile input = new RandomAccessFile(fileName, "rw");
             RandomAccessFile output = new RandomAccessFile(outputFileName, "rw")) {
            long pointer = input.length() - 1;
            while (pointer > -1) {
                input.seek(pointer);
                output.writeByte(input.readByte());
                pointer--;
            }
        }
        return outputFileName;
    }
}