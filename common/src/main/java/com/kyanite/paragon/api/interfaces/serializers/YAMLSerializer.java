package com.kyanite.paragon.api.interfaces.serializers;

import com.kyanite.paragon.api.ConfigHolder;
import com.kyanite.paragon.api.ConfigManager;
import com.kyanite.paragon.api.ConfigOption;
import com.kyanite.paragon.api.interfaces.Config;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class YAMLSerializer implements ConfigSerializer {
    private final Config configuration;
    private final ConfigHolder configHolder;
    private final int indent;
    private final boolean prettyFlow;
    private final DumperOptions.FlowStyle flowStyle;
    private YAMLSerializer(Config configuration, int indent, boolean prettyFlow, DumperOptions.FlowStyle flowStyle) {
        this.configuration = configuration;
        this.configHolder = ConfigManager.getHolder(configuration);
        this.indent = indent;
        this.prettyFlow = prettyFlow;
        this.flowStyle = flowStyle;
    }

    @Override
    public String getSuffix() {
        return ".toml";
    }

    private DumperOptions dumperOptions() {
        DumperOptions options = new DumperOptions();
        options.setIndent(this.indent);
        options.setPrettyFlow(this.prettyFlow);
        options.setDefaultFlowStyle(this.flowStyle);
        return options;
    }

    @Override
    public void load() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(ConfigManager.getFilePath(this.configHolder.getModId(), this.configuration.configSide(), this.getSuffix())));
        Yaml yaml = new Yaml(dumperOptions()).load(br);
        Map<String, Object> data = yaml.load(br);
        data.entrySet().forEach((entry -> {
            Optional<ConfigOption> option = this.configHolder.getConfigOptions().stream().filter((configOption -> configOption.getTitle() == entry.getKey())).findFirst();
            if(option.isPresent()) option.get().setValue(entry.getValue());
        }));
    }

    @Override
    public void save() throws IOException {
        Yaml yamlWriter = new Yaml(dumperOptions());
        PrintWriter writer = new PrintWriter(ConfigManager.getFilePath(this.configHolder.getModId(), this.configuration.configSide(), this.getSuffix()));
        Map<String, Object> map = new HashMap<>();
        for(ConfigOption configOption : this.configHolder.getConfigOptions().stream().filter(configOption -> !configOption.hasParent()).toList()) map.put(configOption.getTitle(), configOption.get());
        yamlWriter.dump(map, writer);
    }

    public static Builder builder(Config configuration) { return new Builder(configuration); }

    public static class Builder {
        private Config configuration;
        private int indent = 2;
        private DumperOptions.FlowStyle flowStyle = DumperOptions.FlowStyle.BLOCK;
        private boolean prettyFlow = true;

        public Builder indentBy(int value) {
            this.indent = value;
            return this;
        }

        public Builder flowStyle(DumperOptions.FlowStyle value) {
            this.flowStyle = value;
            return this;
        }

        public Builder prettyFlow(boolean value) {
            this.prettyFlow = value;
            return this;
        }

        private Builder(Config configuration) {
            this.configuration = configuration;
        }

        public YAMLSerializer build() {
            return new YAMLSerializer(this.configuration, this.indent, this.prettyFlow, this.flowStyle);
        }
    }
}
