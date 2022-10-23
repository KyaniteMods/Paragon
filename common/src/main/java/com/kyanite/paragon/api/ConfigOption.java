package com.kyanite.paragon.api;

import java.util.function.Supplier;

public class ConfigOption<T extends Object> implements Supplier<T> {
    private final String title;
    private final T defaultValue;
    private T value;

    public ConfigOption(String title, T defaultValue) {
        this.title = title;
        this.defaultValue = defaultValue;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public T get() {
        return this.value;
    }
}
