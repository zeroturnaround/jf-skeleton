package ee.ut.jf2013.homework6;


import java.util.concurrent.ConcurrentHashMap;

import static ee.ut.jf2013.homework6.CrawlServer.*;

public class WebCrawler extends Thread {

    private String pageUrl;
    private URLHandler handler;
    private int maxOfUniqueCrawlPages;
    private ConcurrentHashMap<String, Integer> visitedPages;

    public WebCrawler(String pageUrl, int maxOfUniqueCrawlPages, ConcurrentHashMap<String, Integer> visitedPages) {
        this.pageUrl = pageUrl;
        this.maxOfUniqueCrawlPages = maxOfUniqueCrawlPages;
        this.visitedPages = visitedPages;
        this.handler = new URLHandler(pageUrl);
    }

    @Override
    public void run() {
        logger.log(getName() + " - operates with " + pageUrl);
        if (sizeIsExceeded() || !handler.exists()) {
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
            logger.log(getName() + " - explored links size is exceeded -> force Termination");
            phaser.forceTermination();
            return;
        }

        try {
            for (String link : handler.getAllLinks()) {
                if (sizeIsExceeded()) {
                    return;
                }
                if (CrawlServer.skipPages.contains(link)) {
                    continue;
                }
                if (!visitedPages.containsKey(link) && !sizeIsExceeded()) {
                    phaser.register();
                    new Thread(new WebCrawler(link.replaceAll("#(.*?)$", ""), maxOfUniqueCrawlPages, visitedPages)).start();
                }
            }
        } finally {
            handler.close();
            logger.log(getName() + " - is finished it's job.");
            phaser.arriveAndDeregister();
        }
    }

    private boolean sizeIsExceeded() {
        return visitedPages.size() >= maxOfUniqueCrawlPages;
    }
}
