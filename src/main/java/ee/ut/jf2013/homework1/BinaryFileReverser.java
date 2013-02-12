package ee.ut.jf2013.homework1;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static java.lang.System.nanoTime;

public class BinaryFileReverser {

    public static final String PREFIX = "output_";

    public String reverseBinaryFileContent(String fileName) throws IOException {
        if (!new File(fileName).exists()) {
            throw new IOException("File doesn't exist -> " + fileName);
        }
        String outputFileName = PREFIX + fileName.replaceAll(File.separator, "");
        try (RandomAccessFile input = new RandomAccessFile(fileName, "r");
             RandomAccessFile output = new RandomAccessFile(outputFileName, "rw")) {

            long sizeInBytes = input.getChannel().size();
            System.out.println("Filename=" + fileName + ", file size=" + sizeInBytes);

            long startTime = nanoTime();

            performReadWrite(input, output);

            long duration = nanoTime() - startTime;
            long speed = sizeInBytes * 1000_000 / duration; // 1 kB = 1 B / 1000; divide; 1 s =  1 nano / 1000_000_000
            System.out.println("Duration=" + (double) duration / 1000_000_000 + " seconds, speed= " + speed + " kB/s");
        }
        return outputFileName;
    }

    private void performReadWrite(RandomAccessFile input, RandomAccessFile output) throws IOException {
        FileChannel inChannel = input.getChannel();
        MappedByteBuffer inMap = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outMap = output.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());
        long pointer = inChannel.size() - 1;
        while (pointer > -1) {
            byte current = inMap.get(); // position++
            outMap.put((int) pointer, current);
            pointer--;
        }
    }
}