package ee.ut.jf2013.homework4;

abstract class User extends Thread {
    final TunedBlockingQueue queue;

    User(TunedBlockingQueue queue) {
        this.queue = queue;
        setName(getClass().getSimpleName() + "_" + getName());
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                doAction();
            } catch (InterruptedException e) {
                System.out.println(getName() + " is interrupted");
                return;
            }
        }
        System.out.println(getName() + " finished it's execution");
    }

    protected abstract void doAction() throws InterruptedException;
}
