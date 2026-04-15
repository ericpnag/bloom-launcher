package com.bloom.core.mixin;

import com.bloom.core.module.modules.LowFire;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameOverlayRenderer.class)
public class LowFireMixin {
    @Inject(method = "renderFireOverlay", at = @At("HEAD"), cancellable = true)
    private static void bloomLowFire(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Sprite sprite, CallbackInfo ci) {
        if (LowFire.active) {
            // Push matrix, scale fire down to bottom of screen
            matrices.push();
            matrices.translate(0.0, -0.4, 0.0);   // push fire lower on screen
            matrices.scale(1.0f, 0.5f, 1.0f);     // shrink to 50% height
        }
    }

    @Inject(method = "renderFireOverlay", at = @At("RETURN"))
    private static void bloomLowFireEnd(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Sprite sprite, CallbackInfo ci) {
        if (LowFire.active) {
            matrices.pop();
        }
    }
}
