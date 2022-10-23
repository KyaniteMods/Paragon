package com.kyanite.paragon.forge.packet;

import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.api.enums.ConfigHandshakeResult;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class HandshakePacket {
    public final ConfigHandshakeResult handshakeResult;

    public HandshakePacket(ConfigHandshakeResult handshakeResult) {
        this.handshakeResult = handshakeResult;
    }

    public HandshakePacket(FriendlyByteBuf buffer) {
        this(buffer.readEnum(ConfigHandshakeResult.class));
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.handshakeResult);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            Paragon.LOGGER.info("Handshake response from " + player.getName().getString() + " : " + handshakeResult.toString());
            switch (handshakeResult)
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
            success.set(true);
        });

        ctx.get().setPacketHandled(true);
        return success.get();
    }
}