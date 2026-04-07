package com.bloom.core.module.modules;
import com.bloom.core.module.Module;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Keystrokes extends Module {
    public Keystrokes() { super("Keystrokes", "Show WASD keys", true); }
    @Override public boolean hasHud() { return true; }
    @Override public int getHudHeight() { return 44; }
    @Override public void renderHud(MinecraftClient client, int y) {
        int bx = 4, by = y + 2;
        drawKey(client, "W", bx + 12, by, Keyboard.isKeyDown(Keyboard.KEY_W));
        drawKey(client, "A", bx, by + 14, Keyboard.isKeyDown(Keyboard.KEY_A));
        drawKey(client, "S", bx + 12, by + 14, Keyboard.isKeyDown(Keyboard.KEY_S));
        drawKey(client, "D", bx + 24, by + 14, Keyboard.isKeyDown(Keyboard.KEY_D));
        drawKey(client, "L", bx + 4, by + 28, Mouse.isButtonDown(0));
        drawKey(client, "R", bx + 20, by + 28, Mouse.isButtonDown(1));
    }
    private void drawKey(MinecraftClient client, String key, int x, int y, boolean pressed) {
        client.inGameHud.fill(x, y, x + 11, y + 12, pressed ? 0x88FFB0C0 : 0x44000000);
        int tw = client.textRenderer.getStringWidth(key);
        client.textRenderer.drawWithShadow(key, x + (11 - tw) / 2f, y + 2, pressed ? 0xFFFFFF : 0x888888);
    }
}
