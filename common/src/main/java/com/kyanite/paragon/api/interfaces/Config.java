package com.kyanite.paragon.api.interfaces;

import com.kyanite.paragon.api.enums.ConfigSide;
import com.kyanite.paragon.api.interfaces.serializers.ConfigSerializer;
import com.kyanite.paragon.api.interfaces.serializers.JSON5Serializer;

public interface Config {
    public default ConfigSide configSide() {
        return ConfigSide.COMMON;
    }
    public default ConfigSerializer getSerializer() {
        return JSON5Serializer.builder(this).build();
    }

    public default String getFileName() {
        return null;
    }
}
