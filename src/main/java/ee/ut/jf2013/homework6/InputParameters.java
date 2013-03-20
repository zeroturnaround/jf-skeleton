package ee.ut.jf2013.homework6;


public class InputParameters {
    private String rootUrl;
    private int maxOfUniqueCrawlPages = 100;

    public InputParameters(String... args) {
        if (args.length == 0) {
            throw new RuntimeException("root URL must be defined");
        }
        rootUrl = args[0];
        if (args.length > 1) {
            maxOfUniqueCrawlPages = Integer.parseInt(args[1]);
        }
    }

    public String getRootUrl() {
        return rootUrl;
    }

    public int getMaxOfUniqueCrawlPages() {
        return maxOfUniqueCrawlPages;
    }
}
