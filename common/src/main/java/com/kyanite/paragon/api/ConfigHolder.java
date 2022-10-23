package com.kyanite.paragon.api;

import com.google.gson.*;
import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.api.annotation.ModConfig;
import com.kyanite.paragon.api.enums.ConfigType;
import com.kyanite.paragon.platform.PlatformHelper;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.kyanite.paragon.api.ConfigUtils.getFilePath;
import static com.kyanite.paragon.api.ConfigUtils.unwrap;

public class ConfigHolder {
    private final String modId;
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private List<ConfigOption> configOptions;
    private ConfigType configType;
    ConfigHolder(String modId, ModConfig configClass) {
        this.modId = modId;
        this.configType = configClass.configType();
        this.configOptions = configClass.configOptions();
    }

    public ConfigHolder(String modId, ConfigType configType, ConfigOption... configOptions) {
        if(!PlatformHelper.isValidMod(modId)) throw new RuntimeException(modId + " is not a valid mod");
        this.modId = modId;
        this.configOptions = Arrays.stream(configOptions).toList();
        this.configType = configType;
        ConfigRegistry.HOLDERS.add(this);
    }

    public List<ConfigOption> getConfigOptions() {
        return configOptions;
    }

    public String getModId() {
        return modId;
    }

    public void init() throws IOException {
        if(getFilePath(this.modId).exists()) {
            load();
        }else{
            save();
        }
    }

    public void save() throws IOException {
        if (configType.equals(ConfigType.STANDARD)) {
            JsonObject config = new JsonObject();

            for (ConfigOption configOption : this.configOptions) {
                config.add(configOption.getTitle(), GSON.toJsonTree(configOption.getDefaultValue()));
            }

            String jsonString = GSON.toJson(config);

            try (FileWriter fileWriter = new FileWriter(getFilePath(this.getModId()))) {
                fileWriter.write(jsonString);
            }

            Paragon.LOGGER.info("Saved config file for " + this.getModId() + " at " + getFilePath(this.getModId()));

            load();
        } else throw new RuntimeException("Legacy config-type is currently a work-in-progress.");
    }

    public String getRaw() throws IOException {
        return FileUtils.readFileToString(getFilePath(this.modId));
    }

    public void load() throws IOException {
        if(configType.equals(ConfigType.STANDARD)) {
            BufferedReader br = new BufferedReader(new FileReader(getFilePath(this.modId)));
            JsonObject json = new JsonParser().parse(br).getAsJsonObject();

            this.configOptions.forEach((configOption -> {
                Optional<Map.Entry<String, JsonElement>> entry = json.entrySet().stream().filter((set -> set.getKey().equals(configOption.getTitle()))).findFirst();
                if(entry.isPresent()) {
                    Object unwrappedObject = unwrap(entry.get().getValue().getAsJsonPrimitive());
                    if(unwrappedObject != null) {
                        configOption.setValue(unwrappedObject);
                        Paragon.LOGGER.info("Set value of " + entry.get().getKey() + " for " + this.getModId());
                    }else{
                        configOption.setValue(configOption.getDefaultValue());
                        throw new RuntimeException("Config option is not supported and was not loaded properly");
                    }
                }else{
                    Paragon.LOGGER.error(this.getModId() + " is missing a property : " + configOption.getTitle() + " - Recovery started");
                    getFilePath(this.getModId()).delete();
                    try {
                        save();
                    } catch (IOException e) {
                        throw new RuntimeException("Recovery failed for " + this.getModId() + " due to " + e);
                    }
                    return;
                }
            }));
        }else throw new RuntimeException("Legacy config-type is currently a work-in-progress.");
    }
}
