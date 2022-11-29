package com.kyanite.paragon.api.interfaces.serializers;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonGrammar;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.api.ConfigHolder;
import com.kyanite.paragon.api.ConfigManager;
import com.kyanite.paragon.api.ConfigOption;
import com.kyanite.paragon.api.interfaces.Config;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class JSON5Serializer implements ConfigSerializer {
    private final Config configuration;
    private final ConfigHolder configHolder;
    private final boolean useJson5Suffix;
    private JSON5Serializer(Config configuration, boolean useJson5Suffix) {
        this.configuration = configuration;
        this.configHolder = ConfigManager.getHolder(configuration);
        this.useJson5Suffix = useJson5Suffix;
    }

    @Override
    public String getSuffix() {
        return this.useJson5Suffix ? ".json5" : ".json";
    }

    private void setOptionsValue(ConfigOption configOption, blue.endless.jankson.JsonObject json) throws IOException {
        Object object = json.get(configOption.getValueClass(), configOption.getTitle());
        if(object == null) {
            save();
            return;
        } else configOption.setValue(object);
    }

    @Override
    public void load() throws IOException, SyntaxError {
        blue.endless.jankson.JsonObject json = Jankson.builder().build().load(this.configHolder.getFile());

        this.configHolder.getConfigOptions().stream().filter(configOption -> !configOption.hasParent()).forEach(configOption -> {
            try { setOptionsValue(configOption, json); } catch (IOException e) {
                Paragon.LOGGER.error("Unable to load " + configOption.getTitle() + " because of " + e);
            }
        });

        this.configHolder.getConfigGroups().forEach(configGroup -> {
            JsonObject object = json.getObject(configGroup.getTitle());
            if(object == null) {
                try { save(); } catch (IOException e) {
                    Paragon.LOGGER.error("Unable to load " + configGroup.getTitle() + " because of " + e);
                }
                return;
            }

            configGroup.getConfigOptions().forEach(configOption -> {
                try { setOptionsValue(configOption, object); } catch (IOException e) {
                    Paragon.LOGGER.error("Unable to load " + configOption.getTitle() + " because of " + e);
                }
            });
        });
    }

    @Override
    public void save() throws IOException {
        blue.endless.jankson.JsonObject config = new blue.endless.jankson.JsonObject();
        BufferedWriter writer = Files.newBufferedWriter(this.configHolder.getFile().toPath());

        this.configHolder.getConfigOptions().stream().filter(configOption -> !configOption.hasParent()).forEach(configOption -> {
            config.putDefault(configOption.getTitle(), configOption.getDefaultValue(), configOption.getDescription());
        });

        this.configHolder.getConfigGroups().forEach(configGroup -> {
            Map<String, Object> map = new HashMap<>();
            configGroup.getConfigOptions().forEach(configOption -> map.put(configOption.getTitle(), configOption.get()));
            config.putDefault(configGroup.getTitle(), map, configGroup.getDescription());
            configGroup.getConfigOptions().forEach(configOption -> config.getObject(configGroup.getTitle()).setComment(configOption.getTitle(), configOption.getDescription()));
        });

        writer.write(config.toJson(JsonGrammar.JSON5));
        writer.close();
    }

    public static Builder builder(Config configuration) { return new Builder(configuration); }

    public static class Builder {
        private Config configuration;
        private boolean useJson5Suffix = true;

        public Builder useDefaultJsonSuffix() {
            this.useJson5Suffix = false;
            return this;
        }

        private Builder(Config configuration) {
            this.configuration = configuration;
        }

        public JSON5Serializer build() {
            return new JSON5Serializer(this.configuration, this.useJson5Suffix);
        }
    }
}
