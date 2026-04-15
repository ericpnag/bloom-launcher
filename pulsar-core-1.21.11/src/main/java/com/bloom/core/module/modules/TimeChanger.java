package com.bloom.core.module.modules;
import com.bloom.core.module.Module;
import net.minecraft.client.MinecraftClient;

public class TimeChanger extends Module {
    public static boolean active = false;
    public static long clientTime = 6000; // noon

    public TimeChanger() { super("Time Changer", "Set client-side time to day", false); }
    @Override public void onEnable() { active = true; }
    @Override public void onDisable() { active = false; }
}
