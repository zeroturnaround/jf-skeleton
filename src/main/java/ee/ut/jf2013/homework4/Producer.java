package ee.ut.jf2013.homework4;

import java.util.concurrent.ThreadLocalRandom;

class Producer extends User {
    Producer(TunedBlockingQueue queue) {
        super(queue);
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            int element = ThreadLocalRandom.current().nextInt();
            try {
                queue.add(element);
                System.out.println(Thread.currentThread().getName() + " added " + element);
            } catch (InterruptedException e) {
                System.out.println(getName() + " cannot wait to add the element -> " + e);
                return;
            }
        }
        System.out.println(Thread.currentThread().getName() + " finished it's execution");
    }
}
