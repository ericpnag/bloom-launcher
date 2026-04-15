package com.bloom.core.module.modules;
import com.bloom.core.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

public class ToggleSprint extends Module {
    private boolean sprinting = false;
    public ToggleSprint() { super("Toggle Sprint", "Auto-sprint without holding key", true); }
    @Override public void onTick(MinecraftClient client) {
        if (client.player == null) return;
        if (client.player.input.movementForward > 0 && !client.player.isSneaking()) {
            client.player.setSprinting(true);
            sprinting = true;
        } else { sprinting = false; }
    }
    @Override public boolean hasHud() { return true; }
    @Override public void renderHud(MinecraftClient client, int y) {
        String text = sprinting ? "Sprinting" : "Sprint: ON";
        client.inGameHud.fill(2, y - 1, client.textRenderer.getStringWidth(text) + 8, y + 10, 0x44000000);
        client.textRenderer.drawWithShadow(text, 6, y, sprinting ? 0xFFB7C9 : 0x887778);
    }
}
