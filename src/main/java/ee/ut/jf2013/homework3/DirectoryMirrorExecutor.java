package ee.ut.jf2013.homework3;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.Files.*;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.util.Collections.unmodifiableMap;

public class DirectoryMirrorExecutor {

    private final static FileSystem fileSystem = FileSystems.getDefault();


    public static void main(String[] args) throws IOException, InterruptedException {
        new DirectoryMirrorExecutor().execute(readParameters(args));
    }

    public void execute(Map<String, String> parameters) throws IOException, InterruptedException {
        Path source = validateSourceDirectory(parameters);
        Path target = checkAndCreateTargetDirectory(parameters);

        try (DirectoryStream<Path> sourceDir = Files.newDirectoryStream(source);
             DirectoryStream<Path> targetDir = Files.newDirectoryStream(target)) {
            for (Path entry : sourceDir) {
                if (!isDirectory(entry)) {
                    Path targetFile = target.resolve(entry.getFileName());
                    if (notExists(targetFile)) {
                        createFile(entry, target);
                    } else if (getLastModifiedTime(targetFile).compareTo(getLastModifiedTime(entry)) < 0) {
                        updateFile(entry, target);
                    }
                }
            }

            deleteNotExistingFiles(source, targetDir);
        }

        startWatchService(source, target);
    }

    private void startWatchService(Path source, Path target) throws IOException, InterruptedException {
        WatchService service = fileSystem.newWatchService();
        source.register(service, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

        WatchKey key;
        while ((key = service.take()).isValid()) {
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<Path> kind = (WatchEvent.Kind<Path>) event.kind();
                Path entry = source.resolve((Path) event.context());
                if (ENTRY_CREATE == kind) {
                    createFile(entry, target);
                } else if (ENTRY_MODIFY == kind) {
                    updateFile(entry, target);
                } else if (ENTRY_DELETE == kind) {
                    deleteFile(entry, target);
                }
            }
            key.reset();
        }
    }

    private void deleteFile(Path entry, Path... target) throws IOException {
        Path path = target.length == 0 ? entry : target[0].resolve(entry.getFileName());
        Files.delete(path);
        System.out.println("Deleted file from target which has been removed from source " + path);
    }

    private void updateFile(Path entry, Path target) throws IOException {
        Path update = Files.copy(entry, target.resolve(entry.getFileName()), REPLACE_EXISTING);
        System.out.println("Updated file: " + update);
    }

    private void createFile(Path entry, Path target) throws IOException {
        Path copy = Files.copy(entry, target.resolve(entry.getFileName()));
        System.out.println("Created new file: " + copy);
    }

    private void deleteNotExistingFiles(Path source, DirectoryStream<Path> targetDir) throws IOException {
        for (Path entry : targetDir) {
            if (notExists(source.resolve(entry.getFileName()))) {
                deleteFile(entry);
            }
        }
    }

    private Path checkAndCreateTargetDirectory(Map<String, String> parameters) throws IOException {
        Path target = fileSystem.getPath(parameters.get("target"));
        if (notExists(target, NOFOLLOW_LINKS)) {
            System.out.println("New target directory was successfully created: " + Files.createDirectories(target));
        }
        return target;
    }

    private Path validateSourceDirectory(Map<String, String> parameters) throws IOException {
        Path source = fileSystem.getPath(parameters.get("source"));
        if (notExists(source, NOFOLLOW_LINKS)) {
            throw new IOException("Source directory doesn't exist!");
        }
        return source;
    }

    public static Map<String, String> readParameters(final String[] parameters) {
        if (parameters.length < 2) {
            throw new RuntimeException("Source or target directories weren't defined in program arguments.");
        }
        return unmodifiableMap(new HashMap<String, String>(2) {
            {
                put("source", parameters[0]);
                put("target", parameters[1]);
            }
        });
    }
}
