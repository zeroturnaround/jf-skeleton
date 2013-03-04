package ee.ut.jf2013.homework4;

import static java.util.concurrent.ThreadLocalRandom.current;

class Producer extends User {
    Producer(TunedBlockingQueue queue) {
        super(queue);
    }

    @Override
    protected void doAction() throws InterruptedException {
        int element = current().nextInt();
        queue.add(element);
        System.out.println(getName() + " added " + element);
    }
}
