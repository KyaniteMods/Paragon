package com.kyanite.paragon.forge;

import com.kyanite.paragon.Paragon;
import com.kyanite.paragon.api.ConfigRegistry;
import com.kyanite.paragon.api.enums.ConfigSide;
import com.kyanite.paragon.forge.packet.SyncPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod(Paragon.MOD_ID)
public class ParagonForge {
    public ParagonForge() {
        Paragon.init();
        ParagonPacketHandler.init();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventBusSubscriber(modid = Paragon.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
    public static class ParagonServer {
        @SubscribeEvent
        public static void loginEvent(PlayerEvent.PlayerLoggedInEvent event) {
            ConfigRegistry.HOLDERS.stream().filter((configHolder -> configHolder.configSide == ConfigSide.COMMON)).forEach((configHolder -> {
                try {
                    Paragon.LOGGER.info("Server sent config handshake for " + configHolder.getModId() + " to " + event.getEntity().getName().getString());
                    ParagonPacketHandler.sendToPlayer(new SyncPacket(configHolder.getModId(), configHolder.getRaw()), (ServerPlayer) event.getEntity());
                } catch (IOException e) {
                    ConfigRegistry.unregister(configHolder.getModId(), ConfigSide.COMMON);
                    Paragon.LOGGER.info("Unregistered" + configHolder.getModId() + " due to the config-file missing");
                }
            }));
        }
    }
}