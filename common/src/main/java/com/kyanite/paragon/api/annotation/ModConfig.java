package com.kyanite.paragon.api.annotation;

import com.kyanite.paragon.api.enums.ConfigType;
import com.kyanite.paragon.api.ConfigOption;

import java.util.List;

public interface ModConfig {
    public List<ConfigOption> configOptions();
    default public ConfigType configType() {
        return ConfigType.STANDARD;
    }
}
