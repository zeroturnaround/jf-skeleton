package ee.ut.jf2013.homework1;

import java.io.IOException;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.System.nanoTime;

public class PerformJobMeter {

    private String fileName;
    private long sizeInBytes;

    private  PerformJobMeter() {
    }

    public PerformJobMeter(String fileName, long sizeInBytes) {
        this.fileName = fileName;
        this.sizeInBytes = sizeInBytes;
    }

    public static PerformJobMeter createMeter(String fileName, long sizeInBytes) {
        return new PerformJobMeter(fileName, sizeInBytes);
    }

    public void execute(Job job) throws IOException {
        System.out.println("Filename=" + fileName + ", file size=" + sizeInBytes + ", Integer.MAX_VALUE=" + MAX_VALUE);
        long startTime = nanoTime();

        job.perform();

        long duration = nanoTime() - startTime;
        long speed = sizeInBytes * 1000_000 / duration; // 1 kB = 1 B / 1000; divide; 1 s =  1 nano / 1000_000_000
        System.out.println("Duration=" + (double) duration / 1000_000_000 + " seconds, speed= " + speed + " kB/s");
    }

    public interface Job {
        void perform() throws IOException;
    }
}
