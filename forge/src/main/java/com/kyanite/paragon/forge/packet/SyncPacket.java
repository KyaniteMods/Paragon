package com.kyanite.paragon.forge.packet;

import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.api.ConfigManager;
import com.kyanite.paragon.api.enums.ConfigHandshakeResult;
import com.kyanite.paragon.api.enums.ConfigSide;
import com.kyanite.paragon.forge.ParagonPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class SyncPacket {
    public final String modId;
    public final String rawJson;
    public final String suffix;
    public SyncPacket(String modId, String rawJson, String suffix) {
        this.modId = modId;
        this.rawJson = rawJson;
        this.suffix = suffix;
    }

    public SyncPacket(FriendlyByteBuf buffer) {
        this(buffer.readUtf(), buffer.readUtf(), buffer.readUtf());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.modId);
        buffer.writeUtf(this.rawJson);
        buffer.writeUtf(this.suffix);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
        ctx.get().enqueueWork(() -> {
            Paragon.LOGGER.info("Received config handshake on client for " + modId);

            try {
                String rawJson2 = ConfigManager.getRawJson(ConfigManager.getFilePath(modId, ConfigSide.COMMON, suffix));;
                if(!rawJson.equals(rawJson2)) {
                    ParagonPacketHandler.sendToServer(new HandshakePacket(ConfigHandshakeResult.FAIL));
                }else{
                    ParagonPacketHandler.sendToServer(new HandshakePacket(ConfigHandshakeResult.SUCCESS));
                }
            } catch (FileNotFoundException e) {
                if(ConfigManager.isRegistered(modId, ConfigSide.COMMON)) {
                    ConfigManager.unregister(modId, ConfigSide.COMMON);
                    Paragon.LOGGER.info("Unregistered" + modId + " due to the config-file missing");
                }

                ParagonPacketHandler.sendToServer(new HandshakePacket(ConfigHandshakeResult.ERROR));
            } catch (IOException e) {
                ParagonPacketHandler.sendToServer(new HandshakePacket(ConfigHandshakeResult.ERROR));
                throw new RuntimeException(e);
            }
            success.set(true);
        });

        ctx.get().setPacketHandled(true);
        return success.get();
    }
}