package com.kyanite.paragon.forge;

import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.forge.packet.HandshakePacket;
import com.kyanite.paragon.forge.packet.SyncPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ParagonPacketHandler {
    private static SimpleChannel INSTANCE;

    public static void init() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(Paragon.MOD_ID, "main"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        int index = 0;

        INSTANCE.messageBuilder(HandshakePacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(HandshakePacket::encode).decoder(HandshakePacket::new)
                .consumer(HandshakePacket::handle).add();

        INSTANCE.messageBuilder(SyncPacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncPacket::encode).decoder(SyncPacket::new)
                .consumer(SyncPacket::handle).add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
