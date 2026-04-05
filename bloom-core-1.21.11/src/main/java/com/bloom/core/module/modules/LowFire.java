package com.bloom.core.module.modules;

import com.bloom.core.module.Module;

public class LowFire extends Module {
    public static boolean active = false;

    public LowFire() {
        super("Low Fire", "Lowers fire overlay on screen", false);
    }

    @Override public void onEnable() { active = true; }
    @Override public void onDisable() { active = false; }
    @Override public boolean hasHud() { return false; }
}
