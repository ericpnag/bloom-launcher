package com.bloom.core.module.modules;

import com.bloom.core.module.Module;
import net.minecraft.client.MinecraftClient;

public class PerformanceBoost extends Module {
    private int prevRenderDistance;
    private int prevSimulationDistance;
    private boolean prevEntityShadows;
    private boolean prevVsync;
    private int prevMaxFps;
    private double prevFovEffectScale;
    private int prevBiomeBlendRadius;

    public PerformanceBoost() {
        super("FPS Boost", "Optimizes settings for maximum FPS", false);
    }

    @Override
    public void onEnable() {
        MinecraftClient client = MinecraftClient.getInstance();
        var opts = client.options;

        // Save current settings
        prevRenderDistance = opts.getViewDistance().getValue();
        prevSimulationDistance = opts.getSimulationDistance().getValue();
        prevEntityShadows = opts.getEntityShadows().getValue();
        prevVsync = opts.getEnableVsync().getValue();
        prevMaxFps = opts.getMaxFps().getValue();
        prevFovEffectScale = opts.getFovEffectScale().getValue();
        prevBiomeBlendRadius = opts.getBiomeBlendRadius().getValue();

        // Apply performance settings
        if (prevRenderDistance > 12) opts.getViewDistance().setValue(12);
        if (prevSimulationDistance > 8) opts.getSimulationDistance().setValue(8);
        opts.getEntityShadows().setValue(false);
        opts.getEnableVsync().setValue(false);
        opts.getMaxFps().setValue(260);
        opts.getFovEffectScale().setValue(0.0);
        opts.getBiomeBlendRadius().setValue(0);

        // Force chunk reload
        if (client.worldRenderer != null) {
            client.worldRenderer.reload();
        }
    }

    @Override
    public void onDisable() {
        MinecraftClient client = MinecraftClient.getInstance();
        var opts = client.options;

        // Restore previous settings
        opts.getViewDistance().setValue(prevRenderDistance);
        opts.getSimulationDistance().setValue(prevSimulationDistance);
        opts.getEntityShadows().setValue(prevEntityShadows);
        opts.getEnableVsync().setValue(prevVsync);
        opts.getMaxFps().setValue(prevMaxFps);
        opts.getFovEffectScale().setValue(prevFovEffectScale);
        opts.getBiomeBlendRadius().setValue(prevBiomeBlendRadius);

        if (client.worldRenderer != null) {
            client.worldRenderer.reload();
        }
    }

    @Override
    public boolean hasHud() { return false; }
}
