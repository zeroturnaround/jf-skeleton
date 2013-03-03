package ee.ut.jf2013.homework4;

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
