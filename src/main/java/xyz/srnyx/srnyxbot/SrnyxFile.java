package xyz.srnyx.srnyxbot;

import org.jetbrains.annotations.NotNull;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;


public class SrnyxFile {
    @NotNull private final String pathString;
    private final boolean isResource;
    @NotNull private final File file;
    @NotNull public final YamlConfigurationLoader loader;
    public ConfigurationNode yaml;

    public SrnyxFile(@NotNull String pathString, @NotNull NodeStyle style, boolean isResource) {
        this.pathString = pathString + ".yml";
        this.isResource = isResource;
        this.file = new File(this.pathString);
        this.loader = YamlConfigurationLoader.builder().nodeStyle(style).path(file.toPath()).build();
        load();
    }

    public void load() {
        if (isResource && !new File(pathString).exists()) create();
        try {
            this.yaml = loader.load();
        } catch (final IOException e) {
            SrnyxBot.LOGGER.warn(String.format("Failed to load file with path: %s", pathString));
            e.printStackTrace();
        }
    }

    public void create() {
        if (!isResource) {
            try {
                Files.createDirectories(file.toPath().getParent());
                Files.createFile(file.toPath());
            } catch (final IOException e) {
                SrnyxBot.LOGGER.warn(String.format("Failed to create file with path: %s", pathString));
                e.printStackTrace();
            }
            return;
        }

        try (final InputStream inputStream = SrnyxBot.class.getClassLoader().getResourceAsStream(pathString);
             final FileOutputStream outputStream = new FileOutputStream(pathString)) {
            if (inputStream == null) throw new IOException("Resource not found in JAR file.");
            final byte [] buffer = new byte[inputStream.available()];
            int count;
            while ((count = inputStream.read(buffer)) > 0) outputStream.write(buffer, 0, count);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        // Cancel if file is empty
        if (yaml.empty()) {
            // Delete file if it isn't a resource and exists
            if (!isResource && file.exists()) try {
                Files.delete(file.toPath());
            } catch (final IOException e) {
                SrnyxBot.LOGGER.warn(String.format("Failed to delete file with path: %s", pathString));
                e.printStackTrace();
            }
            return;
        }

        // Save file
        try {
            loader.save(yaml);
        } catch (final IOException e) {
            SrnyxBot.LOGGER.warn(String.format("Failed to save file with path: %s", pathString));
            e.printStackTrace();
        }
    }
}
