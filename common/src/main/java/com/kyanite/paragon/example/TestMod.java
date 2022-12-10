package com.kyanite.paragon.example;

import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.api.ConfigManager;

public class TestMod {
    public void init() {
        // Register config
        ConfigManager.register("paragon", new TestModConfig());

        // Getting values
        Paragon.LOGGER.info(TestModConfig.DISCORD_USERNAME.get());
    }
}
