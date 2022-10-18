package com.kyanite.paragon.forge;

import com.kyanite.paragon.Paragon;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Paragon.MOD_ID)
public class ParagonForge {
    public ParagonForge() {
        Paragon.init();
    }
}