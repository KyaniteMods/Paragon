package com.kyanite.paragon.api;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class ConfigOption<T extends Object> implements Supplier<T> {
    private final String title;
    private final T defaultValue;
    private Optional<T> value;
    private Class<?> tClass;
    @Nullable private ConfigGroup configGroup;
    private String description = "";

    public ConfigOption(String title, T defaultValue) {
        this.title = title;
        this.defaultValue = defaultValue;
        this.tClass = defaultValue.getClass();
        this.value = Optional.of(defaultValue);
    }

    public void setDescription(String description) { this.description = description; }
    public String getDescription() { return this.description; }
    public boolean hasParent() { return this.configGroup != null; }
    public void setConfigGroup(ConfigGroup group) { this.configGroup = group; }
    public ConfigGroup getConfigGroup() { return this.configGroup; }
    public void setValue(T value) {
        this.value = Optional.ofNullable(value);
    }
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
