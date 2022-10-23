package com.kyanite.paragon.mixin;

import com.kyanite.paragon.api.ConfigRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.gui.GuiComponent.drawString;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Shadow @Final private boolean fading;

    @Shadow private long fadeInStart;

    @Inject(method = "render", at = @At("TAIL"))
    public void render(PoseStack poseStack, int i, int j, float f, CallbackInfo ci) {
        TitleScreen titleScreen = (TitleScreen) (Object)this;
        float g = this.fading ? (float)(Util.getMillis() - this.fadeInStart) / 1000.0F : 1.0F;
        float h = this.fading ? Mth.clamp(g - 1.0F, 0.0F, 1.0F) : 1.0F;
        int n = Mth.ceil(h * 255.0F) << 24;

        drawString(poseStack, Minecraft.getInstance().font, "Configs registered: " + ConfigRegistry.HOLDERS.stream().count(), 2, titleScreen.height - 20, 16777215 | n);
    }
}
