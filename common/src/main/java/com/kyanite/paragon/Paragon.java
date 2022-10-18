package com.kyanite.paragon;

import com.kyanite.paragon.api.ConfigHolder;
import com.kyanite.paragon.api.ConfigRegistry;
import com.kyanite.paragon.example.TestMod;
import com.kyanite.paragon.platform.PlatformHelper;
import com.mojang.logging.LogUtils;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;

import java.io.IOException;

public class Paragon {
    public static final String MOD_ID = "paragon";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        TestMod mod = new TestMod();
        mod.init();

        for(ConfigHolder configHolder : ConfigRegistry.HOLDERS) {
            if(PlatformHelper.isValidMod(configHolder.getModId())) { // Already checked during config registration, but lets double check anyways
                try {
                    configHolder.init();
                } catch (IOException | ParseException e) {
                    throw new RuntimeException("Unable to load " + configHolder.getModId() + " :: " + e);
                }
            }else{
                throw new RuntimeException(configHolder.getModId() + " is not a valid mod");
            }
        }
    }
}