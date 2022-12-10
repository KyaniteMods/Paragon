package com.kyanite.paragon.api;

import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.api.interfaces.Config;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ConfigHolder {
    private final String modId;
    private final Config config;
    private final String fileName;
    private List<ConfigOption> configOptions;
    private List<ConfigGroup> configGroups;

    public ConfigHolder(String modId, Config config, String fileName) {
        this.modId = modId;
        this.config = config;
        if(fileName == null) { this.fileName = modId; return; }
        this.fileName = fileName.isEmpty() || fileName.isBlank() || fileName == null ?
                modId : fileName.toLowerCase();
    }

    public void init() {
        this.detectConfigGroups();
        this.detectConfigOptions();

        try {
            if(this.getFile().exists()) this.config.getSerializer().load(); else this.config.getSerializer().save();
        }catch (Exception e) {
            Paragon.LOGGER.error("Unable to initialize config for " + modId + " due to " + e.getMessage());
        }
    }

    private void detectConfigOptions() {
        List<ConfigOption> configOptions = new ArrayList<>();
        for (Field field : this.config.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                if(ConfigOption.class.isAssignableFrom(field.getType())) {
                    try {
                        ConfigOption configOption = (ConfigOption) field.get(null);
                        if(field.isAnnotationPresent(blue.endless.jankson.Comment.class)) configOption.setDescription(field.getAnnotation(blue.endless.jankson.Comment.class).value());
                        configOptions.add(configOption);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        this.configOptions = configOptions;
    }

    private void detectConfigGroups() {
        List<ConfigGroup> configGroups = new ArrayList<>();
        for (Field field : this.config.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                if(ConfigGroup.class.isAssignableFrom(field.getType())) {
                    try {
                        ConfigGroup configGroup = (ConfigGroup) field.get(null);
                        if(field.isAnnotationPresent(blue.endless.jankson.Comment.class)) configGroup.setDescription(field.getAnnotation(blue.endless.jankson.Comment.class).value());
                        configGroups.add(configGroup);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        this.configGroups = configGroups;
    }

    public String getModId() {return this.modId;}
    public String getFileName() {return this.fileName;}
    public Config getConfig() {return this.config;}

    public List<ConfigOption> getConfigOptions() {return this.configOptions;}
    public List<ConfigGroup> getConfigGroups() {return this.configGroups;}

    public final String getRaw() throws IOException { return FileUtils.readFileToString(ConfigManager.getFilePath(this.getFileName(), this.config.configSide(), this.config.getSerializer().getSuffix()));}
    public File getFile() {return ConfigManager.getFilePath(this.getFileName(), this.config.configSide(), this.config.getSerializer().getSuffix());}
}
