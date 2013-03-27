package ee.ut.jf2013.homework6;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Phaser;
import java.util.regex.Pattern;

import static java.lang.System.nanoTime;

public class Launcher {

    public static final Pattern PATTERN = Pattern.compile("http://(\\w+\\.)*(\\w+)");

    private final ConcurrentHashMap<String, Integer> visitedPages = new ConcurrentHashMap<>();

    static Phaser phaser = new Phaser(1);

    public static void main(String[] args) throws Exception {
        final InputParameters params = new InputParameters(args);
        new Launcher().start(params);
    }

    private void start(InputParameters params) throws InterruptedException {
        long start = nanoTime();

        phaser.register();
        new Thread(new WebCrawler(params.getRootUrl(), params.getMaxOfUniqueCrawlPages(), visitedPages)).start();
        phaser.arriveAndAwaitAdvance();

        System.out.println("JOB IS DONE: " + ((double) (nanoTime() - start) / 1000_000_000) + " seconds.");
        System.out.println(visitedPages.size() + " pages were examined");

        System.out.println("VISITED PAGES:");
        for (Map.Entry<String, Integer> entry : visitedPages.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
        System.exit(0);
    }
}
