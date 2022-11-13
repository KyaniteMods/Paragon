package com.kyanite.paragon.example;

import com.kyanite.paragon.api.ConfigOption;
import com.kyanite.paragon.api.enums.ConfigHandshakeResult;
import com.kyanite.paragon.api.interfaces.ModConfig;

import java.util.List;

public class TestModConfig implements ModConfig {
    public static final ConfigOption<String[]> name = new ConfigOption<>("name", new String[] { "ace", "balls" });
    public static final ConfigOption<Boolean> isSus = new ConfigOption<>("sus", true);
    public static final ConfigOption<ConfigHandshakeResult> sussiness = new ConfigOption<>("sussiness", ConfigHandshakeResult.ERROR);

    @Override
    public List<ConfigOption> configOptions() {
        return List.of(name, isSus, sussiness);
    }

    @Override
    public String getModId() {
        return "paragon";
    }
}
