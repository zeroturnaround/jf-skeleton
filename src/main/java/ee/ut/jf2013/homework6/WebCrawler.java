package ee.ut.jf2013.homework6;


import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;

import static ee.ut.jf2013.homework6.CrawlServer.PATTERN;
import static ee.ut.jf2013.homework6.CrawlServer.phaser;

public class WebCrawler implements Runnable {

    private String pageUrl;
    private int maxOfUniqueCrawlPages;
    private ConcurrentHashMap<String, Integer> visitedPages;

    public WebCrawler(String pageUrl, int maxOfUniqueCrawlPages, ConcurrentHashMap<String, Integer> visitedPages) {
        this.pageUrl = pageUrl;
        this.maxOfUniqueCrawlPages = maxOfUniqueCrawlPages;
        this.visitedPages = visitedPages;
    }

    @Override
    public void run() {
        URLHandler in = new URLHandler(pageUrl);
        if (sizeIsExceeded() || !in.exists()) {
            phaser.arriveAndDeregister();
            return;
        }
        if (visitedPages.containsKey(pageUrl)) {
            visitedPages.put(pageUrl, visitedPages.get(pageUrl) + 1);
        } else {
            visitedPages.put(pageUrl, 1);
        }

        if (sizeIsExceeded()) {
            phaser.forceTermination();
            return;
        }

        String input = in.readAll();
        Matcher matcher = PATTERN.matcher(input);
        while (matcher.find() && visitedPages.size() < maxOfUniqueCrawlPages) {
            String link = matcher.group();
            if (!visitedPages.containsKey(link)) {
                phaser.register();
                new Thread(new WebCrawler(link, maxOfUniqueCrawlPages, visitedPages)).start();
            }
        }
        phaser.arriveAndDeregister();
    }

    private boolean sizeIsExceeded() {
        return visitedPages.size() >= maxOfUniqueCrawlPages;
    }
}
