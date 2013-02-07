package ee.ut.jf2013.homework1;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Homework {
    public long square(int x) {
        return x*x;
    }

    public String reverse(String str) {
        return StringUtils.reverse(str);
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            throw new RuntimeException("Please specify file name");
        }
        String fileName = args[0];
        new Homework().reverseBinaryFileContent(fileName);
    }

    private void reverseBinaryFileContent(String fileName) throws IOException {
        try (RandomAccessFile input = new RandomAccessFile(fileName, "rw");
             RandomAccessFile output = new RandomAccessFile("output.txt", "rw")) {
            long pointer = input.length() - 1;
            while (pointer > -1) {
                input.seek(pointer);
                output.writeByte(input.readByte());
                pointer--;
                output.seek(output.getFilePointer() + 1);
            }
        }
    }
}
