package com.kyanite.paragon.example;

import com.kyanite.paragon.api.ConfigHolder;
import com.kyanite.paragon.api.ConfigRegistry;
import com.kyanite.paragon.api.enums.ConfigType;

public class TestMod {
    public void init() {
        // Expandable registration system
        ConfigRegistry.register("testmod", new TestModConfig());

        // Lightweight registration system
        ConfigHolder configHolder = new ConfigHolder("testmod", ConfigType.STANDARD,
            TestModConfig.isSus, TestModConfig.name, TestModConfig.sussiness);
    }
}
