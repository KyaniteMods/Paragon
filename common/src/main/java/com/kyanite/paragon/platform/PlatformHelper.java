package com.kyanite.paragon.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class PlatformHelper {
    @ExpectPlatform
    public static boolean isValidMod(String modId) {
        throw new AssertionError();
    }
    @ExpectPlatform
    public static String getConfigPath() {
        throw new AssertionError();
    }
}
