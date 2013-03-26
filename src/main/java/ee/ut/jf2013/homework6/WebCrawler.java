package ee.ut.jf2013.homework6;


import java.util.Queue;
import java.util.concurrent.Phaser;
import java.util.regex.Matcher;

import static ee.ut.jf2013.homework6.Launcher.PATTERN;

public class WebCrawler implements Runnable {

    private String pageUrl;
    private int maxOfUniqueCrawlPages;
    private Queue<String> visitedPages;
    private Phaser phaser;

    public WebCrawler(String pageUrl, int maxOfUniqueCrawlPages, Queue<String> visitedPages, Phaser phaser) {
        this.pageUrl = pageUrl;
        this.maxOfUniqueCrawlPages = maxOfUniqueCrawlPages;
        this.visitedPages = visitedPages;
        this.phaser = phaser;
        phaser.register();
    }

    @Override
    public void run() {
        phaser.arrive();
        URLHandler in = new URLHandler(pageUrl);
        if (visitedPages.size() >= maxOfUniqueCrawlPages || !in.exists()) {
            phaser.arriveAndDeregister();
            return;
        }

        visitedPages.offer(pageUrl);
        String input = in.readAll();
        Matcher matcher = PATTERN.matcher(input);
        while (matcher.find() && visitedPages.size() < maxOfUniqueCrawlPages) {
            String w = matcher.group();
            if (!visitedPages.contains(w)) {
                new Thread(new WebCrawler(pageUrl, maxOfUniqueCrawlPages, visitedPages, phaser)).start();
            }
        }
        phaser.arriveAndDeregister();
    }
}
