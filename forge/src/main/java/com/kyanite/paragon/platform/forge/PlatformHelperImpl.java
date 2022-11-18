package com.kyanite.paragon.platform.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;

public class PlatformHelperImpl {
    public static boolean isValidMod(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static String getConfigPath() {
        return FMLPaths.CONFIGDIR.get().toString();
    }

    public static boolean isOnServer() {
        return FMLLoader.getDist() == Dist.DEDICATED_SERVER;
    }
}
