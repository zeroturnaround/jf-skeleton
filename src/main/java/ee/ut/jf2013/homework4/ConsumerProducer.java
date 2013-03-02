package ee.ut.jf2013.homework4;


import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

public class ConsumerProducer {

    public static final int PRODUCERS_AMOUNT = 6;
    public static final int CONSUMERS_AMOUNT = 8;

    public static void main(String[] args) {
        //final BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(15);
        final TunedBlockingQueue queue = new TunedBlockingQueue(15);


        for (int i = 0; i < PRODUCERS_AMOUNT; i++) {
            new Thread(new Producer(queue)).start();
        }

        for (int i = 0; i < CONSUMERS_AMOUNT; i++) {
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
            int element = ThreadLocalRandom.current().nextInt();
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
            System.out.println("Got " + integer);
        }
    }
}

class TunedBlockingQueue {

    private final int size;

    private final Stack<Integer> queue = new Stack();


    TunedBlockingQueue(int size) {
        this.size = size;
    }

    public void add(Integer element) {
        synchronized (queue) {
            while (queue.size() >= size) {
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    System.out.println("Cannot wait to add the element -> " + e);
                }
            }
            System.out.println("Added " + element);
            queue.push(element);
            queue.notifyAll();
        }
    }

    public Integer get() {
        Integer result;
        synchronized (queue) {
            while (queue.isEmpty()) {
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    System.out.println("Cannot wait to get the element -> " + e);
                }
            }
            result = queue.pop();
            queue.notifyAll();
        }
        return result;
    }
}
