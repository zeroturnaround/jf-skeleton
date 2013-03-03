package ee.ut.jf2013.homework4;


import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

public class ConsumerProducer {

    public static final int PRODUCERS_AMOUNT = 6;
    public static final int CONSUMERS_AMOUNT = 8;
    public static final int TIME_TO_WAIT = 5000;

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
            //thread.join();
        }

    }
}

class Producer extends User {
    Producer(TunedBlockingQueue queue) {
        super(queue);
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            int element = ThreadLocalRandom.current().nextInt();
            queue.add(element);
            System.out.println(Thread.currentThread().getName() + " added " + element);
        }
        System.out.println(Thread.currentThread().getName() + " finished it's execution");
    }
}

class Consumer extends User {
    Consumer(TunedBlockingQueue queue) {
        super(queue);
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            Integer integer = queue.get();
            System.out.println(Thread.currentThread().getName() + " got " + integer);
        }
        System.out.println(Thread.currentThread().getName() + " finished it's execution");
    }
}

abstract class User extends Thread {
    final TunedBlockingQueue queue;
    User(TunedBlockingQueue queue) {
        this.queue = queue;
        setName(getClass().getSimpleName() + "_" + getName());
    }
}

class TunedBlockingQueue {

    private final int size;
    private final Stack<Integer> queue = new Stack<>();

    TunedBlockingQueue(int size) {
        this.size = size;
    }

    public void add(Integer element) {
        synchronized (queue) {
            while (queue.size() >= size) {
                try {
                    System.out.println("Queue is full -> waiting wait when some element will be poped.");
                    queue.wait();
                } catch (InterruptedException e) {
                    System.out.println("Cannot wait to add the element -> " + e);
                }
            }
            queue.push(element);
            queue.notifyAll();
        }
    }

    public Integer get() {
        Integer result;
        synchronized (queue) {
            while (queue.isEmpty()) {
                try {
                    System.out.println("Queue is empty -> wait for new element");
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
