package com.kyanite.paragon.example;

import com.kyanite.paragon.api.ConfigOption;
import com.kyanite.paragon.api.enums.ConfigHandshakeResult;
import com.kyanite.paragon.api.interfaces.configtypes.YAMLModConfig;

public class TestModConfig implements YAMLModConfig {
    public static final ConfigOption<String[]> name = new ConfigOption<>("name", new String[] { "ace", "balls" });
    public static final ConfigOption<Boolean> isSus = new ConfigOption<>("sus", true);
    public static final ConfigOption<ConfigHandshakeResult> sussiness = new ConfigOption<>("sussiness", ConfigHandshakeResult.ERROR);

    @Override
    public String getModId() {
        return "paragon";
    }
}
