package com.kyanite.paragon.api.interfaces;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.api.ConfigOption;
import com.kyanite.paragon.api.enums.ConfigSide;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static com.kyanite.paragon.api.ConfigUtils.getFilePath;

@FunctionalInterface
public interface ModConfig {
    default public List<ConfigOption> configOptions() { return List.of(); }
    default public ConfigSide configSide() {
        return ConfigSide.COMMON;
    }
    default public Gson gson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    public String getModId();
    public default void init() {
        try {
            if(getFilePath(this.getModId(), this.configSide()).exists()) {
                load();
            }else{
                save();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public default void load() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(getFilePath(this.getModId(), this.configSide())));
        JsonObject json = new JsonParser().parse(br).getAsJsonObject();
        this.configOptions().forEach((configOption -> configOption.load(json, this)));
    }

    public default String getRawJSON() throws IOException {
        return FileUtils.readFileToString(getFilePath(this.getModId(), this.configSide()));
    }

    public default void save() throws IOException {
        JsonObject config = new JsonObject();
        for (ConfigOption configOption : this.configOptions()) configOption.save(config, this);
        String jsonString = gson().toJson(config);
        try (FileWriter fileWriter = new FileWriter(getFilePath(this.getModId(), this.configSide()))) { fileWriter.write(jsonString); }
        Paragon.LOGGER.info("Saved config file for " + this.getModId() + " at " + getFilePath(this.getModId(), this.configSide()));
    }
}
