package ee.ut.jf2013.homework4;


import java.util.ArrayList;
import java.util.List;

public class ProducerConsumerProblem {

    public static final int PRODUCERS_AMOUNT = 6;
    public static final int CONSUMERS_AMOUNT = 8;
    public static final int TIME_TO_WAIT = 2000;

    public static void main(String[] args) throws InterruptedException {
        final TunedBlockingQueue queue = new TunedBlockingQueue(15);

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < PRODUCERS_AMOUNT; i++) {
            threads.add(new Producer(queue));
        }

        for (int i = 0; i < CONSUMERS_AMOUNT; i++) {
            threads.add(new Consumer(queue));
        }

        for (Thread thread : threads) {
            thread.start();
        }

        Thread.sleep(TIME_TO_WAIT);

        for (Thread thread : threads) {
            thread.interrupt();
        }
    }
}
