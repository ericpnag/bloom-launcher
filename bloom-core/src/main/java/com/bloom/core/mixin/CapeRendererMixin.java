package com.bloom.core.mixin;

import com.bloom.core.module.modules.CosmeticsCape;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public class CapeRendererMixin {
    @Unique
    private static boolean textureRegistered = false;
    @Unique
    private static final Identifier BLOOM_CAPE = Identifier.of("bloom-core", "textures/cape/bloom_cape.png");

    @Inject(method = "getSkinTextures", at = @At("RETURN"), cancellable = true)
    private void onGetSkinTextures(CallbackInfoReturnable<SkinTextures> cir) {
        if (!CosmeticsCape.showCape) return;
        AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) (Object) this;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && player.getUuid().equals(client.player.getUuid())) {
            // Register texture on first use
            if (!textureRegistered) {
                client.getTextureManager().registerTexture(BLOOM_CAPE, new ResourceTexture(BLOOM_CAPE));
                textureRegistered = true;
            }

            SkinTextures original = cir.getReturnValue();
            cir.setReturnValue(new SkinTextures(
                original.texture(),
                original.textureUrl(),
                BLOOM_CAPE,
                BLOOM_CAPE,
                original.model(),
                original.secure()
            ));
        }
    }
}
