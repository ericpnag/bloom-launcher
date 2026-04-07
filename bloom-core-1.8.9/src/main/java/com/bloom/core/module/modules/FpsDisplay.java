package com.bloom.core.module.modules;
import com.bloom.core.module.Module;
import net.minecraft.client.MinecraftClient;

public class FpsDisplay extends Module {
    public FpsDisplay() { super("FPS Display", "Show current FPS", true); }
    @Override public boolean hasHud() { return true; }
    @Override public void renderHud(MinecraftClient client, int y) {
        String text = MinecraftClient.getCurrentFps() + " FPS";
        int fps = MinecraftClient.getCurrentFps();
        int color = fps >= 60 ? 0x55DD88 : fps >= 30 ? 0xDDBB55 : 0xDD5566;
        client.inGameHud.fill(2, y - 1, client.textRenderer.getStringWidth(text) + 8, y + 10, 0x44000000);
        client.textRenderer.drawWithShadow(text, 6, y, color);
    }
}
