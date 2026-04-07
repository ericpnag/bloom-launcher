package com.bloom.core.module.modules;
import com.bloom.core.module.Module;
import net.minecraft.client.MinecraftClient;

public class Coordinates extends Module {
    public Coordinates() { super("Coordinates", "Show XYZ on screen", true); }
    @Override public boolean hasHud() { return true; }
    @Override public void renderHud(MinecraftClient client, int y) {
        if (client.player == null) return;
        String coords = String.format("%.0f / %.0f / %.0f", client.player.x, client.player.y, client.player.z);
        client.inGameHud.fill(2, y - 1, client.textRenderer.getStringWidth(coords) + 8, y + 10, 0x44000000);
        client.textRenderer.drawWithShadow(coords, 6, y, 0xCCBBAA);
    }
}
