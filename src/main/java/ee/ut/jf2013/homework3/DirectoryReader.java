package ee.ut.jf2013.homework3;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.notExists;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.util.Collections.unmodifiableMap;

public class DirectoryReader {

    private final static FileSystem fileSystem = FileSystems.getDefault();


    public static void main(String[] args) throws IOException {
        Map<String, String> parameters = readParameters(args);
        Path source = validateSourceDirectory(parameters);
        Path target = checkAndCreateTargetDirectory(parameters);

        try (DirectoryStream<Path> paths = Files.newDirectoryStream(source)) {
            for (Path entry : paths) {
                if (!isDirectory(entry)) {
                    Files.copy(entry, target.resolve(entry.getFileName()));
                }
            }
        }

    }

    private static Path checkAndCreateTargetDirectory(Map<String, String> parameters) throws IOException {
        Path target = fileSystem.getPath(parameters.get("target"));
        if (notExists(target, NOFOLLOW_LINKS)) {
            System.out.println("New target directory was successfully created: " + Files.createDirectories(target));
        }
        return target;
    }

    private static Path validateSourceDirectory(Map<String, String> parameters) throws IOException {
        Path source = fileSystem.getPath(parameters.get("source"));
        if (notExists(source, NOFOLLOW_LINKS)) {
            throw new IOException("Source directory doesn't exist!");
        }
        return source;
    }


    private static Map<String, String> readParameters(final String[] parameters) {
        if (parameters.length < 2) {
            throw new RuntimeException("source or target directories weren't defined in program arguments.");
        }
        return unmodifiableMap(new HashMap<String, String>(2) {
            {
                put("source", parameters[0]);
                put("target", parameters[1]);
            }
        });
    }

}
