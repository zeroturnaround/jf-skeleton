package ee.ut.jf2013.homework4;

class Consumer extends User {
    Consumer(TunedBlockingQueue queue) {
        super(queue);
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Integer integer = queue.get();
                System.out.println(Thread.currentThread().getName() + " got " + integer);
            } catch (InterruptedException e) {
                System.out.println(getName() + " cannot wait to get the element -> " + e);
                return;
            }
        }
        System.out.println(Thread.currentThread().getName() + " finished it's execution");
    }
}
