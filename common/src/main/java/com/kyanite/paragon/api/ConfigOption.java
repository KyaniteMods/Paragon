package com.kyanite.paragon.api;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class ConfigOption<T extends Object> implements Supplier<T> {
    private final String title;
    /**
     * Used in Epitome for tooltips
     */
    private String description = "";
    private final T defaultValue;
    private Optional<T> value;
    private Class<?> tClass;
    @Nullable private ConfigGroup configGroup;

    public ConfigOption(String title, T defaultValue) {
        this.title = title;
        this.defaultValue = defaultValue;
        this.tClass = defaultValue.getClass();
        this.value = Optional.of(defaultValue);
    }

    public ConfigOption(String title, String description, T defaultValue) {
        this.title = title;
        this.defaultValue = defaultValue;
        this.tClass = defaultValue.getClass();
        this.description = description;
        this.value = Optional.of(defaultValue);
    }

    public boolean hasParent() { return this.configGroup != null; }
    public void setConfigGroup(ConfigGroup group) { this.configGroup = group; }
    public ConfigGroup getConfigGroup() { return this.configGroup; }
    public void setValue(T value) {
        this.value = Optional.ofNullable(value);
    }
    public String getDescription() { return description; }
    public String getTitle() {
        return title;
    }
    public Class<?> getValueClass() { return tClass; }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public T get() {
        return this.value.isPresent() ? this.value.get() : this.getDefaultValue();
    }
}
