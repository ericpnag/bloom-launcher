package com.bloom.core.module.modules;
import com.bloom.core.module.Module;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.input.Mouse;
import java.util.ArrayList;
import java.util.List;

public class CpsCounter extends Module {
    private final List<Long> clicks = new ArrayList<>();
    private boolean wasPressed = false;
    public CpsCounter() { super("CPS Counter", "Show clicks per second", true); }
    @Override public void onTick(MinecraftClient client) {
        boolean pressed = Mouse.isButtonDown(0);
        if (pressed && !wasPressed) clicks.add(System.currentTimeMillis());
        wasPressed = pressed;
        long now = System.currentTimeMillis();
        clicks.removeIf(t -> now - t > 1000);
    }
    @Override public boolean hasHud() { return true; }
    @Override public void renderHud(MinecraftClient client, int y) {
        String text = clicks.size() + " CPS";
        client.inGameHud.fill(2, y - 1, client.textRenderer.getStringWidth(text) + 8, y + 10, 0x44000000);
        client.textRenderer.drawWithShadow(text, 6, y, 0xFFB7C9);
    }
}
