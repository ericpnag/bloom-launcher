package com.bloom.core.module.modules;

import com.bloom.core.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;

public class CosmeticsHalo extends Module {
    public static boolean showHalo = false;
    public static int selectedHalo = 0; // 0=golden, 1=purple, 2=red, 3=blue, 4=green
    private int tickCount = 0;

    public static final String[] HALO_NAMES = {"Golden", "Void", "Inferno", "Frost", "Nature"};
    public static final int[][] HALO_COLORS = {
        {255, 255, 170},  // Golden
        {198, 120, 221},  // Purple
        {224, 108, 117},  // Red
        {97, 175, 239},   // Blue
        {152, 195, 121},  // Green
    };

    public CosmeticsHalo() {
        super("Pulsar Halo", "Show a glowing halo above your head", false);
    }

    @Override
    public void onEnable() { showHalo = true; }

    @Override
    public void onDisable() { showHalo = false; }

    @Override
    public void onTick(MinecraftClient client) {
        if (!showHalo || client.player == null || client.world == null) return;

        tickCount++;

        double px = client.player.getX();
        double py = client.player.getY() + 2.2;
        double pz = client.player.getZ();

        float radius = 0.4f;
        int particlesPerTick = 3;

        py += Math.sin(tickCount * 0.08) * 0.03;

        // Choose particle type based on halo color
        SimpleParticleType particleType = switch (selectedHalo) {
            case 1 -> ParticleTypes.WITCH;          // Purple
            case 2 -> ParticleTypes.LAVA;            // Red/orange
            case 3 -> ParticleTypes.SNOWFLAKE;       // Blue/ice
            case 4 -> ParticleTypes.HAPPY_VILLAGER;  // Green
            default -> ParticleTypes.END_ROD;        // Golden
        };

        for (int i = 0; i < particlesPerTick; i++) {
            double angle = ((tickCount * 3 + i * (360.0 / particlesPerTick)) % 360) * Math.PI / 180.0;
            double x = px + Math.cos(angle) * radius;
            double z = pz + Math.sin(angle) * radius;

            client.world.addParticleClient(particleType, x, py, z, 0.0, 0.005, 0.0);
        }

        if (tickCount % 10 == 0) {
            double sparkleAngle = Math.random() * Math.PI * 2;
            client.world.addParticleClient(
                ParticleTypes.ENCHANT,
                px + Math.cos(sparkleAngle) * radius * 0.8,
                py + 0.1,
                pz + Math.sin(sparkleAngle) * radius * 0.8,
                0.0, -0.02, 0.0
            );
        }
    }

    @Override
    public boolean hasHud() { return false; }
}
