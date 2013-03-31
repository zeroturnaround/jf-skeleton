package ee.ut.jf2013.homework6;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Phaser;
import java.util.regex.Pattern;

import static java.lang.System.nanoTime;

public class CrawlServer {

    public static final Pattern PATTERN = Pattern.compile("http://(\\w+\\.)*(\\w+)");

    private final ConcurrentHashMap<String, Integer> visitedPages = new ConcurrentHashMap<>();
    static Collection<String> skipPages;

    static Phaser phaser = new Phaser(1);
    static Logger logger = new Logger();

    public static void main(String[] args) throws Exception {
        final InputParameters params = new InputParameters(args);
        new CrawlServer().start(params);
    }

    private void start(InputParameters params) throws InterruptedException {
        logger.start();
        long start = nanoTime();
        skipPages = getDisallowedPages(params.getRootUrl());

        phaser.register();
        new Thread(new WebCrawler(params.getRootUrl(), params.getMaxOfUniqueCrawlPages(), visitedPages)).start();
        phaser.arriveAndAwaitAdvance();

        System.out.println("JOB IS DONE: " + ((double) (nanoTime() - start) / 1000_000_000) + " seconds.");
        System.out.println(visitedPages.size() + " pages were examined");

        System.out.println("VISITED PAGES:");
        for (Map.Entry<String, Integer> entry : getSortedLinks()) {
            System.out.println(entry.getValue() + " : " + entry.getKey());
        }
        logger.close();
        System.exit(0);
    }

    private List<Map.Entry<String, Integer>> getSortedLinks() {
        ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<>(visitedPages.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });
        return entries;
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
