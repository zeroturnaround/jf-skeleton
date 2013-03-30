package ee.ut.jf2013.homework6;


import java.util.concurrent.ConcurrentHashMap;

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

        try {
            for (String link : in.getAllLinks()) {
                if (sizeIsExceeded()) {
                    return;
                }
                if (CrawlServer.skipPages.contains(link)) {
                    continue;
                }
                if (!visitedPages.containsKey(link)) {
                    phaser.register();
                    new Thread(new WebCrawler(link.replaceAll("#(.*?)$", ""), maxOfUniqueCrawlPages, visitedPages)).start();
                }
            }
        } finally {
            in.close();
            phaser.arriveAndDeregister();
        }
    }

    private boolean sizeIsExceeded() {
        return visitedPages.size() >= maxOfUniqueCrawlPages;
    }
}
