package com.kyanite.paragon.example;

import com.kyanite.paragon.api.ConfigRegistry;

public class TestMod {
    public void init() {
        // Register config
        ConfigRegistry.register(new TestModConfig());
    }
}
