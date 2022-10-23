package com.kyanite.paragon.example;

import com.kyanite.paragon.api.ConfigRegistry;

public class TestMod {
    public void init() {
        // Class-based config system
        ConfigRegistry.register("paragon", new TestModConfig());

        // Lightweight config system
        //ConfigHolder configHolder = new ConfigHolder("testmod",
        //    TestModConfig.isSus, TestModConfig.name, TestModConfig.sussiness);

    }
}
