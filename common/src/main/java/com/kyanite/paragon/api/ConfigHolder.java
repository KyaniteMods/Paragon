package com.kyanite.paragon.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kyanite.paragon.api.annotation.ModConfig;
import com.kyanite.paragon.api.enums.ConfigType;
import com.kyanite.paragon.platform.PlatformHelper;

import java.io.*;
import java.util.Arrays;
import java.util.List;

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

    public File getFilePath() {
        return new File(PlatformHelper.getConfigPath(), modId + (configType.equals(ConfigType.STANDARD) ? ".json" : ".toml"));
    }
    public String getModId() {
        return modId;
    }

    public void init() throws IOException {
        if(getFilePath().exists()) load(); else save();
    }

    public static String getPerfectJSON(String unformattedJSON) {
        String perfectJSON = GSON.toJson(JsonParser.parseString(unformattedJSON));
        return perfectJSON;
    }

    public void save() throws IOException {
        if (configType.equals(ConfigType.STANDARD)) {
            JsonObject config = new JsonObject();

            for (ConfigOption configOption : configOptions) {
                config.add(configOption.getTitle(), GSON.toJsonTree(configOption.value()));
            }

            String jsonString = GSON.toJson(config);

            try (FileWriter fileWriter = new FileWriter(getFilePath())) {
                fileWriter.write(jsonString);
            }
        } else throw new RuntimeException("Legacy config-type is currently a work-in-progress.");
    }

    public void load() throws IOException {
        if(configType.equals(ConfigType.STANDARD)) {
            BufferedReader br = new BufferedReader(new FileReader(getFilePath()));
            JsonObject json = new JsonParser().parse(br).getAsJsonObject();

            for (ConfigOption configOption : configOptions) {
                Object title = json.get(configOption.getTitle());
                if(title != null) {
                    configOption.setValue(title);
                }
            }
        }else throw new RuntimeException("Legacy config-type is currently a work-in-progress.");
    }
}
