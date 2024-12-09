package me.julionxn.modpackbundler.models;

import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Profile {

    public String name;
    public Path path;
    private File manifestFile;
    private String version;
    private LoaderInfo loaderInfo;
    private final List<UUID> validUUIDs = new ArrayList<>();

    public Profile(String name, Path path) {
        this.name = name;
        this.path = path;
    }

    public void load(){
        Path manifestPath = path.resolve("manifest.json");
        manifestFile = manifestPath.toFile();
        if(!manifestFile.exists()){
            saveManifest("", new LoaderInfo(LoaderType.VANILLA, ""), new ArrayList<>());
        } else {
            parseManifest();
        }
    }

    private void parseManifest(){
        JsonObject manifest;
        try (BufferedReader br = new BufferedReader(new FileReader(manifestFile))) {
             manifest = new GsonBuilder().create().fromJson(br, JsonObject.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.version = manifest.get("version").getAsString();
        JsonObject loaderData = manifest.get("loader").getAsJsonObject();
        LoaderType loader = LoaderType.valueOf(loaderData.get("type").getAsString().toUpperCase());
        String version = loaderData.get("version").getAsString();
        this.loaderInfo = new LoaderInfo(loader, version);
        JsonArray validUUIDs = manifest.get("validUUIDs").getAsJsonArray();
        for (JsonElement validUUID : validUUIDs) {
            String uuidStr = validUUID.getAsString();
            UUID uuid = UUID.fromString(uuidStr);
            this.validUUIDs.add(uuid);
        }
    }

    public void saveManifest(){
        this.saveManifest(version, loaderInfo, validUUIDs);
    }

    public void saveManifest(String version, LoaderInfo loaderInfo, List<UUID> validUUIDs){
        JsonObject manifest = new JsonObject();
        //Version
        manifest.addProperty("version", version);
        //Loader
        JsonObject loader = new JsonObject();
            //Type
        loader.addProperty("type", loaderInfo.loaderType().toString().toLowerCase());
            //Version
        loader.addProperty("version", loaderInfo.version());
        manifest.add("loader", loader);
        //Valid UUIDs
        JsonArray validUUIDsArray = new JsonArray();
        for (UUID validUUID : validUUIDs) {
            validUUIDsArray.add(validUUID.toString());
        }
        manifest.add("validUUIDs", validUUIDsArray);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(manifest);
        try (FileWriter writer = new FileWriter(manifestFile)) {
            writer.write(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean rename(String newName){
        Path newPath = path.getParent().resolve(newName);
        boolean success = path.toFile().renameTo(newPath.toFile());
        if (success){
            this.name = newName;
            this.path = newPath;
            this.manifestFile = newPath.resolve("manifest.json").toFile();
        }
        return success;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LoaderInfo getLoaderInfo() {
        return loaderInfo;
    }

    public void setLoaderInfo(LoaderInfo loaderInfo) {
        this.loaderInfo = loaderInfo;
    }

    public List<UUID> getValidUUIDs() {
        return validUUIDs;
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