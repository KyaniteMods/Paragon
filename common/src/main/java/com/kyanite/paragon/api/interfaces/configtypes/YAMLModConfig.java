package com.kyanite.paragon.api.interfaces.configtypes;

import com.kyanite.paragon.api.ConfigGroup;
import com.kyanite.paragon.api.ConfigOption;
import com.kyanite.paragon.api.interfaces.BaseModConfig;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.kyanite.paragon.api.ConfigUtils.getFilePath;

@FunctionalInterface
public interface YAMLModConfig extends BaseModConfig {
    @Override
    default void init() {
        try {
            if (getFilePath(this.getModId(), this.configSide(), this.getSuffix()).exists()) load(); else save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    default DumperOptions dumperOptions() {
        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return options;
    }

    @Override
    default void load() throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader(getFilePath(this.getModId(), this.configSide(), this.getSuffix())));
        Yaml yaml = new Yaml(dumperOptions()).load(br);
        Map<String, Object> data = yaml.load(br);
        data.entrySet().forEach((entry -> {
            Optional<ConfigOption> option = this.configOptions().stream().filter((configOption -> configOption.getTitle() == entry.getKey())).findFirst();
            if(option.isPresent()) option.get().setValue(entry.getValue());
        }));
    }

    @Override
    default void save() throws IOException {
        Yaml yamlWriter = new Yaml(dumperOptions());
        PrintWriter writer = new PrintWriter(getFilePath(this.getModId(), this.configSide(), this.getSuffix()));
        Map<String, Object> map = new HashMap<>();
        for(ConfigOption configOption : this.configOptions().stream().filter(configOption -> !configOption.hasParent()).toList()) map.put(configOption.getTitle(), configOption.get());
        for(ConfigGroup configGroup : this.configGroups()) {
            Map<String, Object> options = new HashMap<>();
            for(ConfigOption configOption : configGroup.getConfigOptions()) {
                options.put(configOption.getTitle(), configOption.get());
            }
            map.put(configGroup.getTitle(), options);
        }
        yamlWriter.dump(map, writer);
    }

    @Override
    default String getSuffix() {
        return ".yml";
    }
}
