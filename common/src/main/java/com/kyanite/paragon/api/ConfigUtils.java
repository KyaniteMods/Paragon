package com.kyanite.paragon.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.kyanite.paragon.api.enums.ConfigSide;
import com.kyanite.paragon.platform.PlatformHelper;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
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

    public static Object unwrap(final Object o) {
        if (o == null) {
            return null;
        }
        if (!(o instanceof JsonElement)) {
            return o;
        }
        JsonElement e = (JsonElement) o;
        if (e.isJsonNull()) {
            return null;
        } else if (e.isJsonPrimitive()) {
            JsonPrimitive p = e.getAsJsonPrimitive();
            if (p.isString()) {
                return p.getAsString();
            } else if (p.isBoolean()) {
                return p.getAsBoolean();
            } else if (p.isNumber()) {
                return unwrapNumber(p.getAsNumber());
            }
        }
        return o;
    }

    public static boolean isPrimitiveNumber(final Number n) {
        return n instanceof Integer ||
                n instanceof Float ||
                n instanceof Double ||
                n instanceof Long ||
                n instanceof BigDecimal ||
                n instanceof BigInteger;
    }
    public static Number unwrapNumber(final Number n) {
        Number unwrapped;

        if (!isPrimitiveNumber(n)) {
            BigDecimal bigDecimal = new BigDecimal(n.toString());
            if (bigDecimal.scale() <= 0) {
                if (bigDecimal.abs().compareTo(new BigDecimal(Integer.MAX_VALUE)) <= 0) {
                    unwrapped = bigDecimal.intValue();
                } else if (bigDecimal.abs().compareTo(new BigDecimal(Long.MAX_VALUE)) <= 0){
                    unwrapped = bigDecimal.longValue();
                } else {
                    unwrapped = bigDecimal;
                }
            } else {
                final double doubleValue = bigDecimal.doubleValue();
                if (BigDecimal.valueOf(doubleValue).compareTo(bigDecimal) != 0) {
                    unwrapped = bigDecimal;
                } else {
                    unwrapped = doubleValue;
                }
            }
        } else {
            unwrapped = n;
        }
        return unwrapped;
    }

    public static File getFilePath(String modId, ConfigSide configSide) {
        return new File(PlatformHelper.getConfigPath(), modId + (configSide == ConfigSide.CLIENT ? "-client" : "") + ".json");
    }
}
