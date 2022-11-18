package com.kyanite.paragon.example;

import com.kyanite.paragon.api.ConfigOption;
import com.kyanite.paragon.api.interfaces.Config;

public class TestModConfig implements Config {
    public static final ConfigOption<String> DISCORD_USERNAME = new ConfigOption<>("username", new String("acee#1220"));
}
