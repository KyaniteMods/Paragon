package com.kyanite.paragon.api;

import com.kyanite.paragon.api.annotation.ModConfig;
import com.kyanite.paragon.platform.PlatformHelper;

import java.util.ArrayList;
import java.util.List;

public class ConfigRegistry {
    public static List<ConfigHolder> HOLDERS = new ArrayList<>();

    /**
     * Used to register your config class. You can also use Fabric entrypoints for automatic registration.
     */
    public static void register(String modId, ModConfig config) {
        if(!PlatformHelper.isValidMod(modId)) throw new RuntimeException(modId + " is not a valid mod");
        ConfigHolder holder = new ConfigHolder(modId, config);
        HOLDERS.add(holder);
    }
}
