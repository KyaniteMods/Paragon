package com.kyanite.paragon.api;

import com.kyanite.paragon.api.interfaces.Config;
import com.kyanite.paragon.api.interfaces.BaseModConfig;

/**
 * @deprecated use {@link ConfigManager} instead.
 */
@Deprecated(since = "3.0.0", forRemoval = true)
public class ConfigRegistry {
    /**
     * Old system to register configs (no longer usable)
     *
     * @deprecated use {@link ConfigManager#register(String, Config)} instead.
     */

    @Deprecated(since = "3.0.0", forRemoval = true)
    public static void register(BaseModConfig config) {
        throw new RuntimeException("Please migrate your configs to use the new config system. The old system will be removed shortly.");
    }
}
