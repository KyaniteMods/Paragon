package com.kyanite.paragon.example;

import com.kyanite.paragon.api.ConfigOption;
import com.kyanite.paragon.api.interfaces.configtypes.JSONModConfig;

public class TestModConfig implements JSONModConfig {
    public static final ConfigOption<String[]> NAME = new ConfigOption<>("name", new String[] { "ace", "balls" });
    public static final ConfigOption<Boolean> SUS = new ConfigOption<>("sus", true);
    public static final ConfigOption<Float> IMPOSTOR_CHANCE = new ConfigOption<>("impostor_chance", 100f);

    @Override
    public String getModId() {
        return "paragon";
    }
}
