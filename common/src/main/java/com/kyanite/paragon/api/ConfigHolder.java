package com.kyanite.paragon.api;

import com.google.gson.*;
import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.api.interfaces.ModConfig;
import com.kyanite.paragon.api.enums.ConfigSide;
import com.kyanite.paragon.platform.PlatformHelper;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static com.kyanite.paragon.api.ConfigUtils.getFilePath;
import static com.kyanite.paragon.api.ConfigUtils.unwrap;

public class ConfigHolder {
    private final String modId;
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private List<ConfigOption> configOptions;

    public ConfigSide configSide;
    ConfigHolder(String modId, ModConfig configClass) {
        this.modId = modId;
        this.configSide = configClass.configSide();
        this.configOptions = configClass.configOptions();
    }

    public ConfigHolder(String modId, ConfigSide configSide, ConfigOption... configOptions) {
        if(!PlatformHelper.isValidMod(modId)) throw new RuntimeException(modId + " is not a valid mod");
        this.modId = modId;
        this.configSide = configSide;
        this.configOptions = Arrays.stream(configOptions).toList();
        ConfigRegistry.HOLDERS.add(this);
    }

    public String getModId() {
        return modId;
    }

    public void init() throws IOException {
        if(getFilePath(this.modId, this.configSide).exists()) {
            load();
        }else{
            save();
        }
    }

    public void save() throws IOException {
        JsonObject config = new JsonObject();

        for (ConfigOption configOption : this.configOptions) {
            config.add(configOption.getTitle(), GSON.toJsonTree(configOption.getDefaultValue()));
        }

        String jsonString = GSON.toJson(config);

        try (FileWriter fileWriter = new FileWriter(getFilePath(this.getModId(), this.configSide))) {
            fileWriter.write(jsonString);
        }

        Paragon.LOGGER.info("Saved config file for " + this.getModId() + " at " + getFilePath(this.getModId(), this.configSide));

        load();
    }

    public String getRaw() throws IOException {
        return FileUtils.readFileToString(getFilePath(this.modId, this.configSide));
    }

    public void load() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(getFilePath(this.modId, this.configSide)));
        JsonObject json = new JsonParser().parse(br).getAsJsonObject();

        this.configOptions.forEach((configOption -> {
            Optional<Map.Entry<String, JsonElement>> entry = json.entrySet().stream().filter((set -> set.getKey().equals(configOption.getTitle()))).findFirst();
            if (entry.isPresent()) {
                Object unwrappedObject;
                if(entry.get().getValue().isJsonPrimitive()) {
                    unwrappedObject = unwrap(entry.get().getValue().getAsJsonPrimitive());
                }else{
                    List<Object> list = new ArrayList<>();
                    for(JsonElement jsonPrimitive : entry.get().getValue().getAsJsonArray()) {
                        if(jsonPrimitive.isJsonPrimitive()) list.add(unwrap(jsonPrimitive));
                    }
                    unwrappedObject = list.toArray();
                }
                if (unwrappedObject != null) {
                    configOption.setValue(unwrappedObject);
                    Paragon.LOGGER.info("Set value of " + entry.get().getKey() + " for " + this.getModId());
                } else {
                    configOption.setValue(configOption.getDefaultValue());
                    throw new RuntimeException("Config option is not supported and was not loaded properly");
                }
            } else {
                Paragon.LOGGER.error(this.getModId() + " is missing a property : " + configOption.getTitle() + " - Recovery started");
                getFilePath(this.getModId(), this.configSide).delete();
                try {
                    save();
                } catch (IOException e) {
                    throw new RuntimeException("Recovery failed for " + this.getModId() + " due to " + e);
                }
                return;
            }
        }));
    }
}
