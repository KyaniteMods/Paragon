package com.kyanite.paragon.fabric;

import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.api.ConfigRegistry;
import com.kyanite.paragon.api.annotation.ModConfig;
import com.kyanite.paragon.api.enums.ConfigHandshakeResult;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class ParagonFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Paragon.init();
        search();

        // Run handshake on server join
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if(server.isSingleplayer()) {
                Paragon.LOGGER.info("Singleplayer server - Config handshake is unneccesary");
                return;
            }

            ConfigRegistry.HOLDERS.forEach((configHolder -> {
                try {
                    Paragon.LOGGER.info("Server sent config handshake for " + configHolder.getModId() + " to " + handler.player.getName().getString());
                    ServerPlayNetworking.send(handler.player, new ResourceLocation(Paragon.MOD_ID, "sync"),
                            PacketByteBufs.create()
                                    .writeUtf(configHolder.getModId())
                                    .writeUtf(configHolder.getRaw()));
                } catch (IOException e) {
                    ConfigRegistry.unregister(configHolder.getModId());
                    Paragon.LOGGER.info("Unregistered" + configHolder.getModId() + " due to the config-file missing");
                }
            }));
        });

        // Handshake response
        ServerPlayNetworking.registerGlobalReceiver(new ResourceLocation(Paragon.MOD_ID, "handshake"), (server, player, handler, buf, responseSender) -> {
            ConfigHandshakeResult result = buf.readEnum(ConfigHandshakeResult.class);
            server.execute(() -> {
                Paragon.LOGGER.info("Handshake response from " + player.getName().getString() + " : " + result.toString());
                switch (result)
                {
                    case SUCCESS -> {
                        Paragon.LOGGER.info("Handshake succeeded with no issues");
                    }

                    case FAIL -> {
                        Paragon.LOGGER.info("Handshake failed due to config mismatch");
                        player.connection.disconnect(Component.literal("Config is mismatched between server and client!"));
                    }

                    case ERROR -> {
                        Paragon.LOGGER.info("Handshake failed due to error");
                        player.connection.disconnect(Component.literal("Unable to complete config handshake"));
                    }
                }
            });
        });
    }

    public void search() {
        FabricLoader.getInstance().getEntrypointContainers("config", ModConfig.class).forEach(entrypoint -> {
            ConfigRegistry.register(entrypoint.getProvider().getMetadata().getId(), entrypoint.getEntrypoint());
        });
    }
}