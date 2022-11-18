package com.kyanite.paragon.example;

import com.kyanite.paragon.api.ConfigGroup;
import com.kyanite.paragon.api.ConfigOption;
import com.kyanite.paragon.api.interfaces.Config;
import com.kyanite.paragon.api.interfaces.serializers.ConfigSerializer;
import com.kyanite.paragon.api.interfaces.serializers.YAMLSerializer;

public class TestModConfig implements Config {
    public static final ConfigOption<String> DISCORD_USERNAME = new ConfigOption<>("username", new String("acee#1220"));
    public static final ConfigOption<String> ABOUT_ME = new ConfigOption<>("about_me", new String("Minecraft modder & game developer"));

    public static final ConfigGroup MY_INFO = new ConfigGroup("my_info", DISCORD_USERNAME, ABOUT_ME);

    @Override
    public ConfigSerializer getSerializer() {
        return YAMLSerializer.builder(this).build();
    }
}
