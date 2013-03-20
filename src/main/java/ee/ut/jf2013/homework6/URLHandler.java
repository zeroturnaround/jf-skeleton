package ee.ut.jf2013.homework6;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

public class URLHandler {

    private Scanner scanner;
    private static final String charsetName = "UTF-8";
    private static final Locale usLocale = new Locale("en", "US");
    private static final Pattern EVERYTHING_PATTERN = Pattern.compile("\\A");

    public URLHandler(String pageUrl) {
        try {
            URL url = new URL(pageUrl);
            URLConnection site = url.openConnection();
            InputStream is = site.getInputStream();
            scanner = new Scanner(new BufferedInputStream(is), charsetName);
            scanner.useLocale(usLocale);
        } catch (IOException e) {
            System.err.println("Could not open " + pageUrl);
        }
    }

    public boolean exists() {
        return scanner != null;
    }

    public String readAll() {
        return !scanner.hasNextLine() ? "" : scanner.useDelimiter(EVERYTHING_PATTERN).next();
    }
}
