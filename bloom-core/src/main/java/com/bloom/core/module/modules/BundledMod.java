package com.bloom.core.module.modules;

import com.bloom.core.module.Module;

public class BundledMod extends Module {
    public BundledMod(String name, String description) {
        super(name, description, true);
    }

    @Override public void onEnable() {} 
    @Override public void onDisable() {}
    @Override public boolean hasHud() { return false; }
}
