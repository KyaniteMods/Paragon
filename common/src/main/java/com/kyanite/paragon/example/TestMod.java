package com.kyanite.paragon.example;

import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.api.ConfigRegistry;

public class TestMod {
    public void init() {
        // Register config
        ConfigRegistry.register(new TestModConfig());

        // Getting values
        Paragon.LOGGER.info(TestModConfig.DISCORD_USERNAME.get());
    }
}
