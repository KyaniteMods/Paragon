package com.kyanite.paragon.platform.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class PlatformHelperImpl {
    public static boolean isValidMod(String modId) {
        return FabricLoader.getInstance().getModContainer(modId).isPresent();
    }
    public static String getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().toString();
    }

    public static boolean isOnServer() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
    }
}
