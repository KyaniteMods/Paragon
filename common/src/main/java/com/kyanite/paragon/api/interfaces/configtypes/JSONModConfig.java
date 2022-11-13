package com.kyanite.paragon.api.interfaces.configtypes;

import com.google.gson.*;
import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.api.ConfigOption;
import com.kyanite.paragon.api.interfaces.BaseModConfig;

import java.io.*;

import static com.kyanite.paragon.api.ConfigUtils.getFilePath;

@FunctionalInterface
public interface JSONModConfig extends BaseModConfig {
    default public Gson gson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    public default void init() {
        try {
            if (getFilePath(this.getModId(), this.configSide(), this.getSuffix()).exists()) load(); else save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Suffix for the config file, default is JSON (see TOMLModConfig)
     */
    public default String getSuffix() {
        return ".json";
    }

    /**
     * Loading the values from the config file
     */
    public default void load() throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader(getFilePath(this.getModId(), this.configSide(), this.getSuffix())));
        JsonObject json = new JsonParser().parse(br).getAsJsonObject();
        this.configOptions().forEach((configOption -> {
            JsonElement element = json.get(configOption.getTitle());
            if(element == null) {
                getFilePath(getModId(), configSide(), getSuffix()).delete();
                try {
                    save();
                } catch (IOException e) {
                    throw new RuntimeException("Recovery failed for " + getModId() + " due to " + e);
                }
                return;
            }
            configOption.setValue(gson().fromJson(element, configOption.getValueClass()));
        }));
    }

    /**
     * Saving the config file with default values
     */
    public default void save() throws IOException {
        JsonObject config = new JsonObject();
        for (ConfigOption configOption : this.configOptions()) {
            config.add(configOption.getTitle(), gson().toJsonTree(configOption.getDefaultValue()));
        }
        String jsonString = gson().toJson(config);
        try (FileWriter fileWriter = new FileWriter(getFilePath(this.getModId(), this.configSide(), this.getSuffix()))) { fileWriter.write(jsonString); }
        Paragon.LOGGER.info("Saved config file for " + this.getModId() + " at " + getFilePath(this.getModId(), this.configSide(), this.getSuffix()));
    }
}
