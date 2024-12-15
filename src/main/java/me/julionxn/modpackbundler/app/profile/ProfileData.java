package me.julionxn.modpackbundler.app.profile;

import me.julionxn.modpackbundler.models.LoaderType;

import java.util.List;

public record ProfileData(String name, String version,
                          LoaderType loaderType, String loaderVersion, String profileImage,
                          String description, List<String> uuids) {
}
