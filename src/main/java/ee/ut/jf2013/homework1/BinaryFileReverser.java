package ee.ut.jf2013.homework1;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static ee.ut.jf2013.homework1.PerformJobMeter.Job;
import static ee.ut.jf2013.homework1.PerformJobMeter.createMeter;

public class BinaryFileReverser {

    public static final String PREFIX = "output_";

    public void reverseBinaryFileContentInSingleFile(String fileName) throws IOException {
        checkInputFileExistence(fileName);
        try (RandomAccessFile file = new RandomAccessFile(fileName, "rw")) {
            final long size = file.getChannel().size();
            createMeter(fileName, size).execute(new Job() {
                @Override
                public void perform() throws IOException {
                    FileChannel inChannel = file.getChannel();
                    MappedByteBuffer inMap = inChannel.map(FileChannel.MapMode.READ_WRITE, 0, size);
                    long pointer = size - 1;
                    while (pointer > size / 2) {
                        int opposite = (int) (size - pointer - 1);
                        byte right = inMap.get((int) pointer); // get right
                        inMap.put((int) (pointer), inMap.get(opposite)); // set left to right
                        inMap.put(opposite, right);
                        pointer--;
                    }
                }
            });
        }
    }

    public String reverseBinaryFileContent(String fileName) throws IOException {
        checkInputFileExistence(fileName);
        String outputFileName = PREFIX + fileName.replaceAll(File.separator, "");
        try (RandomAccessFile input = new RandomAccessFile(fileName, "r");
             RandomAccessFile output = new RandomAccessFile(outputFileName, "rw")) {
            createMeter(fileName, input.getChannel().size()).execute(new Job() {
                @Override
                public void perform() throws IOException {
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
            });
        }
        return outputFileName;
    }

    private static void checkInputFileExistence(String fileName) throws IOException {
        if (!new File(fileName).exists()) {
            throw new IOException("File doesn't exist -> " + fileName);
        }
    }
}