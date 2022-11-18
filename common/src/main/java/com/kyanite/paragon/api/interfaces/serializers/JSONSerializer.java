package com.kyanite.paragon.api.interfaces.serializers;

import com.google.gson.*;
import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.api.ConfigHolder;
import com.kyanite.paragon.api.ConfigManager;
import com.kyanite.paragon.api.interfaces.Config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;

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

    @Override
    public void load() throws IOException {
        BufferedReader reader = Files.newBufferedReader(this.configHolder.getFile().toPath());
        JsonElement element = JsonParser.parseReader(reader);
        if(element == null) {
            Paragon.LOGGER.error("Unable to load config file because element was null");
            return;
        }
        JsonObject json = new JsonParser().parse(reader).getAsJsonObject();
        this.configHolder.getConfigOptions().forEach(configOption -> {
            configOption.setValue(this.gson.fromJson(json.get(configOption.getTitle()), configOption.getValueClass()));
        });
    }

    @Override
    public void save() throws IOException {
        JsonObject config = new JsonObject();
        BufferedWriter writer = Files.newBufferedWriter(this.configHolder.getFile().toPath());
        this.configHolder.getConfigOptions().forEach(configOption -> {
            config.add(configOption.getTitle(), this.gson.toJsonTree(configOption.get()));
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
