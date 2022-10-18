package com.kyanite.paragon.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.api.annotation.ModConfig;
import com.kyanite.paragon.api.enums.ConfigType;
import com.kyanite.paragon.platform.PlatformHelper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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

    public void init() throws IOException, ParseException {
        if(getFilePath().exists()) load(); else save();
    }

    public static String getPerfectJSON(String unformattedJSON) {
        String perfectJSON = GSON.toJson(JsonParser.parseString(unformattedJSON));
        return perfectJSON;
    }

    public void save() {
        if(configType.equals(ConfigType.STANDARD)) {
            JSONObject jsonObject = new JSONObject();

            for(ConfigOption configOption : configOptions) {
                jsonObject.put(configOption.getTitle(), configOption.value());
            }

            try (FileWriter file = new FileWriter(getFilePath())) {
                file.write(getPerfectJSON(jsonObject.toJSONString()));
                file.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else throw new RuntimeException("Legacy config-type is currently a work-in-progress.");
    }

    public void load() throws IOException, ParseException {
        if(configType.equals(ConfigType.STANDARD)) {
            JSONParser parser = new JSONParser();

            try (Reader reader = new FileReader(getFilePath())) {
                JSONObject jsonObject = (JSONObject) parser.parse(reader);
                for (ConfigOption configOption : configOptions) {
                    Object title = jsonObject.get(configOption.getTitle());
                    if(title != null) {
                        configOption.setValue(title);
                    }
                    Paragon.LOGGER.info(String.valueOf(title));
                }
            }
        }else throw new RuntimeException("Legacy config-type is currently a work-in-progress.");
    }
}
