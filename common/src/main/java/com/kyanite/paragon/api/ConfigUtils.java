package com.kyanite.paragon.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kyanite.paragon.platform.PlatformHelper;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigUtils {
    public static List<ConfigOption> getOptionsFromJson(File jsonPath) throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader(jsonPath));
        JsonObject json = new JsonParser().parse(br).getAsJsonObject();
        List<ConfigOption> configOptions = new ArrayList<>();
        for(Map.Entry<String, JsonElement> entrySet : json.entrySet()) {
            configOptions.add(new ConfigOption(entrySet.getKey(), entrySet.getValue()));
        }
        return configOptions;
    }

    public static List<ConfigOption> getOptionsFromJson(String raw) throws FileNotFoundException {
        JsonObject json = new JsonParser().parse(raw).getAsJsonObject();
        List<ConfigOption> configOptions = new ArrayList<>();
        for(Map.Entry<String, JsonElement> entrySet : json.entrySet()) {
            configOptions.add(new ConfigOption(entrySet.getKey(), entrySet.getValue()));
        }
        return configOptions;
    }

    public static String getRawJson(File path) throws IOException {
        return FileUtils.readFileToString(path);
    }

    public static File getFilePath(String modId) {
        return new File(PlatformHelper.getConfigPath(), modId + ".json");
    }
}
