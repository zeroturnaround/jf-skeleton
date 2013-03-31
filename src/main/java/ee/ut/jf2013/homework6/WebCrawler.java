package ee.ut.jf2013.homework6;


import java.util.concurrent.ConcurrentHashMap;

import static ee.ut.jf2013.homework6.CrawlServer.logger;
import static ee.ut.jf2013.homework6.CrawlServer.phaser;

public class WebCrawler extends Thread {

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
        logger.log(getName() + " - operates with " + pageUrl);
        URLHandler in = new URLHandler(pageUrl);
        if (sizeIsExceeded() || !in.exists()) {
            logger.log(getName() + " - explored links size is exceeded or link doesn't exist: " + pageUrl);
            phaser.arriveAndDeregister();
            return;
        }
        if (visitedPages.containsKey(pageUrl)) {
            logger.log(getName() + " - the link is already explored: " + pageUrl);
            visitedPages.put(pageUrl, visitedPages.get(pageUrl) + 1);
        } else {
            logger.log(getName() + " - the link is added to list: " + pageUrl);
            visitedPages.put(pageUrl, 1);
        }

        if (sizeIsExceeded()) {
            logger.log(getName() + " - explored links size is exceeded");
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
            logger.log(getName() + " - is finished it's job.");
            phaser.arriveAndDeregister();
        }
    }

    private boolean sizeIsExceeded() {
        return visitedPages.size() >= maxOfUniqueCrawlPages;
    }
}
