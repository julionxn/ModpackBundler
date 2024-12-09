package me.julionxn.modpackbundler.models;

import me.julionxn.modpackbundler.util.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Project {

    public String name;
    public Path path;
    private final List<Profile> profiles = new ArrayList<>();

    public Project(String name, Path path) {
        this.name = name;
        this.path = path;
    }

    public void loadProfiles() {
        Optional<List<Path>> profilePathsOpt = FileUtils.getDirectories(path);
        if (profilePathsOpt.isEmpty()) return;
        List<Path> profilePaths = profilePathsOpt.get();
        for (Path profilePath : profilePaths) {
            String profileName = profilePath.getFileName().toString();
            Profile profile = new Profile(profileName, profilePath);
            profile.load();
            profiles.add(profile);
        }
    }

    public Profile addProfile(String profileName){
        Profile profile = new Profile(profileName, path.resolve(profileName));
        addProfile(profile);
        return profile;
    }

    public void addProfile(Profile profile){
        profiles.add(profile);
        File file = profile.path.toFile();
        if (!file.exists() && !file.mkdir()) {
            System.out.printf("[ERROR] Cannot create directory: %s\n", file.getAbsolutePath());
            return;
        }
        profile.load();
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public boolean removeProfile(String profileName){
        return profiles.removeIf(profile -> profile.name.equals(profileName));
    }

    public boolean rename(String newName){
        Path newPath = path.getParent().resolve(newName);
        boolean success = path.toFile().renameTo(newPath.toFile());
        if (success){
            this.name = newName;
            this.path = newPath;
        }
        return success;
    }

    public boolean remove(){
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public @NotNull FileVisitResult visitFile(Path file, @NotNull BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public @NotNull FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
