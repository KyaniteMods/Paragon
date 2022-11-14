package com.kyanite.paragon.api;

import java.util.Arrays;
import java.util.List;

public class ConfigGroup {
    private final String title;
    /**
     * Used in Epitome for tooltips
     */
    private String description = "";
    private List<ConfigOption> configOptions;

    public ConfigGroup(String title, String description, ConfigOption... options) {
        this.title = title;
        this.configOptions = Arrays.stream(options).toList();
        this.description = description;
        this.configOptions.forEach((configOption -> {
            configOption.setConfigGroup(this);
        }));
    }

    public ConfigGroup(String title, ConfigOption... options) {
        this.title = title;
        this.configOptions = Arrays.stream(options).toList();
        this.configOptions.forEach((configOption -> {
            configOption.setConfigGroup(this);
        }));
    }

    public String getDescription() { return this.description; }
    public String getTitle() { return this.title; }
    public List<ConfigOption> getConfigOptions() { return this.configOptions; }
}
