package ee.ut.jf2013.homework6;


import java.util.concurrent.ConcurrentHashMap;

public class WebCrawler {

    public static void main(String[] args) {


        if (args.length == 0) {
            System.out.println("root URL must be defined");
            return;
        }
        String url = args[0];
        int maxUniquePages = 100;
        if (args.length > 1) {
            maxUniquePages = Integer.parseInt(args[1]);
        }

        ConcurrentHashMap<String, String> map = new ConcurrentHashMap();

    }

}
