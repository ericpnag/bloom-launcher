package com.bloom.core.mixin;

import com.bloom.core.module.modules.Freelook;
import net.minecraft.entity.Entity;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * When freelook is active, intercept mouse look changes so
 * they rotate the camera but NOT the player body.
 * Target Entity base class which has changeLookDirection in all versions.
 */
@Mixin(Entity.class)
public class FreelookMixin {
    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void bloomFreelook(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
        Entity self = (Entity)(Object) this;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.player != self) return;

        if (Freelook.looking) {
            float sensitivity = 0.15f;
            Freelook.cameraYaw += (float)(cursorDeltaX * sensitivity);
            Freelook.cameraPitch = Math.max(-90, Math.min(90,
                Freelook.cameraPitch - (float)(cursorDeltaY * sensitivity)));
            ci.cancel();
        }
    }
}
