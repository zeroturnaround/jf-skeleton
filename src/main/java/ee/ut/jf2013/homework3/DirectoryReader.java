package ee.ut.jf2013.homework3;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.Files.*;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Collections.unmodifiableMap;

public class DirectoryReader {

    private final static FileSystem fileSystem = FileSystems.getDefault();


    public static void main(String[] args) throws IOException {
        Map<String, String> parameters = readParameters(args);
        Path source = validateSourceDirectory(parameters);
        Path target = checkAndCreateTargetDirectory(parameters);

        try (DirectoryStream<Path> sourceDir = Files.newDirectoryStream(source);
             DirectoryStream<Path> targetDir = Files.newDirectoryStream(target)) {
            for (Path entry : sourceDir) {
                if (!isDirectory(entry)) {
                    Path targetFile = target.resolve(entry.getFileName());
                    if (notExists(targetFile)) {
                        Files.copy(entry, targetFile);
                        System.out.println("Created new file: " + targetFile);
                    } else if (getLastModifiedTime(targetFile).compareTo(getLastModifiedTime(entry)) < 0) {
                        Files.copy(entry, targetFile, REPLACE_EXISTING);
                        System.out.println("Updated file: " + targetFile);
                    }
                }
            }

            deleteNotExistingFiles(source, targetDir);
        }

    }

    private static void deleteNotExistingFiles(Path source, DirectoryStream<Path> targetDir) throws IOException {
        for (Path entry : targetDir) {
            if (notExists(source.resolve(entry.getFileName()))) {
                Files.delete(entry);
                System.out.println("delete file from target which has been removed from source " + entry);
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
