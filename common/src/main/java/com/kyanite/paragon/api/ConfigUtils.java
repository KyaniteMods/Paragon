package com.kyanite.paragon.api;

import com.kyanite.paragon.api.enums.ConfigSide;
import com.kyanite.paragon.platform.PlatformHelper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class ConfigUtils {
    public static String getRawJson(File path) throws IOException {
        return FileUtils.readFileToString(path);
    }

    public static File getFilePath(String modId, ConfigSide configSide, String suffix) {
        return new File(PlatformHelper.getConfigPath(), modId + (configSide == ConfigSide.CLIENT ? "-client" : "") + suffix);
    }
}
