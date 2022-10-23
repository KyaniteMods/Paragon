package com.kyanite.paragon.api;

import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.api.annotation.ModConfig;
import com.kyanite.paragon.platform.PlatformHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConfigRegistry {
    public static List<ConfigHolder> HOLDERS = new ArrayList<>();

    /**
     * Used to register your config class. You can also use Fabric entrypoints for automatic registration.
     */
    public static void register(String modId, ModConfig config) {
        if(!PlatformHelper.isValidMod(modId)) throw new RuntimeException(modId + " is not a valid mod");
        ConfigHolder holder = new ConfigHolder(modId, config);
        HOLDERS.add(holder);

        Paragon.LOGGER.info("Registered " + modId);
        try {
            holder.init();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load " + holder.getModId() + " :: " + e);
        }
    }

    public static boolean isRegistered(String modId) {
        Optional<ConfigHolder> holder = HOLDERS.stream().filter(configHolder -> configHolder.getModId() == modId).findFirst();
        return holder.isPresent();
    }

    public static void unregister(String modId) {
        Optional<ConfigHolder> holder = HOLDERS.stream().filter(configHolder -> configHolder.getModId() == modId).findFirst();
        if(holder.isPresent())
            HOLDERS.remove(holder);
        else
            throw new RuntimeException(modId + " is not registered");
    }
}
