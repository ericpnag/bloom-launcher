package com.bloom.core.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public abstract class Module {
    private final String name;
    private final String description;
    private boolean enabled;

    public Module(String name, String description, boolean enabledByDefault) {
        this.name = name;
        this.description = description;
        this.enabled = enabledByDefault;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isEnabled() { return enabled; }

    public void toggle() {
        enabled = !enabled;
        if (enabled) onEnable(); else onDisable();
        // Auto-save config
        try {
            com.bloom.core.config.PulsarConfig.save(com.bloom.core.PulsarCore.MODULES);
        } catch (Exception ignored) {}
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) toggle();
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick(MinecraftClient client) {}
    public boolean hasHud() { return false; }
    public int getHudHeight() { return 12; }
    public void renderHud(DrawContext context, MinecraftClient client, int y) {}
}
