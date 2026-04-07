package com.bloom.core.mixin;

import com.bloom.core.module.modules.LowFire;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameOverlayRenderer.class)
public class LowFireMixin {
    @Inject(method = "renderFireOverlay", at = @At("HEAD"), cancellable = true)
    private static void bloomLowFire(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
        if (LowFire.active) {
            matrices.push();
            matrices.translate(0.0, -0.4, 0.0);
            matrices.scale(1.0f, 0.5f, 1.0f);
        }
    }

    @Inject(method = "renderFireOverlay", at = @At("RETURN"))
    private static void bloomLowFireEnd(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
        if (LowFire.active) {
            matrices.pop();
        }
    }
}
