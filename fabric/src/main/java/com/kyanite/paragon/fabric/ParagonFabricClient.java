package com.kyanite.paragon.fabric;

import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.api.ConfigManager;
import com.kyanite.paragon.api.enums.ConfigHandshakeResult;
import com.kyanite.paragon.api.enums.ConfigSide;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.resources.ResourceLocation;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ParagonFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Handshake
        ClientPlayNetworking.registerGlobalReceiver(new ResourceLocation(Paragon.MOD_ID, "sync"),  (client, handler, buf, responseSender) -> {
            String id = buf.readUtf();
            String rawJson = buf.readUtf();
            String suffix = buf.readUtf();
            client.execute(() -> {
                Paragon.LOGGER.info("Received config handshake on client for " + id);

                try {
                    String rawJson2 = ConfigManager.getRawJson(ConfigManager.getFilePath(id, ConfigSide.COMMON, suffix));;
                    if(!rawJson.equals(rawJson2)) {
                        responseSender.sendPacket(new ResourceLocation(Paragon.MOD_ID, "handshake"), PacketByteBufs.create().writeEnum(ConfigHandshakeResult.FAIL));
                    }else{
                        responseSender.sendPacket(new ResourceLocation(Paragon.MOD_ID, "handshake"), PacketByteBufs.create().writeEnum(ConfigHandshakeResult.SUCCESS));
                    }
                } catch (FileNotFoundException e) {
                    if(ConfigManager.isRegistered(id, ConfigSide.COMMON)) {
                        ConfigManager.unregister(id, ConfigSide.COMMON);
                        Paragon.LOGGER.info("Unregistered" + id + " due to the config-file missing");
                    }
                    responseSender.sendPacket(new ResourceLocation(Paragon.MOD_ID, "handshake"), PacketByteBufs.create().writeEnum(ConfigHandshakeResult.ERROR));
                } catch (IOException e) {
                    responseSender.sendPacket(new ResourceLocation(Paragon.MOD_ID, "handshake"), PacketByteBufs.create().writeEnum(ConfigHandshakeResult.ERROR));
                    throw new RuntimeException(e);
                }
            });
        });
    }
}
