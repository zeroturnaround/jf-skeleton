package ee.ut.jf2013.homework6;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Logger extends Thread {


    private BlockingQueue<String> logs = new ArrayBlockingQueue<>(1000);

    private boolean printable = true;

    @Override
    public void run() {
        while (printable) {
            try {
                System.out.println(logs.take());
            } catch (InterruptedException e) {
                System.err.println("Error happened : " + e.getMessage());
            }
        }
    }

    public void log(String message) {
        logs.offer(message);
    }

    public void close() {
        printable = false;
    }
}
