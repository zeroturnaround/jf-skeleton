package ee.ut.jf2013.cache;


public interface Computable<A, V> {
    V compute(A arg) throws InterruptedException;
}
