package com.kyanite.paragon.fabric;

import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.api.ConfigRegistry;
import com.kyanite.paragon.api.annotation.ModConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class ParagonFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Paragon.init();
        search();
    }

    public void search() {
        FabricLoader.getInstance().getEntrypointContainers("config", ModConfig.class).forEach(entrypoint -> {
            ConfigRegistry.register(entrypoint.getProvider().getMetadata().getId(), entrypoint.getEntrypoint());
        });
    }
}