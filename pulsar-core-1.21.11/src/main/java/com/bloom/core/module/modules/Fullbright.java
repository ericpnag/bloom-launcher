package com.bloom.core.module.modules;

import com.bloom.core.module.Module;
import net.minecraft.client.MinecraftClient;

public class Fullbright extends Module {
    public static boolean active = false;

    public Fullbright() {
        super("Fullbright", "Night vision brightness", false);
    }

    @Override
    public void onEnable() { active = true; }

    @Override
    public void onDisable() { active = false; }
}
