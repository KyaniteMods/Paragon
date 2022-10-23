package com.kyanite.paragon.forge.packet;

import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.api.ConfigRegistry;
import com.kyanite.paragon.api.ConfigUtils;
import com.kyanite.paragon.api.enums.ConfigHandshakeResult;
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

    public SyncPacket(String modId, String rawJson) {
        this.modId = modId;
        this.rawJson = rawJson;
    }

    public SyncPacket(FriendlyByteBuf buffer) {
        this(buffer.readUtf(), buffer.readUtf());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.modId);
        buffer.writeUtf(this.rawJson);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
        ctx.get().enqueueWork(() -> {
            Paragon.LOGGER.info("Received config handshake on client for " + modId);

            try {
                String rawJson2 = ConfigUtils.getRawJson(ConfigUtils.getFilePath(modId));;
                if(!rawJson.equals(rawJson2)) {
                    ParagonPacketHandler.sendToServer(new HandshakePacket(ConfigHandshakeResult.FAIL));
                }else{
                    ParagonPacketHandler.sendToServer(new HandshakePacket(ConfigHandshakeResult.SUCCESS));
                }
            } catch (FileNotFoundException e) {
                if(ConfigRegistry.isRegistered(modId)) {
                    ConfigRegistry.unregister(modId);
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