package com.kyanite.paragon.api.interfaces.configtypes;

import com.kyanite.paragon.api.ConfigGroup;
import com.kyanite.paragon.api.ConfigOption;
import com.kyanite.paragon.api.interfaces.BaseModConfig;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.kyanite.paragon.api.ConfigUtils.getFilePath;

@FunctionalInterface
public interface TOMLModConfig extends BaseModConfig {
    @Override
    default void init() {
        try {
            if (getFilePath(this.getModId(), this.configSide(), this.getSuffix()).exists()) load(); else save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    default void load() throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader(getFilePath(this.getModId(), this.configSide(), this.getSuffix())));
        Toml toml = new Toml().read(br);
        toml.entrySet().forEach((entry -> {
            Optional<ConfigOption> option = this.configOptions().stream().filter((configOption -> configOption.getTitle() == entry.getKey())).findFirst();
            if(option.isPresent()) option.get().setValue(entry.getValue());
        }));
    }

    @Override
    default void save() throws IOException {
        TomlWriter tomlWriter = new TomlWriter();
        Map<String, Object> map = new HashMap<>();
        for(ConfigOption configOption : this.configOptions().stream().filter(configOption -> !configOption.hasParent()).toList()) map.put(configOption.getTitle(), configOption.get());
        for(ConfigGroup configGroup : this.configGroups()) {
            Map<String, Object> options = new HashMap<>();
            for(ConfigOption configOption : configGroup.getConfigOptions()) {
                options.put(configOption.getTitle(), configOption.get());
            }
            map.put(configGroup.getTitle(), options);
        }
        tomlWriter.write(map, getFilePath(this.getModId(), this.configSide(), this.getSuffix()));
    }

    @Override
    default String getSuffix() {
        return ".toml";
    }
}
