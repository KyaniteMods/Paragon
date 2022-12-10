package com.kyanite.paragon.api.interfaces.serializers;

import com.google.gson.*;
import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.api.ConfigHolder;
import com.kyanite.paragon.api.ConfigManager;
import com.kyanite.paragon.api.ConfigOption;
import com.kyanite.paragon.api.interfaces.Config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * @deprecated
 * This is still perfectly fine to use, and will not cause any actual issues but using JSON5 ({@link JSON5Serializer})
 * is recommended due to its many advantages, such as comments and other sanitization features.
 */
@Deprecated
public class JSONSerializer implements ConfigSerializer {
    private final Gson gson;
    private final Config configuration;
    private final ConfigHolder configHolder;
    private JSONSerializer(Gson gson, Config configuration) {
        this.gson = gson;
        this.configuration = configuration;
        this.configHolder = ConfigManager.getHolder(configuration);
    }

    @Override
    public String getSuffix() {
        return ".json";
    }

    private void setOptionsValue(ConfigOption configOption, JsonObject json) throws IOException {
        JsonElement element = json.get(configOption.getTitle());
        if(element == null) {
            Paragon.LOGGER.error("Couldn't find " + configOption.getTitle() + " : resetting config");
            save();
            return;
        } else configOption.setValue(gson.fromJson(element, configOption.getValueClass()));
    }

    @Override
    public void load() throws IOException {
        BufferedReader reader = Files.newBufferedReader(this.configHolder.getFile().toPath());
        JsonElement jsonElement = JsonParser.parseReader(reader);
        if(jsonElement != null && jsonElement.isJsonObject()) {
            JsonObject json = jsonElement.getAsJsonObject();
            this.configHolder.getConfigOptions().stream().filter(configOption -> !configOption.hasParent()).forEach(configOption -> {
                try { setOptionsValue(configOption, json); } catch (IOException e) {
                    Paragon.LOGGER.error("Unable to load " + configOption.getTitle() + " because of " + e);
                }
            });
            this.configHolder.getConfigGroups().forEach(configGroup -> {
                JsonElement element1 = json.get(configGroup.getTitle());
                if(element1 == null) {
                    Paragon.LOGGER.error("Couldn't find " + configGroup.getTitle() + " : resetting config");
                    try { save(); } catch (IOException e) {
                        Paragon.LOGGER.error("Unable to load " + configGroup.getTitle() + " because of " + e);
                    }
                    return;
                }

                configGroup.getConfigOptions().forEach(configOption -> {
                    try { setOptionsValue(configOption, element1.getAsJsonObject()); } catch (IOException e) {
                        Paragon.LOGGER.error("Unable to load " + configOption.getTitle() + " because of " + e);
                    }
                });
            });
        }else{
            Paragon.LOGGER.warn("Reloading " + this.configHolder.getModId() + "'s config because there was an error while deserializing");
            save();
        }
    }

    @Override
    public void save() throws IOException {
        JsonObject config = new JsonObject();
        BufferedWriter writer = Files.newBufferedWriter(this.configHolder.getFile().toPath());

        this.configHolder.getConfigOptions().stream().filter(configOption -> !configOption.hasParent()).forEach(configOption -> config.add(configOption.getTitle(), this.gson.toJsonTree(configOption.get())));

        this.configHolder.getConfigGroups().forEach(configGroup -> {
            Map<String, Object> options = new HashMap<>();
            configGroup.getConfigOptions().forEach(configOption -> options.put(configOption.getTitle(), configOption.get()));
            config.add(configGroup.getTitle(), gson.toJsonTree(options));
        });

        writer.write(gson.toJson(config));
        writer.close();
    }

    public static Builder builder(Config configuration) { return new Builder(configuration); }

    public static class Builder {
        private Gson gson = new GsonBuilder().setPrettyPrinting().create();
        private Config configuration;

        private Builder(Config configuration) {
            this.configuration = configuration;
        }

        public Builder useCustomGson(Gson customGson) {
            this.gson = customGson;
            return this;
        }

        public JSONSerializer build() {
            return new JSONSerializer(this.gson, this.configuration);
        }
    }
}
