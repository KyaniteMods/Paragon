package com.kyanite.paragon.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.api.interfaces.ModConfig;

import java.io.IOException;
import java.util.function.Supplier;

import static com.kyanite.paragon.api.ConfigUtils.getFilePath;

public class ConfigOption<T extends Object> implements Supplier<T> {
    private final String title;
    private final T defaultValue;
    private T value;

    public ConfigOption(String title, T defaultValue) {
        this.title = title;
        this.defaultValue = defaultValue;
    }

    public void load(JsonObject jsonObject, ModConfig configHolder) {
        JsonElement element = jsonObject.get(this.getTitle());
        if(element.isJsonNull() || element == null) {
            recover(configHolder);
            return;
        }
        this.setValue((T) configHolder.gson().fromJson(element, defaultValue.getClass()));
    }

    public void save(JsonObject jsonObject, ModConfig configHolder) {
        jsonObject.add(this.getTitle(), configHolder.gson().toJsonTree(this.getDefaultValue()));
    }

    private void recover(ModConfig configHolder) {
        Paragon.LOGGER.error(configHolder.getModId() + " is missing a property : " + this.getTitle() + " - Recovery started");
        getFilePath(configHolder.getModId(), configHolder.configSide()).delete();
        try {
            configHolder.save();
        } catch (IOException e) {
            throw new RuntimeException("Recovery failed for " + configHolder.getModId() + " due to " + e);
        }
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
