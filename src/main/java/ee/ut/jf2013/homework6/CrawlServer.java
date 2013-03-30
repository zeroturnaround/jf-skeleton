package ee.ut.jf2013.homework6;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Phaser;
import java.util.regex.Pattern;

import static java.lang.System.nanoTime;

public class CrawlServer {

    public static final Pattern PATTERN = Pattern.compile("http://(\\w+\\.)*(\\w+)");

    private final ConcurrentHashMap<String, Integer> visitedPages = new ConcurrentHashMap<>();
    static Collection<String> skipPages;

    static Phaser phaser = new Phaser(1);

    public static void main(String[] args) throws Exception {
        final InputParameters params = new InputParameters(args);
        new CrawlServer().start(params);
    }

    private void start(InputParameters params) throws InterruptedException {
        long start = nanoTime();

        skipPages = getDisallowedPages(params.getRootUrl());

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

    private static Collection<String> getDisallowedPages(String url) {
        final URLHandler urlHandler = new URLHandler(url + "/robots.txt");
        return new HashSet<String>() {
            {
                for (String skip : urlHandler.readAll().split("\n")) {
                    if (!skip.startsWith("#") && !skip.startsWith("User-agent")) {
                        add(skip.replace("Disallow: ", ""));
                    }
                }
            }
        };
    }
}
