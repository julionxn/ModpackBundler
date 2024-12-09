package me.julionxn.modpackbundler.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class FileUtils {

    public static Optional<List<Path>> getDirectories(Path path) {
        List<Path> paths;
        try (Stream<Path> stream = Files.list(path)) {
            paths = stream.filter(Files::isDirectory).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (paths.isEmpty()) return Optional.empty();
        return Optional.of(paths);
    }

}
