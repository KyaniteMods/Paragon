package com.kyanite.paragon.fabric;

import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.api.ConfigRegistry;
import com.kyanite.paragon.api.ConfigUtils;
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
            client.execute(() -> {
                Paragon.LOGGER.info("Received config handshake on client for " + id);

                try {
                    String rawJson2 = ConfigUtils.getRawJson(ConfigUtils.getFilePath(id, ConfigSide.COMMON));;
                    if(!rawJson.equals(rawJson2)) {
                        responseSender.sendPacket(new ResourceLocation(Paragon.MOD_ID, "handshake"), PacketByteBufs.create().writeEnum(ConfigHandshakeResult.FAIL));
                    }else{
                        responseSender.sendPacket(new ResourceLocation(Paragon.MOD_ID, "handshake"), PacketByteBufs.create().writeEnum(ConfigHandshakeResult.SUCCESS));
                    }
                } catch (FileNotFoundException e) {
                    if(ConfigRegistry.isRegistered(id, ConfigSide.COMMON)) {
                        ConfigRegistry.unregister(id, ConfigSide.COMMON);
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
