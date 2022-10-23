package com.kyanite.paragon.api.interfaces;

import com.kyanite.paragon.api.ConfigOption;
import com.kyanite.paragon.api.enums.ConfigSide;

import java.util.List;

public interface ModConfig {
    public List<ConfigOption> configOptions();
    default public ConfigSide configSide() {
        return ConfigSide.COMMON;
    }
}
