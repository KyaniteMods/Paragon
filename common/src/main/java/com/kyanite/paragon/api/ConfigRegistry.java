package com.kyanite.paragon.api;

import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.api.enums.ConfigSide;
import com.kyanite.paragon.api.interfaces.BaseModConfig;
import com.kyanite.paragon.platform.PlatformHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConfigRegistry {
    public static List<BaseModConfig> CONFIGS = new ArrayList<>();
    /**
     * Used to register your config class. You can also use Fabric entrypoints for automatic registration.
     */
    public static void register(BaseModConfig config) {
        if(!PlatformHelper.isValidMod(config.getModId())) throw new RuntimeException(config.getModId() + " is not a valid mod");
        if(isRegistered(config.getModId(), config.configSide())) throw new RuntimeException(config.getModId() + " is already registered!");

        CONFIGS.add(config);

        Paragon.LOGGER.info("Registered " + config.getModId());
        config.init();
    }

    public static boolean isRegistered(String modId, ConfigSide configSide) {
        Optional<BaseModConfig> holder = CONFIGS.stream().filter(configHolder -> configHolder.getModId() == modId && configHolder.configSide() == configSide).findFirst();
        return holder.isPresent();
    }

    public static void unregister(String modId, ConfigSide configSide) {
        Optional<BaseModConfig> holder = CONFIGS.stream().filter(configHolder -> configHolder.getModId() == modId && configHolder.configSide() == configSide).findFirst();
        if(holder.isPresent())
            CONFIGS.remove(holder);
        else
            throw new RuntimeException(modId + " is not registered");
    }
}
