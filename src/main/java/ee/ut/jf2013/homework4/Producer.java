package ee.ut.jf2013.homework4;

import java.util.concurrent.ThreadLocalRandom;

class Producer extends User {
    Producer(TunedBlockingQueue queue) {
        super(queue);
    }

    @Override
    protected void doAction() throws InterruptedException {
        int element = ThreadLocalRandom.current().nextInt();
        queue.add(element);
        System.out.println(Thread.currentThread().getName() + " added " + element);
    }
}
