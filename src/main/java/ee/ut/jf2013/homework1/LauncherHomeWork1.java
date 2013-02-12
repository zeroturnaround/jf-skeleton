package ee.ut.jf2013.homework1;

import java.io.IOException;

public class LauncherHomeWork1 {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            throw new RuntimeException("Please specify file name");
        }
        new BinaryFileReverser().reverseBinaryFileContentInSingleFile(args[0]);
    }
}
