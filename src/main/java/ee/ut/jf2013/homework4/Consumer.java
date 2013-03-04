package ee.ut.jf2013.homework4;

class Consumer extends User {
    Consumer(TunedBlockingQueue queue) {
        super(queue);
    }

    @Override
    protected void doAction() throws InterruptedException {
        Integer integer = queue.get();
        System.out.println(Thread.currentThread().getName() + " got " + integer);
    }
}
