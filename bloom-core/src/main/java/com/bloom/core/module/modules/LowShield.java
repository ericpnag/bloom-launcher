package com.bloom.core.module.modules;

import com.bloom.core.module.Module;

public class LowShield extends Module {
    public static boolean active = false;

    public LowShield() {
        super("Low Shield", "Lowers shield blocking overlay", false);
    }

    @Override public void onEnable() { active = true; }
    @Override public void onDisable() { active = false; }
    @Override public boolean hasHud() { return false; }
}
