package ee.ut.jf2013.homework4;

abstract class User extends Thread {
    final TunedBlockingQueue queue;
    User(TunedBlockingQueue queue) {
        this.queue = queue;
        setName(getClass().getSimpleName() + "_" + getName());
    }
}
