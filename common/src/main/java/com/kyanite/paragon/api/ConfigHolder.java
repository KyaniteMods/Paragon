package com.kyanite.paragon.api;

import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.api.interfaces.Config;
import com.kyanite.paragon.api.interfaces.Description;
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
    private List<ConfigOption> configOptions;

    public ConfigHolder(String modId, Config config) {
        this.modId = modId;
        this.config = config;
    }

    public void init() {
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
                        if(field.isAnnotationPresent(Description.class)) configOption.setDescription(field.getAnnotation(Description.class).value());
                        configOptions.add(configOption);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        this.configOptions = configOptions;
    }
    public String getModId() {return this.modId;}

    public Config getConfig() {return this.config;}

    public List<ConfigOption> getConfigOptions() {return this.configOptions;}

    public final String getRaw() throws IOException { return FileUtils.readFileToString(ConfigManager.getFilePath(this.getModId(), this.config.configSide(), this.config.getSerializer().getSuffix()));}
    public File getFile() {return ConfigManager.getFilePath(this.modId, this.config.configSide(), this.config.getSerializer().getSuffix());}
}
