package com.kyanite.paragon.example;

import com.kyanite.paragon.api.ConfigGroup;
import com.kyanite.paragon.api.ConfigOption;
import com.kyanite.paragon.api.interfaces.configtypes.JSONModConfig;

public class TestModConfig implements JSONModConfig {
    public static final ConfigOption<String> DISCORD_USERNAME = new ConfigOption<>("username", new String("acee#1220"));
    public static final ConfigOption<String[]> ROLES = new ConfigOption<>("roles", new String[] {
            "Modder",
            "Game Dev",
            "Co-owner of Kyanite Mods"
    });

    public static final ConfigGroup ME = new ConfigGroup("angxd",
            DISCORD_USERNAME, ROLES);

    @Override
    public String getModId() {
        return "paragon";
    }
}
