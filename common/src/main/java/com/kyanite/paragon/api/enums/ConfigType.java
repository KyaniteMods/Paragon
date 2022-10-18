package com.kyanite.paragon.api.enums;

public enum ConfigType {
    /**
         * JSON - Best for modern mods, but not very compatible with other config-related mods.
     */
    STANDARD,
    /**
     * TOML - Configuration system used in older config-libraries. Compatible with most if not all config-related mods.
     */
    LEGACY
}
