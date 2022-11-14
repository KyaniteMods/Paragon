package com.kyanite.paragon.api.interfaces.configtypes;

import com.google.gson.*;
import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.api.ConfigGroup;
import com.kyanite.paragon.api.ConfigOption;
import com.kyanite.paragon.api.interfaces.BaseModConfig;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

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
        for (ConfigOption configOption : this.configOptions().stream().filter(configOption -> !configOption.hasParent()).toList()) {
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
        }

        for(ConfigGroup configGroup : this.configGroups()) {
            JsonObject element = json.get(configGroup.getTitle()).getAsJsonObject();
            if(element == null) {
                getFilePath(getModId(), configSide(), getSuffix()).delete();
                try {
                    save();
                } catch (IOException e) {
                    throw new RuntimeException("Recovery failed for " + getModId() + " due to " + e);
                }
                return;
            }

            for(ConfigOption configOption : configGroup.getConfigOptions()) {
                JsonElement element1 = element.get(configOption.getTitle());
                if(element1 == null) {
                    getFilePath(getModId(), configSide(), getSuffix()).delete();
                    try {
                        save();
                    } catch (IOException e) {
                        throw new RuntimeException("Recovery failed for " + getModId() + " due to " + e);
                    }
                    return;
                }
                configOption.setValue(gson().fromJson(element1, configOption.getValueClass()));
            }
        }
    }

    /**
     * Saving the config file with default values
     */
    public default void save() throws IOException {
        JsonObject config = new JsonObject();
        for (ConfigOption configOption : this.configOptions().stream().filter(configOption -> !configOption.hasParent()).toList()) {
            config.add(configOption.getTitle(), gson().toJsonTree(configOption.get()));
        }
        for(ConfigGroup configGroup : this.configGroups()) {
            Map<String, Object> options = new HashMap<>();
            for(ConfigOption configOption : configGroup.getConfigOptions()) {
                options.put(configOption.getTitle(), configOption.get());
            }
            config.add(configGroup.getTitle(), gson().toJsonTree(options));
        }
        String jsonString = gson().toJson(config);
        try (FileWriter fileWriter = new FileWriter(getFilePath(this.getModId(), this.configSide(), this.getSuffix()))) { fileWriter.write(jsonString); }
        Paragon.LOGGER.info("Saved config file for " + this.getModId() + " at " + getFilePath(this.getModId(), this.configSide(), this.getSuffix()));
    }
}
