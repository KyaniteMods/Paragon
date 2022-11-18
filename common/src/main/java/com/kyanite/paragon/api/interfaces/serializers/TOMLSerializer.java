package com.kyanite.paragon.api.interfaces.serializers;

import com.kyanite.paragon.api.ConfigHolder;
import com.kyanite.paragon.api.ConfigManager;
import com.kyanite.paragon.api.ConfigOption;
import com.kyanite.paragon.api.interfaces.Config;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TOMLSerializer implements ConfigSerializer {
    private final Config configuration;
    private final ConfigHolder configHolder;
    private final int indentValues, indentTables, padArrayDelimitersBy;
    private TOMLSerializer(Config configuration, int indentValues, int indentTables, int padArrayDelimitersBy) {
        this.configuration = configuration;
        this.configHolder = ConfigManager.getHolder(configuration);
        this.indentValues = indentValues;
        this.indentTables = indentTables;
        this.padArrayDelimitersBy = padArrayDelimitersBy;
    }

    @Override
    public String getSuffix() {
        return ".toml";
    }

    @Override
    public void load() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(ConfigManager.getFilePath(this.configHolder.getModId(), this.configuration.configSide(), this.getSuffix())));
        Toml toml = new Toml().read(br);
        toml.entrySet().forEach((entry -> {
            Optional<ConfigOption> option = this.configHolder.getConfigOptions().stream().filter((configOption -> configOption.getTitle() == entry.getKey())).findFirst();
            if(option.isPresent()) option.get().setValue(entry.getValue());
        }));
    }

    @Override
    public void save() throws IOException {
        TomlWriter tomlWriter = new TomlWriter.Builder()
                .indentValuesBy(this.indentValues)
                .indentTablesBy(this.indentTables)
                .padArrayDelimitersBy(this.padArrayDelimitersBy)
                .build();

        Map<String, Object> map = new HashMap<>();
        for(ConfigOption configOption : this.configHolder.getConfigOptions().stream().filter(configOption -> !configOption.hasParent()).toList()) map.put(configOption.getTitle(), configOption.get());
        tomlWriter.write(map, ConfigManager.getFilePath(this.configHolder.getModId(), this.configuration.configSide(), this.getSuffix()));
    }

    public static Builder builder(Config configuration) { return new Builder(configuration); }

    public static class Builder {
        private Config configuration;
        private int indentValues = 0, indentTables = 0, padArrayDelimitersBy = 0;

        public Builder indentValuesBy(int value) {
            this.indentValues = value;
            return this;
        }

        public Builder indentTablesBy(int value) {
            this.indentTables = value;
            return this;
        }

        public Builder padArrayDelimitersBy(int value) {
            this.padArrayDelimitersBy = value;
            return this;
        }

        private Builder(Config configuration) {
            this.configuration = configuration;
        }

        public TOMLSerializer build() {
            return new TOMLSerializer(this.configuration, this.indentValues, this.indentTables, this.padArrayDelimitersBy);
        }
    }
}
