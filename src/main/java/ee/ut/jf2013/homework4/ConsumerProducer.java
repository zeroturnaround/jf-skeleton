package ee.ut.jf2013.homework4;


import java.util.ArrayList;
import java.util.List;

public class ConsumerProducer {

    public static void main(String[] args) {
        //final BlockingQueue<Integer> messages = new ArrayBlockingQueue<>(15);
        final TunedBlockingQueue queue = new TunedBlockingQueue(15);


        for (int i = 0; i < 6; i++) {
            new Thread(new Producer(queue)).start();
        }

        for (int i = 0; i < 8; i++) {
            new Thread(new Consumer(queue)).start();
        }
    }
}

class Producer implements Runnable {
    private final TunedBlockingQueue queue;

    public Producer(TunedBlockingQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            int element = (int) (Math.random());
            System.out.println(element);
            queue.add(element);
        }
    }
}

class Consumer implements Runnable {
    private final TunedBlockingQueue queue;

    public Consumer(TunedBlockingQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            Integer integer = queue.get();
            System.out.println(integer);
        }
    }
}

class TunedBlockingQueue {

    private final int size;

    private final List<Integer> queue = new ArrayList();


    TunedBlockingQueue(int size) {
        this.size = size;
    }

    public synchronized void add(Integer element) {
        if (queue.size() > size) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Cannot wait to add the element -> " + e);
            }
        }
        queue.add(element);

    }

    public synchronized Integer get() {
        if (queue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Cannot wait to get the element -> " + e);
            }
        }
        return queue.iterator().next();
    }
}
