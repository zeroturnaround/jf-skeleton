package ee.ut.jf2013.homework4;

/**
 * Created with IntelliJ IDEA.
 * User: jaros
 * Date: 3/3/13
 * Time: 6:23 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class User extends Thread {
    final TunedBlockingQueue queue;
    User(TunedBlockingQueue queue) {
        this.queue = queue;
        setName(getClass().getSimpleName() + "_" + getName());
    }
}
