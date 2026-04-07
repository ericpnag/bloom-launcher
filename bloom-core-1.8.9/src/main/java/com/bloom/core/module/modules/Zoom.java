package com.bloom.core.module.modules;
import com.bloom.core.module.Module;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.input.Keyboard;

public class Zoom extends Module {
    public static boolean zooming = false;
    private float prevFov = 70f;
    public Zoom() { super("Zoom", "Hold C to zoom in", true); }
    @Override public void onTick(MinecraftClient client) {
        if (client.player == null) return;
        boolean held = Keyboard.isKeyDown(Keyboard.KEY_C);
        if (held && !zooming) {
            zooming = true;
            prevFov = client.options.fov;
            client.options.fov = 20f;
        } else if (!held && zooming) {
            zooming = false;
            client.options.fov = prevFov;
        }
    }
}
