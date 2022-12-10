package com.kyanite.paragon.example;

import blue.endless.jankson.Comment;
import com.kyanite.paragon.api.ConfigGroup;
import com.kyanite.paragon.api.ConfigOption;
import com.kyanite.paragon.api.interfaces.Config;
import com.kyanite.paragon.api.interfaces.serializers.ConfigSerializer;
import com.kyanite.paragon.api.interfaces.serializers.JSON5Serializer;

public class TestModConfig implements Config {
    public static final ConfigOption<String> DISCORD_USERNAME = new ConfigOption<>("username", new String("acee#1220"));
    @Comment("What do I do?")
    public static final ConfigOption<String> ABOUT_ME = new ConfigOption<>("about_me", new String("Minecraft modder & game developer (also the creator of this super cool config-lib ur using rn!)"));

    public static final ConfigGroup MY_INFO = new ConfigGroup("my_info", DISCORD_USERNAME, ABOUT_ME);

    @Override
    public ConfigSerializer getSerializer() {
        return JSON5Serializer.builder(this).build();
    }
}
