package com.kyanite.paragon.fabric;

import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.api.ConfigManager;
import com.kyanite.paragon.api.enums.ConfigHandshakeResult;
import com.kyanite.paragon.api.enums.ConfigSide;
import com.kyanite.paragon.api.interfaces.Config;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.TextComponent;
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

            ConfigManager.getRegisteredConfigs().entrySet().stream().filter((configHolder -> configHolder.getKey().configSide() == ConfigSide.COMMON)).forEach((configHolder -> {
                try {
                    Paragon.LOGGER.info("Server sent config handshake for " + configHolder.getValue().getModId() + " to " + handler.player.getName().getString());
                    ServerPlayNetworking.send(handler.player, new ResourceLocation(Paragon.MOD_ID, "sync"),
                            PacketByteBufs.create()
                                    .writeUtf(configHolder.getValue().getModId())
                                    .writeUtf(configHolder.getValue().getFileName())
                                    .writeUtf(configHolder.getValue().getRaw())
                                    .writeUtf(configHolder.getKey().getSerializer().getSuffix()));
                } catch (IOException e) {
                    ConfigManager.unregister(configHolder.getValue().getModId(), configHolder.getKey().configSide());
                    Paragon.LOGGER.info("Unregistered" + configHolder.getValue().getModId() + " due to the config-file missing");
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
                        player.connection.disconnect(new TextComponent("Config is mismatched between server and client!"));
                    }

                    case ERROR -> {
                        Paragon.LOGGER.info("Handshake failed due to error");
                        player.connection.disconnect(new TextComponent("Unable to complete config handshake"));
                    }
                }
            });
        });
    }

    public void search() {
        FabricLoader.getInstance().getEntrypointContainers("config", Config.class).forEach(entrypoint -> {
            ConfigManager.register(entrypoint.getProvider().getMetadata().getId(), entrypoint.getEntrypoint());
        });
    }
}