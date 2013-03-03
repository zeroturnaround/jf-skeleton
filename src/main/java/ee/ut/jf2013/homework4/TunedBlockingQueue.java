package ee.ut.jf2013.homework4;

import java.util.Stack;

class TunedBlockingQueue {

    private final int size;
    private final Stack<Integer> queue = new Stack<>();

    TunedBlockingQueue(int size) {
        this.size = size;
    }

    public void add(Integer element) {
        synchronized (queue) {
            while (queue.size() >= size) {
                try {
                    System.out.println("Queue is full -> waiting wait when some element will be poped.");
                    queue.wait();
                } catch (InterruptedException e) {
                    System.out.println("Cannot wait to add the element -> " + e);
                }
            }
            queue.push(element);
            queue.notifyAll();
        }
    }

    public Integer get() {
        Integer result;
        synchronized (queue) {
            while (queue.isEmpty()) {
                try {
                    System.out.println("Queue is empty -> wait for new element");
                    queue.wait();
                } catch (InterruptedException e) {
                    System.out.println("Cannot wait to get the element -> " + e);
                }
            }
            result = queue.pop();
            queue.notifyAll();
        }
        return result;
    }
}
