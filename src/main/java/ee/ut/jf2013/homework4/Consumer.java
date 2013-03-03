package ee.ut.jf2013.homework4;

/**
 * Created with IntelliJ IDEA.
 * User: jaros
 * Date: 3/3/13
 * Time: 6:23 PM
 * To change this template use File | Settings | File Templates.
 */
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
