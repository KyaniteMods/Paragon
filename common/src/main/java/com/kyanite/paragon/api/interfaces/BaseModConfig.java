package com.kyanite.paragon.api.interfaces;

import com.kyanite.paragon.api.ConfigGroup;
import com.kyanite.paragon.api.ConfigOption;
import com.kyanite.paragon.api.enums.ConfigSide;
import org.apache.commons.io.FileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static com.kyanite.paragon.api.ConfigUtils.getFilePath;

public interface BaseModConfig {
    /**
     * Since 2.0.0, this method no longer requires an override. It automatically finds the config-options in your class.
     */
    default List<ConfigOption> configOptions() {
        List<ConfigOption> configOptions = new ArrayList<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                if(ConfigOption.class.isAssignableFrom(field.getType())) {
                    try {
                        configOptions.add((ConfigOption) field.get(null));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return configOptions;
    }

    default List<ConfigGroup> configGroups() {
        List<ConfigGroup> configGroups = new ArrayList<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                if(ConfigGroup.class.isAssignableFrom(field.getType())) {
                    try {
                        configGroups.add((ConfigGroup) field.get(null));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return configGroups;
    }

    default public ConfigSide configSide() {
        return ConfigSide.COMMON;
    }
    public default String getRaw() throws IOException {
        return FileUtils.readFileToString(getFilePath(this.getModId(), this.configSide(), this.getSuffix()));
    }
    public String getModId();
    public void init();
    public String getSuffix();
    public void load() throws FileNotFoundException;
    public void save() throws IOException;
}
