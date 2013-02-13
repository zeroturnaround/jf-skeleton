package ee.ut.jf2013;

import ee.ut.jf2013.homework1.BinaryFileReverser;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            throw new RuntimeException("Please specify file name");
        }
        new BinaryFileReverser().reverseBinaryFileContentInSingleFile(args[0]);
    }
}
