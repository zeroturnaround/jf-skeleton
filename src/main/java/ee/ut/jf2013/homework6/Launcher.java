package ee.ut.jf2013.homework6;

public class Launcher {

    public static void main(String[] args) throws Exception {
        new Thread(new WebCrawler(new InputParameters(args))).start();
    }
}
