package com.kyanite.paragon;

import com.kyanite.paragon.example.TestMod;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class Paragon {
    public static final String MOD_ID = "paragon";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        new TestMod().init();
        LOGGER.info("Paragon has been initialized");
    }
}