package com.kyanite.paragon.api;

import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.api.enums.ConfigSide;
import com.kyanite.paragon.api.interfaces.Config;
import com.kyanite.paragon.platform.PlatformHelper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConfigManager {
    private static Map<Config, ConfigHolder> CONFIGS = new HashMap<>();

    public static Map<Config, ConfigHolder> getRegisteredConfigs() { return ConfigManager.CONFIGS; }

    public static void register(String modId, Config config) {
        if(!PlatformHelper.isValidMod(modId)) throw new RuntimeException(modId + " is not a valid mod");
        if(isRegistered(modId, config.configSide())) throw new RuntimeException(modId + " is already registered!");
        if(config.configSide() == ConfigSide.CLIENT && PlatformHelper.isOnServer()) throw new RuntimeException("Cannot register client config on server! (" + modId + ")");

        ConfigHolder configHolder = new ConfigHolder(modId, config);
        CONFIGS.put(config, configHolder);

        Paragon.LOGGER.info("Registered " + modId);
        configHolder.init();
    }

    public static ConfigHolder getHolder(Config config) {
        return CONFIGS.get(config);
    }

    public static boolean isRegistered(String modId, ConfigSide configSide) {
        return getConfigSet(modId, configSide).isPresent();
    }

    public static String getRawJson(File path) throws IOException {
        return FileUtils.readFileToString(path);
    }

    public static File getFilePath(String modId, ConfigSide configSide, String suffix) {
        return new File(PlatformHelper.getConfigPath(), modId + (configSide == ConfigSide.CLIENT ? "-client" : "") + suffix);
    }

    public static Optional<Map.Entry<Config, ConfigHolder>> getConfigSet(String modId, ConfigSide configSide) {
        return CONFIGS.entrySet().stream().filter(configHolder -> configHolder.getValue().getModId() == modId && configHolder.getKey().configSide() == configSide).findFirst();
    }

    public static void unregister(String modId, ConfigSide configSide) {
        Optional<Map.Entry<Config, ConfigHolder>> holder = getConfigSet(modId, configSide);
        if(holder.isPresent())
            CONFIGS.remove(holder);
        else
            throw new RuntimeException(modId + " is not registered");
    }
}
