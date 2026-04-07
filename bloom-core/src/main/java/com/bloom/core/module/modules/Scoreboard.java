package com.bloom.core.module.modules;
import com.bloom.core.module.Module;

public class Scoreboard extends Module {
    public static boolean hideScoreboard = false;
    public Scoreboard() { super("Hide Scoreboard", "Hides the sidebar scoreboard", false); }
    @Override public void onEnable() { hideScoreboard = true; }
    @Override public void onDisable() { hideScoreboard = false; }
}
