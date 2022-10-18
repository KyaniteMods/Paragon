package com.kyanite.paragon.example;

import com.kyanite.paragon.api.ConfigHolder;
import com.kyanite.paragon.api.ConfigRegistry;
import com.kyanite.paragon.api.enums.ConfigType;

public class TestMod {
    public void init() {
        ConfigRegistry.register("testmod", new TestModConfig());

        ConfigHolder configHolder = new ConfigHolder("testmod", ConfigType.STANDARD,
            TestModConfig.isSus, TestModConfig.name, TestModConfig.sussiness);
    }
}
