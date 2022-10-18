package com.kyanite.paragon.platform.forge;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;

public class PlatformHelperImpl {
    public static boolean isValidMod(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static String getConfigPath() {
        return FMLPaths.CONFIGDIR.get().toString();
    }
}
