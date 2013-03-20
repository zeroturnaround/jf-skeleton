package ee.ut.jf2013.homework6;


import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler implements Runnable {

    private final InputParameters params;

    WebCrawler(InputParameters params) {
        this.params = params;
    }

    @Override
    public void run() {
        Queue<String> toBeExamined = new ArrayBlockingQueue<>(params.getMaxOfUniqueCrawlPages());
        toBeExamined.offer(params.getRootUrl());

        Set<String> visitedPages = new HashSet<>();

        while (!toBeExamined.isEmpty() && visitedPages.size() <= params.getMaxOfUniqueCrawlPages()) {
            String pageUrl = toBeExamined.poll();

            URLHandler in = new URLHandler(pageUrl);

            if (!in.exists()) {
                continue;
            }
            String input = in.readAll();

            Matcher matcher = Pattern.compile("http://(\\w+\\.)*(\\w+)").matcher(input);

            // find and print all matches
            while (matcher.find() && visitedPages.size() <= params.getMaxOfUniqueCrawlPages()) {
                String w = matcher.group();
                if (!visitedPages.contains(w)) {
                    toBeExamined.offer(w);
                    visitedPages.add(w);
                }
            }

        }

        System.out.println("CRAWLER FINISHED IT'S JOB");
        System.out.println("VISITED PAGES:");
        for (String page : visitedPages) {
            System.out.println(page);
        }

    }
}
