package ee.ut.jf2013.homework3;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.Files.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardWatchEventKinds.*;

public class DirectoryMirrorExecutor {

    private final static FileSystem fileSystem = FileSystems.getDefault();


    public static void main(String[] args) throws IOException, InterruptedException {
        new DirectoryMirrorExecutor().execute(readParameters(args));
    }

    public void execute(Parameters parameters) throws IOException, InterruptedException {
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
        System.out.println("Source directory was removed.");
    }

    private void deleteFile(Path entry, Path target) throws IOException {
        Path path = target == null ? entry : target.resolve(entry.getFileName());
        Files.delete(path);
        System.out.println("Deleted file from target which has been removed from source " + path);
    }

    private void updateFile(Path entry, Path target) throws IOException {
        Path update = Files.copy(entry, target.resolve(entry.getFileName()), REPLACE_EXISTING);
        System.out.println("Updated file: " + update);
    }

    private void createFile(Path entry, Path target) throws IOException {
        Path path = target.resolve(entry);
        if (Files.exists(path)) {
            updateFile(entry, target);
        } else {
            Path copy = Files.copy(entry, path);
            System.out.println("Created new file: " + copy);
        }
    }

    private void deleteNotExistingFiles(Path source, DirectoryStream<Path> targetDir) throws IOException {
        for (Path entry : targetDir) {
            if (notExists(source.resolve(entry.getFileName()))) {
                deleteFile(entry, entry.getRoot());
            }
        }
    }

    private Path checkAndCreateTargetDirectory(Parameters parameters) throws IOException {
        Path target = fileSystem.getPath(parameters.getTarget());
        if (notExists(target)) {
            System.out.println("New target directory was successfully created: " + Files.createDirectories(target));
        }
        return target;
    }

    private Path validateSourceDirectory(Parameters parameters) throws IOException {
        Path source = fileSystem.getPath(parameters.getSource());
        if (notExists(source)) {
            throw new IOException("Source directory doesn't exist!");
        }
        return source;
    }

    public static Parameters readParameters(final String[] parameters) {
        if (parameters.length < 2) {
            throw new RuntimeException("Source or target directories weren't defined in program arguments.");
        }
        return new Parameters(parameters[0], parameters[1]);
    }

    static class Parameters {
        private String source;
        private String target;

        Parameters(String source, String target) {
            this.source = source;
            this.target = target;
        }

        public String getSource() {
            return source;
        }

        public String getTarget() {
            return target;
        }
    }
}
