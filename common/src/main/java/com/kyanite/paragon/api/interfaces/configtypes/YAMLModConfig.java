package com.kyanite.paragon.api.interfaces.configtypes;

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
        for(ConfigOption configOption : this.configOptions()) map.put(configOption.getTitle(), configOption.getDefaultValue());
        yamlWriter.dump(map, writer);
    }

    @Override
    default String getSuffix() {
        return ".yml";
    }
}
