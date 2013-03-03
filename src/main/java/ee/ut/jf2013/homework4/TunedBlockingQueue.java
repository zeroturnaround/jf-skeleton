package ee.ut.jf2013.homework4;

import java.util.Stack;

class TunedBlockingQueue {

    private final int size;
    private final Stack<Integer> queue = new Stack<>();

    TunedBlockingQueue(int size) {
        this.size = size;
    }

    public void add(Integer element) throws InterruptedException {
        synchronized (queue) {
            while (queue.size() >= size) {
                System.out.println("Queue is full -> waiting wait when some element will be poped.");
                queue.wait();
            }
            queue.push(element);
            queue.notifyAll();
        }
    }

    public Integer get() throws InterruptedException {
        Integer result;
        synchronized (queue) {
            while (queue.isEmpty()) {
                System.out.println("Queue is empty -> wait for new element");
                queue.wait();
            }
            result = queue.pop();
            queue.notifyAll();
        }
        return result;
    }
}
