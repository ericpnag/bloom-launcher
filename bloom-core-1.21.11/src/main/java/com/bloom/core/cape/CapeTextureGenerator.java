package com.bloom.core.cape;

import net.minecraft.client.texture.NativeImage;

/**
 * Generates detailed cape textures at 8x resolution (512x256).
 * Cape UV layout scaled 8x:
 *   Front face: x:8-87, y:8-135 (80x128)
 *   Back face:  x:96-175, y:8-135 (80x128)
 *   Left edge:  x:0-7, y:8-135
 *   Right edge: x:88-95, y:8-135
 *   Top strip:  x:8-87, y:0-7
 */
public class CapeTextureGenerator {

    private static final int SCALE = 8;
    private static final int FW = 10 * SCALE;   // 80
    private static final int FH = 16 * SCALE;   // 128
    private static final int FX = 1 * SCALE;    // 8
    private static final int FY = 1 * SCALE;    // 8
    private static final int BX = 12 * SCALE;   // 96
    private static final int BY = 1 * SCALE;    // 8
    private static final int DEPTH = 1 * SCALE; // 8

    public static int faceWidth() { return FW; }
    public static int faceHeight() { return FH; }

    public static void generateBase(NativeImage img, String capeFile) {
        switch (capeFile) {
            case "bloom_cape.png" -> generateCherryBlossom(img);
            case "midnight_cape.png" -> generateMidnight(img);
            case "frost_cape.png" -> generateFrost(img);
            case "flame_cape.png" -> generateFlame(img);
            case "ocean_cape.png" -> generateOcean(img);
            case "emerald_cape.png" -> generateEmerald(img);
            case "sunset_cape.png" -> generateSunset(img);
            case "galaxy_cape.png" -> generateGalaxy(img);
            case "void_cape.png" -> generateVoid(img);
            case "lightning_cape.png" -> generateLightning(img);
            case "blood_cape.png" -> generateBlood(img);
            case "arctic_cape.png" -> generateArctic(img);
            case "phantom_cape.png" -> generatePhantom(img);
            case "neon_cape.png" -> generateNeon(img);
            case "lava_cape.png" -> generateLava(img);
            case "sakura_cape.png" -> generateSakura(img);
            case "storm_cape.png" -> generateStorm(img);
            case "solar_cape.png" -> generateSolar(img);
            case "amethyst_cape.png" -> generateAmethyst(img);
            case "inferno_cape.png" -> generateInferno(img);
            case "drift_cape.png" -> generateDrift(img);
            case "obsidian_cape.png" -> generateObsidian(img);
            case "blackhole_cape.png" -> generateBlackHole(img);
        }
    }

    // Write to front face and back face
    static void fillFace(NativeImage img, int lx, int ly, int argb) {
        if (lx < 0 || lx >= FW || ly < 0 || ly >= FH) return;
        img.setColorArgb(FX + lx, FY + ly, argb);
        img.setColorArgb(BX + lx, BY + ly, argb);
    }

    static void blendFace(NativeImage img, int lx, int ly, int color, float alpha) {
        if (lx < 0 || lx >= FW || ly < 0 || ly >= FH) return;
        blend(img, FX + lx, FY + ly, color, alpha);
        blend(img, BX + lx, BY + ly, color, alpha);
    }

    private static void fillEdges(NativeImage img, int ly, int argb) {
        if (ly < 0 || ly >= FH) return;
        for (int d = 0; d < DEPTH; d++) {
            img.setColorArgb(d, FY + ly, argb);
            img.setColorArgb(FX + FW + d, FY + ly, argb);
        }
    }

    private static void fillTop(NativeImage img, int lx, int argb) {
        if (lx < 0 || lx >= FW) return;
        for (int d = 0; d < DEPTH; d++) {
            img.setColorArgb(FX + lx, d, argb);
        }
    }

    // ========== PERLIN-ISH NOISE HELPER ==========
    private static float noise2D(float x, float y) {
        int ix = (int) Math.floor(x), iy = (int) Math.floor(y);
        float fx = x - ix, fy = y - iy;
        fx = fx * fx * (3 - 2 * fx);
        fy = fy * fy * (3 - 2 * fy);
        float a = pseudoRand(ix, iy), b = pseudoRand(ix + 1, iy);
        float c = pseudoRand(ix, iy + 1), d = pseudoRand(ix + 1, iy + 1);
        return lerpF(lerpF(a, b, fx), lerpF(c, d, fx), fy);
    }

    private static float pseudoRand(int x, int y) {
        int n = x + y * 57;
        n = (n << 13) ^ n;
        return (1.0f - ((n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0f) * 0.5f + 0.5f;
    }

    private static float fbm(float x, float y, int octaves) {
        float value = 0, amp = 0.5f, freq = 1;
        for (int i = 0; i < octaves; i++) {
            value += noise2D(x * freq, y * freq) * amp;
            amp *= 0.5f;
            freq *= 2;
        }
        return value;
    }

    // ========== CHERRY BLOSSOM ==========
    private static void generateCherryBlossom(NativeImage img) {
        // Rich pink gradient background with subtle texture
        for (int y = 0; y < FH; y++) {
            float t = (float) y / (FH - 1);
            for (int x = 0; x < FW; x++) {
                float n = fbm(x * 0.05f, y * 0.05f, 3) * 0.08f;
                float tt = Math.max(0, Math.min(1, t + n));
                int r = lerp(248, 195, tt), g = lerp(185, 110, tt), b = lerp(205, 140, tt);
                fillFace(img, x, y, argb(255, r, g, b));
            }
            int ec = argb(255, lerp(245, 195, t), lerp(178, 110, t), lerp(200, 140, t));
            fillEdges(img, y, ec);
        }
        for (int x = 0; x < FW; x++) fillTop(img, x, argb(255, 248, 185, 205));

        // Branch with bark texture (diagonal across cape)
        drawBranch(img, -5, 45, 85, 25, 3.0f);
        drawBranch(img, 20, 100, 65, 70, 2.5f);
        // Smaller twigs
        drawBranch(img, 30, 30, 50, 15, 1.5f);
        drawBranch(img, 55, 75, 75, 55, 1.5f);

        // Large detailed cherry blossoms at key positions
        drawFlower(img, 16, 20, 18, 0.0f);
        drawFlower(img, 56, 12, 20, 0.4f);
        drawFlower(img, 34, 55, 19, 0.7f);
        drawFlower(img, 64, 70, 16, 0.2f);
        drawFlower(img, 12, 90, 17, 0.9f);
        drawFlower(img, 50, 105, 15, 0.5f);
        drawFlower(img, 70, 40, 13, 0.1f);

        // Medium flowers
        drawFlower(img, 8, 50, 11, 0.3f);
        drawFlower(img, 40, 30, 12, 0.6f);
        drawFlower(img, 72, 95, 10, 0.8f);
        drawFlower(img, 25, 80, 11, 0.15f);

        // Small buds scattered
        drawBud(img, 5, 10, 5);
        drawBud(img, 40, 8, 4);
        drawBud(img, 75, 25, 5);
        drawBud(img, 6, 70, 4);
        drawBud(img, 60, 45, 5);
        drawBud(img, 20, 115, 4);
        drawBud(img, 68, 115, 5);
        drawBud(img, 45, 120, 3);

        // Falling petal shapes (static base for animation to move)
        for (int i = 0; i < 12; i++) {
            int px = (int)(pseudoRand(i * 7, 42) * FW);
            int py = (int)(pseudoRand(i * 13, 17) * FH);
            float rot = pseudoRand(i * 3, 99) * 6.28f;
            drawPetal(img, px, py, 4 + (int)(pseudoRand(i, 5) * 3), rot, 0.4f);
        }

        // Soft vignette darkening at edges
        applyVignette(img, argb(255, 160, 80, 100), 0.15f);
    }

    private static void drawBranch(NativeImage img, int x0, int y0, int x1, int y1, float thickness) {
        int barkDark = argb(255, 95, 60, 40);
        int barkLight = argb(255, 130, 85, 55);
        float dx = x1 - x0, dy = y1 - y0;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        int steps = (int) len;
        for (int i = 0; i <= steps; i++) {
            float t = (float) i / steps;
            int px = (int)(x0 + dx * t);
            int py = (int)(y0 + dy * t);
            float w = thickness * (1.0f - t * 0.4f);
            for (int oy = (int)-w - 1; oy <= (int) w + 1; oy++) {
                for (int ox = (int)-w - 1; ox <= (int) w + 1; ox++) {
                    float d = (float) Math.sqrt(ox * ox + oy * oy);
                    if (d <= w) {
                        float edge = d / w;
                        int c = edge > 0.6f ? barkDark : barkLight;
                        blendFace(img, px + ox, py + oy, c, 0.85f * (1 - edge * 0.3f));
                    }
                }
            }
        }
    }

    private static void drawFlower(NativeImage img, int cx, int cy, int size, float rotOffset) {
        // Petal colors with more variation
        int petalLight = argb(255, 255, 220, 232);
        int petalMid = argb(255, 252, 198, 215);
        int petalDark = argb(255, 242, 175, 198);
        int petalDeep = argb(255, 228, 150, 178);
        int centerColor = argb(255, 255, 248, 210);
        int stamenColor = argb(255, 200, 135, 105);
        int stamenTip = argb(255, 220, 160, 60);

        // 5 petals with rounded heart-like shape
        for (int p = 0; p < 5; p++) {
            double angle = rotOffset + p * Math.PI * 2.0 / 5.0 - Math.PI / 2.0;
            float pcx = cx + (float)(Math.cos(angle) * size * 0.40);
            float pcy = cy + (float)(Math.sin(angle) * size * 0.40);
            float pLen = size * 0.58f;
            float pWid = size * 0.38f;

            for (int py = (int)(pcy - pLen - 2); py <= (int)(pcy + pLen + 2); py++) {
                for (int px = (int)(pcx - pLen - 2); px <= (int)(pcx + pLen + 2); px++) {
                    float ddx = px - pcx, ddy = py - pcy;
                    float cos = (float) Math.cos(-angle), sin = (float) Math.sin(-angle);
                    float lx = ddx * cos - ddy * sin;
                    float ly = ddx * sin + ddy * cos;
                    float ex = lx / pLen, ey = ly / pWid;
                    float dist = ex * ex + ey * ey;

                    if (dist <= 1.0f) {
                        // Petal notch at tip (heart shape)
                        if (ex > 0.55f && Math.abs(ey) < 0.10f) continue;

                        float fromCenter = (float) Math.sqrt((px - cx) * (px - cx) + (py - cy) * (py - cy)) / size;
                        // Gradient from center outward with vein lines
                        float vein = (float)(Math.sin(lx * 2.5f) * 0.5 + 0.5) * 0.15f;
                        int color;
                        if (dist > 0.85f) color = petalDeep;
                        else if (dist > 0.6f) color = petalDark;
                        else if (fromCenter < 0.25f) color = petalLight;
                        else color = petalMid;

                        float alpha = dist > 0.9f ? 0.7f : 0.92f;
                        blendFace(img, px, py, color, alpha);
                        // Subtle vein overlay
                        if (vein > 0.1f && dist < 0.8f) {
                            blendFace(img, px, py, petalDeep, vein * 0.3f);
                        }
                    }
                }
            }
        }

        // Center cluster
        int cRad = Math.max(3, size / 4);
        for (int dy = -cRad; dy <= cRad; dy++) {
            for (int dx = -cRad; dx <= cRad; dx++) {
                float d = (float) Math.sqrt(dx * dx + dy * dy);
                if (d <= cRad) {
                    float edge = d / cRad;
                    int c = edge > 0.6f ? argb(255, 245, 235, 195) : centerColor;
                    fillFace(img, cx + dx, cy + dy, c);
                }
            }
        }

        // Stamens radiating outward
        for (int s = 0; s < 7; s++) {
            double sa = rotOffset + s * Math.PI * 2.0 / 7.0 + Math.PI / 7.0;
            for (int i = cRad; i < cRad + size / 3; i++) {
                int sx = cx + (int)(Math.cos(sa) * i);
                int sy = cy + (int)(Math.sin(sa) * i);
                float t = (float)(i - cRad) / (size / 3.0f);
                blendFace(img, sx, sy, stamenColor, 0.7f * (1 - t * 0.5f));
            }
            // Stamen tip (pollen dot)
            int tipX = cx + (int)(Math.cos(sa) * (cRad + size / 3));
            int tipY = cy + (int)(Math.sin(sa) * (cRad + size / 3));
            fillFace(img, tipX, tipY, stamenTip);
            fillFace(img, tipX + 1, tipY, stamenTip);
            fillFace(img, tipX, tipY + 1, stamenTip);
        }
    }

    private static void drawBud(NativeImage img, int cx, int cy, int size) {
        // Teardrop bud shape
        for (int dy = -size; dy <= size + 2; dy++) {
            float w = dy < 0 ? size * (1 - (float) Math.abs(dy) / size) : size * (1 - (float) dy / (size + 2));
            for (int dx = (int)-w; dx <= (int) w; dx++) {
                float d = (float) Math.abs(dx) / Math.max(1, w);
                int color = dy < -size / 2 ? argb(255, 255, 208, 220) :
                    dy < 0 ? argb(255, 248, 190, 210) :
                        argb(255, 235, 165, 185);
                blendFace(img, cx + dx, cy + dy, color, 0.8f * (1 - d * 0.3f));
            }
        }
        // Calyx (green base)
        for (int dx = -2; dx <= 2; dx++) {
            blendFace(img, cx + dx, cy + size + 1, argb(255, 100, 155, 80), 0.7f);
            blendFace(img, cx + dx, cy + size + 2, argb(255, 85, 140, 65), 0.5f);
        }
    }

    private static void drawPetal(NativeImage img, int cx, int cy, int size, float rot, float alpha) {
        // Single loose petal shape
        int color = argb(255, 255, 210, 225);
        for (int dy = -size; dy <= size; dy++) {
            for (int dx = -size; dx <= size; dx++) {
                float cos = (float) Math.cos(-rot), sin = (float) Math.sin(-rot);
                float lx = dx * cos - dy * sin;
                float ly = dx * sin + dy * cos;
                float ex = lx / size, ey = ly / (size * 0.55f);
                float dist = ex * ex + ey * ey;
                if (dist <= 1.0f && dist > 0.1f) {
                    blendFace(img, cx + dx, cy + dy, color, alpha * (1 - dist * 0.5f));
                }
            }
        }
    }

    // ========== MIDNIGHT ==========
    private static void generateMidnight(NativeImage img) {
        // Deep night sky gradient with noise texture
        for (int y = 0; y < FH; y++) {
            float t = (float) y / (FH - 1);
            for (int x = 0; x < FW; x++) {
                float n = fbm(x * 0.04f, y * 0.04f, 3) * 0.06f;
                float tt = Math.max(0, Math.min(1, t + n));
                int r = lerp(22, 5, tt), g = lerp(14, 2, tt), b = lerp(48, 14, tt);
                fillFace(img, x, y, argb(255, r, g, b));
            }
            fillEdges(img, y, argb(255, lerp(22, 5, t), lerp(14, 2, t), lerp(48, 14, t)));
        }
        for (int x = 0; x < FW; x++) fillTop(img, x, argb(255, 22, 14, 48));

        // Large crescent moon with glow halo
        int moonCx = 58, moonCy = 20, moonR = 14;
        // Outer glow
        for (int dy = -moonR - 8; dy <= moonR + 8; dy++) {
            for (int dx = -moonR - 8; dx <= moonR + 8; dx++) {
                float d = (float) Math.sqrt(dx * dx + dy * dy);
                if (d > moonR && d < moonR + 8) {
                    float glow = 1.0f - (d - moonR) / 8.0f;
                    blendFace(img, moonCx + dx, moonCy + dy, argb(255, 200, 195, 150), glow * 0.2f);
                }
            }
        }
        // Moon body (crescent)
        for (int dy = -moonR; dy <= moonR; dy++) {
            for (int dx = -moonR; dx <= moonR; dx++) {
                float d1 = (float) Math.sqrt(dx * dx + dy * dy);
                float d2 = (float) Math.sqrt((dx - 5) * (dx - 5) + (dy - 2) * (dy - 2));
                if (d1 <= moonR && d2 > moonR - 2.5f) {
                    float bright = 1.0f - d1 / moonR * 0.2f;
                    // Surface texture
                    float tex = fbm(dx * 0.15f, dy * 0.15f, 2) * 0.08f;
                    bright = Math.max(0.6f, bright - tex);
                    fillFace(img, moonCx + dx, moonCy + dy,
                        argb(255, (int)(255 * bright), (int)(250 * bright), (int)(205 * bright)));
                }
            }
        }

        // 4-point stars with cross-flare effect (larger at 8x)
        drawCrossFlare(img, 12, 12, 6, argb(255, 225, 225, 255));
        drawCrossFlare(img, 36, 28, 5, argb(255, 210, 210, 248));
        drawCrossFlare(img, 68, 50, 7, argb(255, 235, 225, 255));
        drawCrossFlare(img, 16, 78, 5, argb(255, 215, 215, 252));
        drawCrossFlare(img, 52, 100, 6, argb(255, 230, 230, 255));
        drawCrossFlare(img, 72, 110, 4, argb(255, 205, 205, 245));

        // Medium stars (simple 4-point)
        drawStar(img, 30, 8, 4, argb(255, 200, 200, 240));
        drawStar(img, 48, 18, 3, argb(255, 195, 195, 235));
        drawStar(img, 8, 42, 4, argb(255, 210, 210, 248));
        drawStar(img, 44, 60, 3, argb(255, 200, 200, 240));
        drawStar(img, 24, 95, 4, argb(255, 215, 215, 250));

        // Small dot stars scattered
        int[][] smalls = {
            {5, 5}, {20, 4}, {42, 6}, {75, 8}, {8, 25}, {50, 30}, {70, 35},
            {4, 40}, {25, 48}, {60, 42}, {35, 55}, {70, 58}, {10, 65},
            {45, 72}, {65, 75}, {20, 85}, {55, 88}, {75, 92}, {8, 100},
            {35, 105}, {62, 108}, {15, 115}, {48, 118}, {72, 120},
            {30, 122}, {58, 125}
        };
        for (int[] s : smalls) {
            float bright = pseudoRand(s[0], s[1]);
            int c = argb(255, 150 + (int)(bright * 80), 150 + (int)(bright * 70), 200 + (int)(bright * 55));
            fillFace(img, s[0], s[1], c);
            // Tiny glow around brighter ones
            if (bright > 0.6f) {
                blendFace(img, s[0] + 1, s[1], c, 0.3f);
                blendFace(img, s[0] - 1, s[1], c, 0.3f);
                blendFace(img, s[0], s[1] + 1, c, 0.3f);
                blendFace(img, s[0], s[1] - 1, c, 0.3f);
            }
        }

        // Aurora band at bottom with multiple color layers
        for (int y = FH - 24; y < FH; y++) {
            float s = (float)(y - (FH - 24)) / 24.0f;
            for (int x = 0; x < FW; x++) {
                float wave1 = (float)(Math.sin(x * 0.12 + y * 0.06) * 0.5 + 0.5);
                float wave2 = (float)(Math.sin(x * 0.08 - y * 0.04 + 2) * 0.5 + 0.5);
                // Purple-green aurora
                blendFace(img, x, y, argb(255, 70, 20, 110), s * wave1 * 0.3f);
                blendFace(img, x, y, argb(255, 30, 100, 80), s * wave2 * 0.15f);
            }
        }
    }

    private static void drawCrossFlare(NativeImage img, int cx, int cy, int size, int color) {
        // Bright center
        fillFace(img, cx, cy, color);
        int r = (color >> 16) & 0xFF, g = (color >> 8) & 0xFF, b = color & 0xFF;
        // Diagonal flares (X shape)
        for (int i = 1; i <= size; i++) {
            float fade = 1.0f - (float) i / (size + 1);
            int dim = argb((int)(255 * fade), r, g, b);
            // Cardinal
            blendFace(img, cx + i, cy, dim, fade * 0.9f);
            blendFace(img, cx - i, cy, dim, fade * 0.9f);
            blendFace(img, cx, cy + i, dim, fade * 0.9f);
            blendFace(img, cx, cy - i, dim, fade * 0.9f);
            // Diagonal (shorter, dimmer)
            if (i <= size * 2 / 3) {
                float dfade = fade * 0.4f;
                blendFace(img, cx + i, cy + i, dim, dfade);
                blendFace(img, cx - i, cy + i, dim, dfade);
                blendFace(img, cx + i, cy - i, dim, dfade);
                blendFace(img, cx - i, cy - i, dim, dfade);
            }
        }
        // Core glow (3x3 bright center)
        for (int dy = -1; dy <= 1; dy++)
            for (int dx = -1; dx <= 1; dx++)
                if (dx != 0 || dy != 0)
                    blendFace(img, cx + dx, cy + dy, color, 0.5f);
    }

    private static void drawStar(NativeImage img, int cx, int cy, int size, int color) {
        fillFace(img, cx, cy, color);
        int dim = argb(180, (color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF);
        for (int i = 1; i <= size; i++) {
            float fade = 1.0f - (float) i / (size + 1);
            blendFace(img, cx + i, cy, dim, fade);
            blendFace(img, cx - i, cy, dim, fade);
            blendFace(img, cx, cy + i, dim, fade);
            blendFace(img, cx, cy - i, dim, fade);
        }
    }

    // ========== FROST ==========
    private static void generateFrost(NativeImage img) {
        // Ice-blue gradient with crystalline noise
        for (int y = 0; y < FH; y++) {
            float t = (float) y / (FH - 1);
            for (int x = 0; x < FW; x++) {
                float n = fbm(x * 0.06f, y * 0.06f, 3) * 0.08f;
                float tt = Math.max(0, Math.min(1, t + n));
                int r = lerp(165, 85, tt), g = lerp(218, 170, tt), b = lerp(255, 238, tt);
                fillFace(img, x, y, argb(255, r, g, b));
            }
            fillEdges(img, y, argb(255, lerp(165, 85, t), lerp(218, 170, t), lerp(255, 238, t)));
        }
        for (int x = 0; x < FW; x++) fillTop(img, x, argb(255, 165, 218, 255));

        // Frost creep pattern (Voronoi-like crystal boundaries)
        for (int y = 0; y < FH; y++) {
            for (int x = 0; x < FW; x++) {
                float v = voronoi(x * 0.08f, y * 0.08f);
                if (v < 0.12f) {
                    blendFace(img, x, y, argb(255, 230, 245, 255), 0.6f * (1 - v / 0.12f));
                }
            }
        }

        // Large detailed snowflakes
        drawSnowflake(img, 20, 22, 16);
        drawSnowflake(img, 58, 16, 18);
        drawSnowflake(img, 38, 60, 14);
        drawSnowflake(img, 14, 95, 12);
        drawSnowflake(img, 65, 85, 16);
        drawSnowflake(img, 40, 110, 10);

        // Ice crystal clusters (small hexagonal formations)
        drawIceCrystal(img, 8, 50, 6);
        drawIceCrystal(img, 72, 40, 5);
        drawIceCrystal(img, 30, 85, 5);
        drawIceCrystal(img, 55, 115, 4);

        // Frost edge glow (neon blue at borders)
        for (int y = 0; y < FH; y++) {
            float edgeDist = Math.min(y, FH - 1 - y) / (float) FH;
            float xEdge = Math.min(0, 0) / (float) FW; // top/bottom edges
            if (edgeDist < 0.06f) {
                float glow = (1 - edgeDist / 0.06f) * 0.25f;
                for (int x = 0; x < FW; x++) {
                    blendFace(img, x, y, argb(255, 200, 240, 255), glow);
                }
            }
        }
        for (int x = 0; x < FW; x++) {
            float edgeDist = Math.min(x, FW - 1 - x) / (float) FW;
            if (edgeDist < 0.06f) {
                float glow = (1 - edgeDist / 0.06f) * 0.2f;
                for (int y = 0; y < FH; y++) {
                    blendFace(img, x, y, argb(255, 200, 240, 255), glow);
                }
            }
        }
    }

    private static float voronoi(float x, float y) {
        int ix = (int) Math.floor(x), iy = (int) Math.floor(y);
        float minDist = 10;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                float px = ix + dx + pseudoRand(ix + dx, iy + dy);
                float py = iy + dy + pseudoRand(iy + dy + 50, ix + dx + 50);
                float d = (float) Math.sqrt((x - px) * (x - px) + (y - py) * (y - py));
                if (d < minDist) minDist = d;
            }
        }
        return minDist;
    }

    private static void drawSnowflake(NativeImage img, int cx, int cy, int size) {
        int bright = argb(255, 240, 250, 255);
        int mid = argb(255, 220, 240, 252);
        int dim = argb(255, 200, 230, 248);
        // Center crystal
        for (int dy = -2; dy <= 2; dy++)
            for (int dx = -2; dx <= 2; dx++)
                if (dx * dx + dy * dy <= 5)
                    fillFace(img, cx + dx, cy + dy, bright);

        // 6 arms with detailed branches
        for (int arm = 0; arm < 6; arm++) {
            double angle = arm * Math.PI / 3.0;
            for (int i = 1; i <= size; i++) {
                float t = (float) i / size;
                int px = cx + (int)(Math.cos(angle) * i);
                int py = cy + (int)(Math.sin(angle) * i);
                int color = t < 0.4f ? bright : (t < 0.7f ? mid : dim);
                fillFace(img, px, py, color);
                // Width of arm (thicker near center)
                float width = 1.5f * (1 - t * 0.6f);
                double perpAngle = angle + Math.PI / 2;
                for (int w = 1; w <= (int) width; w++) {
                    int wx = px + (int)(Math.cos(perpAngle) * w);
                    int wy = py + (int)(Math.sin(perpAngle) * w);
                    blendFace(img, wx, wy, mid, 0.6f);
                    wx = px - (int)(Math.cos(perpAngle) * w);
                    wy = py - (int)(Math.sin(perpAngle) * w);
                    blendFace(img, wx, wy, mid, 0.6f);
                }

                // Branches at 1/4, 1/2, 3/4
                if (i == size / 4 || i == size / 2 || i == size * 3 / 4) {
                    int branchLen = (int)(size * 0.35f * (1 - t));
                    for (int side = -1; side <= 1; side += 2) {
                        double ba = angle + side * Math.PI / 4;
                        for (int bLen = 1; bLen <= branchLen; bLen++) {
                            float bt = (float) bLen / branchLen;
                            int bx = px + (int)(Math.cos(ba) * bLen);
                            int by = py + (int)(Math.sin(ba) * bLen);
                            blendFace(img, bx, by, bt < 0.5f ? mid : dim, 0.8f * (1 - bt * 0.4f));
                            // Sub-branches
                            if (bLen == branchLen / 2 && branchLen > 3) {
                                double sba = ba + side * Math.PI / 5;
                                for (int sb = 1; sb <= branchLen / 3; sb++) {
                                    blendFace(img, bx + (int)(Math.cos(sba) * sb),
                                        by + (int)(Math.sin(sba) * sb), dim, 0.5f);
                                }
                            }
                        }
                    }
                }
            }
            // Arm tip crystal
            int tipX = cx + (int)(Math.cos(angle) * size);
            int tipY = cy + (int)(Math.sin(angle) * size);
            fillFace(img, tipX, tipY, bright);
        }
    }

    private static void drawIceCrystal(NativeImage img, int cx, int cy, int size) {
        int color = argb(255, 210, 240, 255);
        // Hexagonal outline
        for (int i = 0; i < 6; i++) {
            double a1 = i * Math.PI / 3.0;
            double a2 = (i + 1) * Math.PI / 3.0;
            int x0 = cx + (int)(Math.cos(a1) * size);
            int y0 = cy + (int)(Math.sin(a1) * size);
            int x1 = cx + (int)(Math.cos(a2) * size);
            int y1 = cy + (int)(Math.sin(a2) * size);
            drawLine(img, x0, y0, x1, y1, color, 0.7f);
        }
        fillFace(img, cx, cy, argb(255, 235, 248, 255));
    }

    private static void drawLine(NativeImage img, int x0, int y0, int x1, int y1, int color, float alpha) {
        int dx = Math.abs(x1 - x0), dy = Math.abs(y1 - y0);
        int steps = Math.max(dx, dy);
        if (steps == 0) { blendFace(img, x0, y0, color, alpha); return; }
        for (int i = 0; i <= steps; i++) {
            float t = (float) i / steps;
            int px = (int)(x0 + (x1 - x0) * t);
            int py = (int)(y0 + (y1 - y0) * t);
            blendFace(img, px, py, color, alpha);
        }
    }

    // ========== FLAME ==========
    private static void generateFlame(NativeImage img) {
        // Multi-layered fire gradient with noise turbulence
        for (int y = 0; y < FH; y++) {
            float t = (float) y / (FH - 1);
            for (int x = 0; x < FW; x++) {
                // Turbulent noise for fire look
                float turb = fbm(x * 0.08f, y * 0.06f, 4) * 0.18f;
                float turb2 = fbm(x * 0.12f + 50, y * 0.1f + 50, 3) * 0.1f;
                float f = Math.max(0, Math.min(1, t + turb + turb2));

                int r, g, b;
                if (f < 0.15f) {
                    // White-yellow core (top)
                    float ff = f / 0.15f;
                    r = 255; g = lerp(255, 245, ff); b = lerp(200, 100, ff);
                } else if (f < 0.3f) {
                    // Bright yellow-orange
                    float ff = (f - 0.15f) / 0.15f;
                    r = 255; g = lerp(245, 185, ff); b = lerp(100, 30, ff);
                } else if (f < 0.55f) {
                    // Orange
                    float ff = (f - 0.3f) / 0.25f;
                    r = lerp(255, 220, ff); g = lerp(185, 80, ff); b = lerp(30, 10, ff);
                } else if (f < 0.75f) {
                    // Deep orange-red
                    float ff = (f - 0.55f) / 0.2f;
                    r = lerp(220, 170, ff); g = lerp(80, 30, ff); b = lerp(10, 5, ff);
                } else {
                    // Dark red-black embers
                    float ff = (f - 0.75f) / 0.25f;
                    r = lerp(170, 60, ff); g = lerp(30, 8, ff); b = lerp(5, 0, ff);
                }
                fillFace(img, x, y, argb(255, r, g, b));
            }
            float t2 = (float) y / (FH - 1);
            fillEdges(img, y, argb(255, lerp(255, 60, t2), lerp(220, 8, t2), lerp(120, 0, t2)));
        }
        for (int x = 0; x < FW; x++) fillTop(img, x, argb(255, 255, 248, 180));

        // Flame tongues rising from bottom
        int[][] tongues = {{10, 30}, {25, 25}, {40, 35}, {55, 28}, {70, 22}, {18, 20}, {48, 32}, {65, 26}};
        for (int[] t : tongues) {
            int tx = t[0], h = t[1];
            for (int dy = 0; dy < h; dy++) {
                int y = FH - 1 - dy;
                float f = (float) dy / h;
                // Parabolic width falloff
                int w = Math.max(1, (int)(7 * (1 - f * f)));
                float sway = (float)(Math.sin(dy * 0.18 + tx * 0.3) * 2.5);
                for (int dx = -w; dx <= w; dx++) {
                    int x = tx + dx + (int) sway;
                    float edgeFade = 1.0f - (float) Math.abs(dx) / w;
                    int cr = 255, cg = (int)(220 * (1 - f * 0.7f)), cb = (int)(60 * (1 - f));
                    blendFace(img, x, y, argb(255, cr, cg, cb), 0.35f * (1 - f * 0.5f) * edgeFade);
                }
            }
        }

        // Hot white spots (embers in the core)
        int[][] hotSpots = {{30, 15}, {50, 20}, {15, 25}, {60, 10}, {40, 8}, {20, 12}};
        for (int[] hs : hotSpots) {
            int hx = hs[0], hy = hs[1];
            for (int dy = -3; dy <= 3; dy++) {
                for (int dx = -3; dx <= 3; dx++) {
                    float d = (float) Math.sqrt(dx * dx + dy * dy);
                    if (d <= 3) {
                        float glow = (1 - d / 3.0f);
                        blendFace(img, hx + dx, hy + dy, argb(255, 255, 255, 200), glow * 0.5f);
                    }
                }
            }
        }

        // Ember sparkle dots
        for (int i = 0; i < 20; i++) {
            int ex = (int)(pseudoRand(i * 11, 7) * FW);
            int ey = (int)(pseudoRand(i * 3, 23) * FH);
            float brightness = pseudoRand(i, 99);
            if (brightness > 0.4f) {
                fillFace(img, ex, ey, argb(255, 255, (int)(200 * brightness), (int)(50 * brightness)));
            }
        }
    }

    // ========== OCEAN ==========
    private static void generateOcean(NativeImage img) {
        // Deep ocean gradient with depth layers
        for (int y = 0; y < FH; y++) {
            float t = (float) y / (FH - 1);
            for (int x = 0; x < FW; x++) {
                // Multi-frequency wave noise
                float wave1 = (float)(Math.sin(x * 0.06 + y * 0.03) * 0.04);
                float wave2 = (float)(Math.sin(x * 0.12 - y * 0.08 + 1.5) * 0.02);
                float tt = Math.max(0, Math.min(1, t + wave1 + wave2));
                int r = lerp(15, 3, tt), g = lerp(95, 22, tt), b = lerp(185, 65, tt);
                fillFace(img, x, y, argb(255, r, g, b));
            }
            fillEdges(img, y, argb(255, lerp(15, 3, t), lerp(95, 22, t), lerp(185, 65, t)));
        }
        for (int x = 0; x < FW; x++) fillTop(img, x, argb(255, 15, 95, 185));

        // Multiple wave crest layers with foam
        for (int layer = 0; layer < 7; layer++) {
            int baseY = 8 + layer * 17;
            float freq = 0.12f + layer * 0.02f;
            float amp = 3.5f - layer * 0.3f;
            int bright = 210 - layer * 18;

            for (int x = 0; x < FW; x++) {
                int wy = baseY + (int)(Math.sin(x * freq + layer * 2.1) * amp);
                int wy2 = baseY + (int)(Math.sin(x * freq * 0.7 + layer * 1.3 + 1) * amp * 0.6);
                int finalWy = (wy + wy2) / 2;

                // Wave crest line
                blendFace(img, x, finalWy, argb(255, bright, bright + 10, 255), 0.35f);
                blendFace(img, x, finalWy + 1, argb(255, bright - 20, bright - 10, 245), 0.2f);
                blendFace(img, x, finalWy + 2, argb(255, bright - 40, bright - 30, 235), 0.1f);

                // Foam spots
                if ((x + layer * 7) % 5 == 0) {
                    blendFace(img, x, finalWy - 1, argb(255, 230, 245, 255), 0.5f);
                    blendFace(img, x + 1, finalWy - 1, argb(255, 220, 240, 252), 0.35f);
                    blendFace(img, x - 1, finalWy - 1, argb(255, 220, 240, 252), 0.3f);
                }
            }
        }

        // Caustic light patterns (underwater light ripples)
        for (int y = FH / 2; y < FH; y++) {
            for (int x = 0; x < FW; x++) {
                float caustic = (float)(Math.sin(x * 0.25 + y * 0.15) * Math.sin(x * 0.1 - y * 0.2 + 1));
                if (caustic > 0.5f) {
                    blendFace(img, x, y, argb(255, 60, 140, 220), (caustic - 0.5f) * 0.2f);
                }
            }
        }

        // Small fish silhouettes
        drawFish(img, 55, 90, 6, true);
        drawFish(img, 20, 110, 5, false);
        drawFish(img, 65, 120, 4, true);
    }

    private static void drawFish(NativeImage img, int cx, int cy, int size, boolean facingRight) {
        int dir = facingRight ? 1 : -1;
        // Body (oval)
        for (int dy = -size / 2; dy <= size / 2; dy++) {
            int w = (int)(size * Math.sqrt(1 - (float)(dy * dy) / (size * size / 4.0f)));
            for (int dx = -w; dx <= w; dx++) {
                blendFace(img, cx + dx * dir, cy + dy, argb(255, 20, 60, 120), 0.25f);
            }
        }
        // Tail
        for (int dy = -size / 2; dy <= size / 2; dy++) {
            int tw = Math.abs(dy);
            for (int dx = 0; dx <= tw; dx++) {
                blendFace(img, cx - (size + dx) * dir, cy + dy, argb(255, 15, 50, 110), 0.2f);
            }
        }
    }

    // ========== EMERALD ==========
    private static void generateEmerald(NativeImage img) {
        // Rich green gradient with crystalline depth
        for (int y = 0; y < FH; y++) {
            float t = (float) y / (FH - 1);
            for (int x = 0; x < FW; x++) {
                float n = fbm(x * 0.05f, y * 0.05f, 3) * 0.06f;
                float tt = Math.max(0, Math.min(1, t + n));
                int r = lerp(25, 6, tt), g = lerp(165, 75, tt), b = lerp(55, 20, tt);
                fillFace(img, x, y, argb(255, r, g, b));
            }
            fillEdges(img, y, argb(255, lerp(25, 6, t), lerp(165, 75, t), lerp(55, 20, t)));
        }
        for (int x = 0; x < FW; x++) fillTop(img, x, argb(255, 25, 165, 55));

        // Large faceted gems with light refraction
        drawGem(img, 20, 18, 12);
        drawGem(img, 55, 14, 14);
        drawGem(img, 35, 50, 11);
        drawGem(img, 15, 78, 12);
        drawGem(img, 62, 68, 10);
        drawGem(img, 40, 98, 14);
        drawGem(img, 70, 105, 8);

        // Small gem accents
        drawGem(img, 8, 40, 6);
        drawGem(img, 72, 35, 5);
        drawGem(img, 28, 115, 6);
        drawGem(img, 58, 118, 5);

        // Sparkle points with glow
        int[][] sparkles = {
            {10, 8}, {68, 28}, {30, 35}, {75, 55}, {5, 60}, {50, 75},
            {22, 90}, {65, 95}, {12, 108}, {45, 122}, {72, 118}
        };
        for (int[] s : sparkles) {
            fillFace(img, s[0], s[1], argb(255, 200, 255, 220));
            blendFace(img, s[0] + 1, s[1], argb(255, 160, 240, 190), 0.4f);
            blendFace(img, s[0] - 1, s[1], argb(255, 160, 240, 190), 0.4f);
            blendFace(img, s[0], s[1] + 1, argb(255, 160, 240, 190), 0.4f);
            blendFace(img, s[0], s[1] - 1, argb(255, 160, 240, 190), 0.4f);
        }

        // Neon green edge glow
        for (int y = 0; y < FH; y++) {
            for (int x = 0; x < FW; x++) {
                float edgeDist = Math.min(Math.min(x, FW - 1 - x), Math.min(y, FH - 1 - y)) / 8.0f;
                if (edgeDist < 1.0f) {
                    blendFace(img, x, y, argb(255, 80, 255, 120), (1 - edgeDist) * 0.2f);
                }
            }
        }
    }

    private static void drawGem(NativeImage img, int cx, int cy, int size) {
        // Diamond rhombus with faceted lighting
        for (int dy = -size; dy <= size; dy++) {
            int hw = size - Math.abs(dy);
            for (int dx = -hw; dx <= hw; dx++) {
                float dist = (Math.abs(dx) + Math.abs(dy)) / (float) size;

                // 4-quadrant facet lighting
                int baseG;
                if (dy < 0 && dx <= 0) baseG = 230;       // Top-left: brightest
                else if (dy < 0) baseG = 205;              // Top-right: bright
                else if (dx <= 0) baseG = 175;             // Bottom-left: medium
                else baseG = 145;                           // Bottom-right: darkest

                int r = (int)(baseG * 0.20f), g = baseG, b = (int)(baseG * 0.45f);

                // Edge highlight (gem outline catch-light)
                if (dist > 0.85f) {
                    r += 35; g += 35; b += 35;
                }
                // Inner facet line
                if (Math.abs(dx) <= 1 && dy < 0 && dy > -size) {
                    r += 15; g += 20; b += 15;
                }
                if (Math.abs(dy) <= 1 && dx != 0) {
                    r += 10; g += 15; b += 10;
                }

                fillFace(img, cx + dx, cy + dy, argb(255, clamp(r), clamp(g), clamp(b)));
            }
        }

        // Center highlight (bright reflection point)
        for (int dy = -2; dy <= 1; dy++) {
            for (int dx = -2; dx <= 1; dx++) {
                float d = (float) Math.sqrt(dx * dx + dy * dy);
                if (d <= 2) {
                    float bright = 1 - d / 2.5f;
                    blendFace(img, cx + dx, cy + dy, argb(255, 210, 255, 230), bright * 0.7f);
                }
            }
        }

        // Diagonal highlight streak
        for (int i = -size / 2; i <= 0; i++) {
            int hx = cx + i, hy = cy + i;
            blendFace(img, hx, hy, argb(255, 190, 250, 215), 0.3f);
        }
    }

    // ========== SUNSET ==========
    private static void generateSunset(NativeImage img) {
        // Smooth golden hour palette with wide bands
        for (int y = 0; y < FH; y++) {
            float t = (float) y / (FH - 1);
            int r, g, b;
            if (t < 0.15f) {
                float f = t / 0.15f;
                r = 255; g = lerp(215, 180, f); b = lerp(95, 60, f);
            } else if (t < 0.3f) {
                float f = (t - 0.15f) / 0.15f;
                r = 255; g = lerp(180, 140, f); b = lerp(60, 40, f);
            } else if (t < 0.5f) {
                float f = (t - 0.3f) / 0.2f;
                r = lerp(255, 235, f); g = lerp(140, 80, f); b = lerp(40, 70, f);
            } else if (t < 0.7f) {
                float f = (t - 0.5f) / 0.2f;
                r = lerp(235, 180, f); g = lerp(80, 50, f); b = lerp(70, 115, f);
            } else if (t < 0.85f) {
                float f = (t - 0.7f) / 0.15f;
                r = lerp(180, 120, f); g = lerp(50, 35, f); b = lerp(115, 140, f);
            } else {
                float f = (t - 0.85f) / 0.15f;
                r = lerp(120, 70, f); g = lerp(35, 20, f); b = lerp(140, 115, f);
            }
            for (int x = 0; x < FW; x++) {
                float n = fbm(x * 0.03f, y * 0.03f, 2) * 0.03f;
                int rr = clamp(r + (int)(n * 30));
                int gg = clamp(g + (int)(n * 15));
                int bb = clamp(b + (int)(n * 15));
                fillFace(img, x, y, argb(255, rr, gg, bb));
            }
            fillEdges(img, y, argb(255, r, g, b));
        }
        for (int x = 0; x < FW; x++) fillTop(img, x, argb(255, 255, 215, 95));

        // Large sun with corona
        int sunCx = 40, sunCy = 25, sunR = 16;
        // Corona glow (outer rings)
        for (int dy = -sunR - 14; dy <= sunR + 14; dy++) {
            for (int dx = -sunR - 14; dx <= sunR + 14; dx++) {
                float d = (float) Math.sqrt(dx * dx + dy * dy);
                if (d > sunR && d < sunR + 14) {
                    float glow = 1.0f - (d - sunR) / 14.0f;
                    glow = glow * glow; // Quadratic falloff
                    blendFace(img, sunCx + dx, sunCy + dy, argb(255, 255, 230, 160), glow * 0.35f);
                }
            }
        }
        // Sun disc
        for (int dy = -sunR; dy <= sunR; dy++) {
            for (int dx = -sunR; dx <= sunR; dx++) {
                float d = (float) Math.sqrt(dx * dx + dy * dy);
                if (d <= sunR) {
                    float bright = 1.0f - d / sunR * 0.25f;
                    fillFace(img, sunCx + dx, sunCy + dy,
                        argb(255, 255, (int)(248 * bright), (int)(195 * bright)));
                }
            }
        }

        // Sun rays (long radiating beams)
        for (int ray = 0; ray < 16; ray++) {
            double angle = ray * Math.PI / 8.0;
            float rayLen = 12 + pseudoRand(ray, 42) * 8;
            float rayWidth = 1.5f + pseudoRand(ray, 17) * 1.5f;
            for (int i = sunR + 1; i < sunR + (int) rayLen; i++) {
                float fade = 1.0f - (float)(i - sunR) / rayLen;
                fade = fade * fade;
                int px = sunCx + (int)(Math.cos(angle) * i);
                int py = sunCy + (int)(Math.sin(angle) * i);
                for (int w = (int)-rayWidth; w <= (int) rayWidth; w++) {
                    int wx = px + (int)(Math.cos(angle + Math.PI / 2) * w);
                    int wy = py + (int)(Math.sin(angle + Math.PI / 2) * w);
                    float wFade = 1.0f - (float) Math.abs(w) / rayWidth;
                    blendFace(img, wx, wy, argb(255, 255, 235, 165), fade * wFade * 0.3f);
                }
            }
        }

        // Cloud silhouettes with internal shading
        drawCloud(img, 14, 75, 18);
        drawCloud(img, 55, 88, 14);
        drawCloud(img, 35, 100, 10);

        // Horizon line glow
        for (int x = 0; x < FW; x++) {
            float glow = (float)(Math.sin(x * 0.1) * 0.5 + 0.5) * 0.15f;
            for (int dy = -3; dy <= 3; dy++) {
                float dFade = 1 - (float) Math.abs(dy) / 3.0f;
                blendFace(img, x, FH / 2 + dy, argb(255, 255, 200, 120), glow * dFade);
            }
        }
    }

    private static void drawCloud(NativeImage img, int cx, int cy, int size) {
        // Multi-circle cloud with shading
        int[][] circles = {
            {0, 0, size},
            {-size * 2 / 3, 2, size * 3 / 4},
            {size * 2 / 3, 2, size * 3 / 4},
            {-size / 3, -size / 4, size * 2 / 3},
            {size / 3, -size / 4, size * 2 / 3}
        };
        for (int[] c : circles) {
            int ccx = cx + c[0], ccy = cy + c[1], r = c[2];
            for (int dy = -r; dy <= r; dy++) {
                for (int dx = -r; dx <= r; dx++) {
                    float d = (float) Math.sqrt(dx * dx + dy * dy);
                    if (d <= r) {
                        float shade = d / r;
                        // Darker at bottom, lit at top
                        int dark = dy > 0 ? 75 : 55;
                        blendFace(img, ccx + dx, ccy + dy, argb(255, dark, dark / 2, dark - 10), 0.3f * (1 - shade * 0.3f));
                    }
                }
            }
        }
    }

    // ========== GALAXY ==========
    private static void generateGalaxy(NativeImage img) {
        int cx = FW / 2, cy = FH / 2;

        // Multi-arm spiral galaxy with nebula clouds
        for (int y = 0; y < FH; y++) {
            for (int x = 0; x < FW; x++) {
                float dx = x - cx, dy = (y - cy) * 0.6f; // Stretch vertically to make it elliptical
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                float angle = (float) Math.atan2(dy, dx);

                // Two spiral arms
                float spiral1 = (float)(Math.sin(angle * 2 + dist * 0.06 - 0.5) * 0.5 + 0.5);
                float spiral2 = (float)(Math.sin(angle * 2 + dist * 0.06 + Math.PI - 0.5) * 0.5 + 0.5);
                float spiral = Math.max(spiral1, spiral2);
                float falloff = Math.max(0, 1.0f - dist / 45.0f);
                spiral *= falloff * falloff;

                // Nebula color (purple-blue-pink tones)
                float neb1 = fbm(x * 0.04f, y * 0.04f, 3);
                float neb2 = fbm(x * 0.06f + 100, y * 0.06f + 100, 2);

                int r = 6 + (int)(spiral * 40 + neb1 * 20);
                int g = 2 + (int)(spiral * 12 + neb2 * 8);
                int b = 14 + (int)(spiral * 65 + neb1 * 30);

                // Core brightness
                float core = Math.max(0, 1.0f - dist / 12.0f);
                core = core * core * core;
                r += (int)(core * 80);
                g += (int)(core * 60);
                b += (int)(core * 50);

                fillFace(img, x, y, argb(255, clamp(r), clamp(g), clamp(b)));
            }
            fillEdges(img, y, argb(255, 6, 2, 14));
        }
        for (int x = 0; x < FW; x++) fillTop(img, x, argb(255, 6, 2, 14));

        // Nebula cloud patches (colorful regions)
        drawNebulaPatch(img, 15, 30, 12, argb(255, 80, 20, 120)); // Purple
        drawNebulaPatch(img, 60, 25, 10, argb(255, 30, 50, 130)); // Blue
        drawNebulaPatch(img, 25, 90, 14, argb(255, 100, 25, 80)); // Magenta
        drawNebulaPatch(img, 55, 100, 11, argb(255, 40, 60, 120)); // Teal-blue

        // Stars with cross-flare effect
        drawCrossFlare(img, 12, 14, 5, argb(255, 255, 225, 255));
        drawCrossFlare(img, 55, 35, 4, argb(255, 210, 210, 255));
        drawCrossFlare(img, 24, 60, 5, argb(255, 255, 205, 230));
        drawCrossFlare(img, 68, 75, 4, argb(255, 225, 245, 255));
        drawCrossFlare(img, 40, 108, 5, argb(255, 235, 215, 255));

        // Medium stars
        drawStar(img, 30, 20, 3, argb(255, 220, 220, 255));
        drawStar(img, 65, 50, 3, argb(255, 200, 200, 245));
        drawStar(img, 10, 80, 3, argb(255, 240, 230, 255));
        drawStar(img, 50, 70, 2, argb(255, 210, 210, 250));

        // Dense star field
        int[][] stars = {
            {5, 6}, {18, 8}, {35, 5}, {50, 10}, {72, 12}, {8, 28}, {42, 22},
            {62, 30}, {75, 38}, {3, 45}, {28, 42}, {55, 48}, {70, 55},
            {12, 55}, {38, 58}, {65, 62}, {8, 72}, {25, 75}, {50, 78},
            {74, 80}, {15, 88}, {42, 85}, {60, 90}, {5, 100}, {30, 102},
            {55, 98}, {72, 105}, {18, 112}, {45, 115}, {65, 118},
            {10, 120}, {35, 125}, {58, 122}, {75, 125}
        };
        for (int[] s : stars) {
            float bright = pseudoRand(s[0] * 3, s[1] * 7);
            int starBright = 170 + (int)(bright * 85);
            int blueShift = (int)(bright * 40);
            fillFace(img, s[0], s[1], argb(255, starBright - blueShift / 2, starBright - blueShift / 3, starBright + blueShift));
        }

        // Bright galaxy core glow
        for (int dy = -8; dy <= 8; dy++) {
            for (int dx = -10; dx <= 10; dx++) {
                float d = (float) Math.sqrt(dx * dx + dy * dy * 1.5f);
                if (d < 8) {
                    float glow = (1 - d / 8.0f);
                    glow = glow * glow;
                    blendFace(img, cx + dx, cy + dy, argb(255, 200, 180, 220), glow * 0.4f);
                }
            }
        }
    }

    private static void drawNebulaPatch(NativeImage img, int cx, int cy, int radius, int color) {
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                float d = (float) Math.sqrt(dx * dx + dy * dy);
                if (d < radius) {
                    float n = fbm((cx + dx) * 0.1f, (cy + dy) * 0.1f, 2);
                    float alpha = (1 - d / radius) * n * 0.3f;
                    if (alpha > 0.01f) {
                        blendFace(img, cx + dx, cy + dy, color, alpha);
                    }
                }
            }
        }
    }

    // ========== VIGNETTE HELPER ==========
    private static void applyVignette(NativeImage img, int color, float maxAlpha) {
        for (int y = 0; y < FH; y++) {
            for (int x = 0; x < FW; x++) {
                float dx = (x - FW / 2.0f) / (FW / 2.0f);
                float dy = (y - FH / 2.0f) / (FH / 2.0f);
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                float vignette = Math.max(0, dist - 0.6f) / 0.8f;
                if (vignette > 0) {
                    blendFace(img, x, y, color, Math.min(maxAlpha, vignette * maxAlpha));
                }
            }
        }
    }

    // ========== VOID ==========
    private static void generateVoid(NativeImage img) {
        int cx = FW / 2, cy = FH / 2;
        for (int y = 0; y < FH; y++) {
            for (int x = 0; x < FW; x++) {
                float dx = x - cx, dy = y - cy;
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                float dark = Math.max(0, 1.0f - dist / 40.0f);
                int r = (int)(26 * (1 - dark * 0.5f));
                int g = (int)(10 * (1 - dark * 0.5f));
                int b = (int)(46 * (1 - dark * 0.3f));
                fillFace(img, x, y, argb(255, r, g, b));
            }
            fillEdges(img, y, argb(255, 13, 5, 24));
        }
        for (int x = 0; x < FW; x++) fillTop(img, x, argb(255, 26, 10, 46));
        // Dark energy swirls
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4;
            for (int j = 8; j < 30; j++) {
                int px = cx + (int)(Math.cos(angle + j * 0.05) * j);
                int py = cy + (int)(Math.sin(angle + j * 0.05) * j);
                blendFace(img, px, py, argb(255, 80, 30, 120), 0.3f);
            }
        }
    }

    // ========== LIGHTNING ==========
    private static void generateLightning(NativeImage img) {
        for (int y = 0; y < FH; y++) {
            float t = (float) y / (FH - 1);
            int c = argb(255, lerp(232, 150, t), lerp(212, 120, t), lerp(77, 15, t));
            for (int x = 0; x < FW; x++) fillFace(img, x, y, c);
            fillEdges(img, y, c);
        }
        for (int x = 0; x < FW; x++) fillTop(img, x, argb(255, 232, 212, 77));
        // Lightning bolts
        int[][] bolts = {{15, 0}, {45, 5}, {30, 10}};
        for (int[] bolt : bolts) {
            int bx = bolt[0], by = bolt[1];
            for (int seg = 0; seg < 10; seg++) {
                int nx = bx + (int)((Math.random() - 0.5) * 8);
                int ny = by + 5 + (int)(Math.random() * 3);
                // Draw line segment
                int steps = Math.max(Math.abs(nx - bx), Math.abs(ny - by)) + 1;
                for (int s = 0; s <= steps; s++) {
                    float f = (float) s / steps;
                    int px = (int)(bx + (nx - bx) * f);
                    int py = (int)(by + (ny - by) * f);
                    fillFace(img, px, py, argb(255, 255, 255, 200));
                    blendFace(img, px - 1, py, argb(255, 255, 255, 100), 0.4f);
                    blendFace(img, px + 1, py, argb(255, 255, 255, 100), 0.4f);
                }
                bx = nx; by = ny;
            }
        }
    }

    // ========== BLOODMOON ==========
    private static void generateBlood(NativeImage img) {
        for (int y = 0; y < FH; y++) {
            float t = (float) y / (FH - 1);
            int c = argb(255, lerp(139, 74, t), 0, 0);
            for (int x = 0; x < FW; x++) fillFace(img, x, y, c);
            fillEdges(img, y, c);
        }
        for (int x = 0; x < FW; x++) fillTop(img, x, argb(255, 139, 0, 0));
        // Blood moon
        int moonCx = 40, moonCy = 20, moonR = 12;
        for (int dy = -moonR; dy <= moonR; dy++) {
            for (int dx = -moonR; dx <= moonR; dx++) {
                float d = (float) Math.sqrt(dx * dx + dy * dy);
                if (d <= moonR) {
                    float bright = 1.0f - d / moonR * 0.4f;
                    fillFace(img, moonCx + dx, moonCy + dy, argb(255, (int)(200 * bright), (int)(30 * bright), (int)(30 * bright)));
                }
            }
        }
        // Drip effects
        int[][] drips = {{10, 40}, {25, 50}, {55, 35}, {65, 55}};
        for (int[] drip : drips) {
            for (int dy = 0; dy < 15; dy++) {
                float fade = 1.0f - (float) dy / 15;
                blendFace(img, drip[0], drip[1] + dy, argb(255, 180, 0, 0), fade * 0.5f);
            }
        }
    }

    // ========== ARCTIC ==========
    private static void generateArctic(NativeImage img) {
        for (int y = 0; y < FH; y++) {
            float t = (float) y / (FH - 1);
            for (int x = 0; x < FW; x++) {
                float n = (float)(Math.sin(x * 0.1 + y * 0.08) * 0.03);
                int c = argb(255, lerp(224, 160, t + n), lerp(240, 196, t + n), lerp(255, 232, t + n));
                fillFace(img, x, y, c);
            }
            fillEdges(img, y, argb(255, lerp(224, 160, t), lerp(240, 196, t), lerp(255, 232, t)));
        }
        for (int x = 0; x < FW; x++) fillTop(img, x, argb(255, 224, 240, 255));
        // Aurora bands
        for (int band = 0; band < 3; band++) {
            int baseY = 15 + band * 20;
            for (int x = 0; x < FW; x++) {
                int by = baseY + (int)(Math.sin(x * 0.15 + band) * 4);
                blendFace(img, x, by, argb(255, 100, 255, 180), 0.2f);
                blendFace(img, x, by + 1, argb(255, 80, 200, 255), 0.15f);
            }
        }
        // Ice sparkles
        int[][] sparkles = {{10, 10}, {30, 25}, {50, 15}, {20, 50}, {60, 45}, {35, 60}, {15, 35}, {55, 55}};
        for (int[] sp : sparkles) fillFace(img, sp[0], sp[1], argb(255, 240, 250, 255));
    }

    // ========== PHANTOM ==========
    private static void generatePhantom(NativeImage img) {
        // Ghostly white-grey gradient with ethereal noise
        for (int y = 0; y < FH; y++) {
            float t = (float) y / (FH - 1);
            for (int x = 0; x < FW; x++) {
                float n = fbm(x * 0.04f, y * 0.04f, 3) * 0.1f;
                float tt = Math.max(0, Math.min(1, t + n));
                int r = lerp(210, 100, tt), g = lerp(210, 100, tt), b = lerp(220, 120, tt);
                fillFace(img, x, y, argb(255, r, g, b));
            }
            fillEdges(img, y, argb(255, lerp(200, 100, t), lerp(200, 100, t), lerp(215, 115, t)));
        }
        for (int x = 0; x < FW; x++) fillTop(img, x, argb(255, 210, 210, 220));

        // Spectral wisps (elongated semi-transparent shapes)
        int[][] wisps = {{15, 20, 25}, {55, 15, 30}, {30, 50, 20}, {65, 60, 28}, {10, 85, 22}, {50, 95, 25}, {35, 110, 18}, {70, 35, 20}};
        for (int[] w : wisps) {
            int wx = w[0], wy = w[1], wLen = w[2];
            for (int dy = -wLen; dy <= wLen; dy++) {
                float yf = (float) Math.abs(dy) / wLen;
                float width = (1 - yf * yf) * 8;
                float sway = (float)(Math.sin(dy * 0.15 + wx * 0.1) * 3);
                for (int dx = (int)(-width - 1); dx <= (int)(width + 1); dx++) {
                    float xf = (float) Math.abs(dx - sway) / Math.max(1, width);
                    if (xf < 1) {
                        float alpha = (1 - xf) * (1 - yf) * 0.35f;
                        blendFace(img, wx + dx, wy + dy, argb(255, 240, 240, 255), alpha);
                    }
                }
            }
        }

        // Ghost face silhouettes (faint)
        drawGhostFace(img, 25, 40, 8);
        drawGhostFace(img, 60, 75, 6);
        drawGhostFace(img, 15, 100, 7);

        // Ethereal particles
        for (int i = 0; i < 18; i++) {
            int px = (int)(pseudoRand(i * 11, 33) * FW);
            int py = (int)(pseudoRand(i * 7, 51) * FH);
            float bright = pseudoRand(i, 77);
            if (bright > 0.3f) {
                for (int dy = -2; dy <= 2; dy++)
                    for (int dx = -2; dx <= 2; dx++) {
                        float d = (float) Math.sqrt(dx * dx + dy * dy);
                        if (d <= 2) blendFace(img, px + dx, py + dy, argb(255, 235, 235, 255), (1 - d / 2) * bright * 0.3f);
                    }
            }
        }
        applyVignette(img, argb(255, 80, 80, 100), 0.2f);
    }

    private static void drawGhostFace(NativeImage img, int cx, int cy, int size) {
        // Faint circular face outline
        for (int dy = -size; dy <= size; dy++) {
            for (int dx = -size; dx <= size; dx++) {
                float d = (float) Math.sqrt(dx * dx + dy * dy);
                if (d > size - 1.5f && d <= size) {
                    blendFace(img, cx + dx, cy + dy, argb(255, 220, 220, 245), 0.2f);
                }
            }
        }
        // Eyes
        blendFace(img, cx - size / 3, cy - size / 4, argb(255, 180, 200, 255), 0.35f);
        blendFace(img, cx + size / 3, cy - size / 4, argb(255, 180, 200, 255), 0.35f);
        // Mouth
        for (int dx = -size / 4; dx <= size / 4; dx++) {
            blendFace(img, cx + dx, cy + size / 3, argb(255, 200, 200, 240), 0.2f);
        }
    }

    // ========== NEON ==========
    private static void generateNeon(NativeImage img) {
        // Dark cyberpunk background
        for (int y = 0; y < FH; y++) {
            float t = (float) y / (FH - 1);
            for (int x = 0; x < FW; x++) {
                float n = fbm(x * 0.05f, y * 0.05f, 2) * 0.04f;
                int r = lerp(18, 5, t + n), g = lerp(5, 2, t + n), b = lerp(30, 15, t + n);
                fillFace(img, x, y, argb(255, Math.max(0, r), Math.max(0, g), Math.max(0, b)));
            }
            fillEdges(img, y, argb(255, lerp(18, 5, t), lerp(5, 2, t), lerp(30, 15, t)));
        }
        for (int x = 0; x < FW; x++) fillTop(img, x, argb(255, 18, 5, 30));

        // Neon grid lines (horizontal)
        for (int gridY = 10; gridY < FH; gridY += 14) {
            for (int x = 0; x < FW; x++) {
                blendFace(img, x, gridY, argb(255, 255, 50, 200), 0.25f);
                blendFace(img, x, gridY + 1, argb(255, 200, 30, 160), 0.1f);
            }
        }
        // Neon grid lines (vertical)
        for (int gridX = 8; gridX < FW; gridX += 14) {
            for (int y = 0; y < FH; y++) {
                blendFace(img, gridX, y, argb(255, 0, 220, 255), 0.2f);
                blendFace(img, gridX + 1, y, argb(255, 0, 180, 220), 0.08f);
            }
        }

        // Glitch blocks (random neon rectangles)
        int[][] glitches = {{5, 25, 15, 4}, {40, 50, 20, 3}, {10, 80, 12, 5}, {55, 100, 18, 3}, {25, 15, 10, 4}, {60, 70, 14, 3}};
        for (int[] gl : glitches) {
            int gx = gl[0], gy = gl[1], gw = gl[2], gh = gl[3];
            int color = (gx + gy) % 2 == 0 ? argb(255, 255, 50, 220) : argb(255, 0, 255, 255);
            for (int dy = 0; dy < gh; dy++)
                for (int dx = 0; dx < gw; dx++)
                    blendFace(img, gx + dx, gy + dy, color, 0.3f);
        }

        // Neon glow spots
        drawNeonGlow(img, 20, 30, 10, argb(255, 255, 50, 200));
        drawNeonGlow(img, 60, 55, 12, argb(255, 0, 255, 255));
        drawNeonGlow(img, 35, 85, 8, argb(255, 255, 100, 255));
        drawNeonGlow(img, 55, 110, 10, argb(255, 0, 200, 255));

        // Scanline effect
        for (int y = 0; y < FH; y += 2) {
            for (int x = 0; x < FW; x++) {
                blendFace(img, x, y, argb(255, 0, 0, 0), 0.08f);
            }
        }
    }

    private static void drawNeonGlow(NativeImage img, int cx, int cy, int radius, int color) {
        for (int dy = -radius - 3; dy <= radius + 3; dy++) {
            for (int dx = -radius - 3; dx <= radius + 3; dx++) {
                float d = (float) Math.sqrt(dx * dx + dy * dy);
                if (d <= radius + 3) {
                    float alpha = d <= radius ? 0.5f : (1 - (d - radius) / 3) * 0.25f;
                    blendFace(img, cx + dx, cy + dy, color, alpha);
                }
            }
        }
    }

    // ========== LAVA ==========
    private static void generateLava(NativeImage img) {
        // Multi-layered lava with black crust and orange glow underneath
        for (int y = 0; y < FH; y++) {
            float t = (float) y / (FH - 1);
            for (int x = 0; x < FW; x++) {
                float turb = fbm(x * 0.06f, y * 0.05f, 4) * 0.22f;
                float f = Math.max(0, Math.min(1, t + turb));
                int r, g, b;
                if (f < 0.25f) {
                    float ff = f / 0.25f;
                    r = lerp(255, 220, ff); g = lerp(180, 100, ff); b = lerp(30, 8, ff);
                } else if (f < 0.5f) {
                    float ff = (f - 0.25f) / 0.25f;
                    r = lerp(220, 60, ff); g = lerp(100, 30, ff); b = lerp(8, 5, ff);
                } else {
                    float ff = (f - 0.5f) / 0.5f;
                    r = lerp(60, 25, ff); g = lerp(30, 12, ff); b = lerp(5, 5, ff);
                }
                // Lava cracks with orange glow
                float crack = voronoi(x * 0.1f, y * 0.08f);
                if (crack < 0.12f) {
                    float crackGlow = (0.12f - crack) / 0.12f;
                    r = Math.min(255, r + (int)(200 * crackGlow));
                    g = Math.min(255, g + (int)(130 * crackGlow));
                    b = Math.min(255, b + (int)(15 * crackGlow));
                }
                fillFace(img, x, y, argb(255, r, g, b));
            }
            fillEdges(img, y, argb(255, lerp(200, 30, t), lerp(100, 12, t), lerp(15, 5, t)));
        }
        for (int x = 0; x < FW; x++) fillTop(img, x, argb(255, 240, 140, 20));

        // Molten pools
        int[][] pools = {{18, 25, 8}, {55, 40, 10}, {30, 70, 7}, {65, 90, 9}, {15, 105, 6}};
        for (int[] pool : pools) {
            int px = pool[0], py = pool[1], pr = pool[2];
            for (int dy = -pr; dy <= pr; dy++) {
                for (int dx = -pr; dx <= pr; dx++) {
                    float d = (float) Math.sqrt(dx * dx + dy * dy);
                    if (d <= pr) {
                        float glow = 1 - d / pr;
                        int mr = (int)(255 * glow), mg = (int)(160 * glow * glow), mb = (int)(20 * glow * glow * glow);
                        blendFace(img, px + dx, py + dy, argb(255, mr, mg, mb), glow * 0.7f);
                    }
                }
            }
        }

        // Hot ember particles
        for (int i = 0; i < 15; i++) {
            int ex = (int)(pseudoRand(i * 9, 11) * FW);
            int ey = (int)(pseudoRand(i * 5, 29) * FH);
            float bright = pseudoRand(i, 88);
            if (bright > 0.4f) fillFace(img, ex, ey, argb(255, 255, (int)(200 * bright), (int)(50 * bright)));
        }
    }

    // ========== SAKURA ==========
    private static void generateSakura(NativeImage img) {
        // Soft pink background
        for (int y = 0; y < FH; y++) {
            float t = (float) y / (FH - 1);
            for (int x = 0; x < FW; x++) {
                float n = fbm(x * 0.04f, y * 0.04f, 3) * 0.06f;
                float tt = Math.max(0, Math.min(1, t + n));
                int r = lerp(255, 240, tt), g = lerp(225, 180, tt), b = lerp(235, 200, tt);
                fillFace(img, x, y, argb(255, r, g, b));
            }
            fillEdges(img, y, argb(255, lerp(255, 240, t), lerp(220, 180, t), lerp(230, 200, t)));
        }
        for (int x = 0; x < FW; x++) fillTop(img, x, argb(255, 255, 225, 235));

        // Cherry blossom branch (diagonal curved)
        drawBranch(img, 5, 50, 75, 30, 2.5f);
        drawBranch(img, 15, 100, 70, 75, 2.0f);
        drawBranch(img, 35, 30, 55, 15, 1.5f);

        // Cherry blossom flowers
        drawFlower(img, 20, 25, 14, 0.0f);
        drawFlower(img, 55, 18, 16, 0.5f);
        drawFlower(img, 38, 50, 13, 0.8f);
        drawFlower(img, 68, 38, 11, 0.2f);
        drawFlower(img, 15, 80, 15, 0.6f);
        drawFlower(img, 50, 72, 12, 0.3f);
        drawFlower(img, 30, 95, 13, 0.9f);
        drawFlower(img, 65, 100, 10, 0.1f);

        // Falling petals
        for (int i = 0; i < 16; i++) {
            int px = (int)(pseudoRand(i * 7, 42) * FW);
            int py = (int)(pseudoRand(i * 13, 17) * FH);
            float rot = pseudoRand(i * 3, 99) * 6.28f;
            drawPetal(img, px, py, 3 + (int)(pseudoRand(i, 5) * 3), rot, 0.5f);
        }

        applyVignette(img, argb(255, 180, 100, 120), 0.1f);
    }

    // ========== STORM ==========
    private static void generateStorm(NativeImage img) {
        // Dark stormy grey gradient
        for (int y = 0; y < FH; y++) {
            float t = (float) y / (FH - 1);
            for (int x = 0; x < FW; x++) {
                float n = fbm(x * 0.05f, y * 0.04f, 4) * 0.12f;
                float tt = Math.max(0, Math.min(1, t + n));
                int r = lerp(70, 22, tt), g = lerp(75, 25, tt), b = lerp(85, 32, tt);
                fillFace(img, x, y, argb(255, r, g, b));
            }
            fillEdges(img, y, argb(255, lerp(65, 22, t), lerp(70, 25, t), lerp(80, 32, t)));
        }
        for (int x = 0; x < FW; x++) fillTop(img, x, argb(255, 70, 75, 85));

        // Thundercloud formations (darker clusters)
        int[][] clouds = {{15, 15, 18}, {50, 10, 22}, {30, 35, 15}, {65, 28, 17}};
        for (int[] cl : clouds) {
            int cx = cl[0], cy = cl[1], cr = cl[2];
            for (int dy = -cr; dy <= cr; dy++) {
                for (int dx = -cr; dx <= cr; dx++) {
                    float d = (float) Math.sqrt(dx * dx + (dy * 1.5f) * (dy * 1.5f));
                    if (d <= cr) {
                        float n = fbm((cx + dx) * 0.08f, (cy + dy) * 0.08f, 2);
                        float alpha = (1 - d / cr) * 0.35f * n;
                        blendFace(img, cx + dx, cy + dy, argb(255, 30, 32, 40), alpha);
                    }
                }
            }
        }

        // Lightning bolt
        drawLightningBolt(img, 35, 8, 110, 4);
        // Secondary smaller bolt
        drawLightningBolt(img, 58, 12, 80, 3);

        // Rain streaks
        for (int i = 0; i < 50; i++) {
            int rx = (int)(pseudoRand(i * 11, 7) * FW);
            int ry = (int)(pseudoRand(i * 3, 23) * FH);
            int len = 5 + (int)(pseudoRand(i, 55) * 12);
            for (int j = 0; j < len && ry + j < FH; j++) {
                float fade = 1 - (float) j / len;
                blendFace(img, rx, ry + j, argb(255, 160, 180, 210), fade * 0.35f);
            }
        }
    }

    private static void drawLightningBolt(NativeImage img, int startX, int startY, int length, int branches) {
        int lx = startX, ly = startY;
        int boltColor = argb(255, 255, 255, 220);
        int glowColor = argb(255, 200, 200, 255);
        for (int seg = 0; seg < length / 8; seg++) {
            int nlx = lx + (int)(pseudoRand(seg * 3 + startX, seg * 7) * 10 - 5);
            int nly = ly + 6 + (int)(pseudoRand(seg * 5, seg * 11) * 5);
            drawLine(img, lx, ly, nlx, nly, boltColor, 0.95f);
            // Glow around bolt
            int mx = (lx + nlx) / 2, my = (ly + nly) / 2;
            for (int dy = -3; dy <= 3; dy++)
                for (int dx = -3; dx <= 3; dx++) {
                    float d = (float) Math.sqrt(dx * dx + dy * dy);
                    if (d > 0 && d <= 3) blendFace(img, mx + dx, my + dy, glowColor, (1 - d / 3) * 0.3f);
                }
            // Branch lightning
            if (seg % 3 == 1 && branches > 0) {
                int bx = nlx, by = nly;
                int dir = seg % 2 == 0 ? 1 : -1;
                for (int bs = 0; bs < 4; bs++) {
                    int nbx = bx + dir * (3 + (int)(pseudoRand(bs + seg, 99) * 3));
                    int nby = by + 3 + (int)(pseudoRand(bs + seg * 2, 77) * 3);
                    drawLine(img, bx, by, nbx, nby, boltColor, 0.6f);
                    bx = nbx; by = nby;
                }
            }
            lx = nlx; ly = nly;
        }
    }

    // ========== SOLAR ==========
    private static void generateSolar(NativeImage img) {
        // Hot orange-yellow gradient
        for (int y = 0; y < FH; y++) {
            float t = (float) y / (FH - 1);
            for (int x = 0; x < FW; x++) {
                float n = fbm(x * 0.05f, y * 0.04f, 3) * 0.08f;
                float tt = Math.max(0, Math.min(1, t + n));
                int r = lerp(255, 180, tt), g = lerp(210, 60, tt), b = lerp(80, 5, tt);
                fillFace(img, x, y, argb(255, r, g, b));
            }
            fillEdges(img, y, argb(255, lerp(255, 180, t), lerp(200, 60, t), lerp(70, 5, t)));
        }
        for (int x = 0; x < FW; x++) fillTop(img, x, argb(255, 255, 215, 90));

        // Solar disc with corona
        int scx = FW / 2, scy = 28;
        // Corona rays
        for (int ray = 0; ray < 24; ray++) {
            double angle = ray * Math.PI * 2 / 24;
            int rayLen = 25 + (int)(pseudoRand(ray * 5, 10) * 18);
            for (int r = 0; r <= rayLen; r++) {
                float rt = (float) r / rayLen;
                int px = scx + (int)(Math.cos(angle) * r);
                int py = scy + (int)(Math.sin(angle) * r);
                float alpha = (1 - rt) * 0.4f;
                float width = 2 * (1 - rt);
                for (int w = (int)-width; w <= (int) width; w++) {
                    int wx = px + (int)(Math.cos(angle + Math.PI / 2) * w);
                    int wy = py + (int)(Math.sin(angle + Math.PI / 2) * w);
                    blendFace(img, wx, wy, argb(255, 255, 230, 100), alpha * (1 - (float) Math.abs(w) / Math.max(1, width)));
                }
            }
        }
        // Bright solar core
        for (int dy = -14; dy <= 14; dy++) {
            for (int dx = -14; dx <= 14; dx++) {
                float d = (float) Math.sqrt(dx * dx + dy * dy);
                if (d <= 14) {
                    float core = 1 - d / 14;
                    int r = 255, g = (int)(220 + 35 * core), b = (int)(100 + 155 * core * core);
                    fillFace(img, scx + dx, scy + dy, argb(255, r, g, b));
                }
            }
        }

        // Solar flare arcs
        drawSolarFlare(img, 12, 60, 30, 15, true);
        drawSolarFlare(img, 50, 75, 25, 12, false);

        // Sunspots
        for (int dy = -3; dy <= 3; dy++)
            for (int dx = -3; dx <= 3; dx++)
                if (dx * dx + dy * dy <= 9)
                    blendFace(img, scx - 5 + dx, scy + 2 + dy, argb(255, 180, 100, 20), 0.3f);
    }

    private static void drawSolarFlare(NativeImage img, int startX, int startY, int width, int height, boolean curveRight) {
        for (int x = 0; x < width; x++) {
            float t = (float) x / width;
            float curve = (float) Math.sin(t * Math.PI) * height;
            int py = startY - (int) curve;
            int px = startX + x;
            if (curveRight) px = startX + x; else px = startX - x + width;
            for (int w = -2; w <= 2; w++) {
                float alpha = (1 - (float) Math.abs(w) / 2) * 0.5f * (float) Math.sin(t * Math.PI);
                blendFace(img, px, py + w, argb(255, 255, 200, 60), alpha);
            }
        }
    }

    // ========== AMETHYST ==========
    private static void generateAmethyst(NativeImage img) {
        // Deep purple gradient
        for (int y = 0; y < FH; y++) {
            float t = (float) y / (FH - 1);
            for (int x = 0; x < FW; x++) {
                float n = fbm(x * 0.05f, y * 0.05f, 3) * 0.08f;
                float tt = Math.max(0, Math.min(1, t + n));
                int r = lerp(90, 35, tt), g = lerp(25, 8, tt), b = lerp(130, 55, tt);
                fillFace(img, x, y, argb(255, r, g, b));
            }
            fillEdges(img, y, argb(255, lerp(85, 35, t), lerp(22, 8, t), lerp(125, 55, t)));
        }
        for (int x = 0; x < FW; x++) fillTop(img, x, argb(255, 90, 25, 130));

        // Crystal cluster formations
        drawCrystalCluster(img, 15, 22, 18);
        drawCrystalCluster(img, 55, 18, 22);
        drawCrystalCluster(img, 35, 55, 16);
        drawCrystalCluster(img, 68, 48, 20);
        drawCrystalCluster(img, 20, 88, 14);
        drawCrystalCluster(img, 55, 82, 17);
        drawCrystalCluster(img, 40, 112, 12);

        // Faceted light reflections
        for (int i = 0; i < 12; i++) {
            int fx = (int)(pseudoRand(i * 9, 44) * FW);
            int fy = (int)(pseudoRand(i * 5, 66) * FH);
            drawCrossFlare(img, fx, fy, 3 + (int)(pseudoRand(i, 22) * 3), argb(255, 200, 160, 255));
        }

        // Edge glow
        for (int y = 0; y < FH; y++) {
            float edgeDist = Math.min(y, FH - 1 - y) / (float) FH;
            if (edgeDist < 0.05f) {
                float glow = (1 - edgeDist / 0.05f) * 0.2f;
                for (int x = 0; x < FW; x++)
                    blendFace(img, x, y, argb(255, 180, 120, 255), glow);
            }
        }
    }

    private static void drawCrystalCluster(NativeImage img, int cx, int cy, int size) {
        // Multiple crystal shards pointing upward at different angles
        int shards = 5 + (int)(pseudoRand(cx, cy) * 3);
        for (int i = 0; i < shards; i++) {
            float angle = (pseudoRand(cx + i, cy + i * 3) - 0.5f) * 1.2f;
            float len = size * (0.5f + pseudoRand(i + cx, i + cy) * 0.5f);
            int w = 2 + (int)(pseudoRand(i * 2, cx) * 2);
            for (int j = 0; j < (int) len; j++) {
                float t = (float) j / len;
                int px = cx + (int)(Math.sin(angle) * j);
                int py = cy - j;
                float taper = 1 - t;
                for (int dx = (int)(-w * taper); dx <= (int)(w * taper); dx++) {
                    float edge = (float) Math.abs(dx) / Math.max(1, w * taper);
                    int r = (int)(140 + 80 * (1 - edge) * (1 - t * 0.3f));
                    int g = (int)(50 + 40 * (1 - edge));
                    int b = (int)(180 + 75 * (1 - edge) * (1 - t * 0.2f));
                    blendFace(img, px + dx, py, argb(255, r, g, b), 0.8f * (1 - edge * 0.3f));
                }
            }
            // Crystal tip highlight
            int tipX = cx + (int)(Math.sin(angle) * len);
            int tipY = cy - (int) len;
            fillFace(img, tipX, tipY, argb(255, 220, 180, 255));
        }
    }

    // ========== INFERNO ==========
    private static void generateInferno(NativeImage img) {
        // Dark hellfire gradient (black to deep red)
        for (int y = 0; y < FH; y++) {
            float t = (float) y / (FH - 1);
            for (int x = 0; x < FW; x++) {
                float turb = fbm(x * 0.08f, y * 0.06f, 4) * 0.2f;
                float f = Math.max(0, Math.min(1, t + turb));
                int r, g, b;
                if (f < 0.3f) {
                    float ff = f / 0.3f;
                    r = lerp(25, 60, ff); g = lerp(5, 5, ff); b = lerp(5, 2, ff);
                } else if (f < 0.6f) {
                    float ff = (f - 0.3f) / 0.3f;
                    r = lerp(60, 200, ff); g = lerp(5, 25, ff); b = lerp(2, 5, ff);
                } else {
                    float ff = (f - 0.6f) / 0.4f;
                    r = lerp(200, 255, ff); g = lerp(25, 80, ff); b = lerp(5, 10, ff);
                }
                fillFace(img, x, y, argb(255, r, g, b));
            }
            fillEdges(img, y, argb(255, lerp(25, 220, t), lerp(5, 40, t), lerp(5, 5, t)));
        }
        for (int x = 0; x < FW; x++) fillTop(img, x, argb(255, 30, 5, 5));

        // Skull motifs
        drawSkull(img, 22, 30, 10);
        drawSkull(img, 58, 55, 8);
        drawSkull(img, 35, 85, 12);
        drawSkull(img, 68, 105, 7);

        // Hellfire wisps
        int[][] hellFires = {{10, 20, 25}, {40, 15, 30}, {65, 35, 22}, {20, 60, 20}, {55, 80, 28}, {30, 100, 18}};
        for (int[] hf : hellFires) {
            int hx = hf[0], hy = hf[1], hLen = hf[2];
            for (int dy = 0; dy < hLen; dy++) {
                int y = hy + hLen - dy;
                float f = (float) dy / hLen;
                int w = Math.max(1, (int)(5 * (1 - f * f)));
                float sway = (float)(Math.sin(dy * 0.2 + hx * 0.3) * 2);
                for (int dx = -w; dx <= w; dx++) {
                    float edge = 1 - (float) Math.abs(dx) / w;
                    blendFace(img, hx + dx + (int) sway, y, argb(255, 255, (int)(60 * (1 - f)), (int)(10 * (1 - f))), 0.25f * (1 - f * 0.4f) * edge);
                }
            }
        }

        // Ember particles
        for (int i = 0; i < 20; i++) {
            int ex = (int)(pseudoRand(i * 13, 9) * FW);
            int ey = (int)(pseudoRand(i * 7, 31) * FH);
            fillFace(img, ex, ey, argb(255, 255, (int)(150 * pseudoRand(i, 99)), 0));
        }
    }

    private static void drawSkull(NativeImage img, int cx, int cy, int size) {
        // Skull cranium (oval)
        for (int dy = -size; dy <= size / 2; dy++) {
            for (int dx = -size; dx <= size; dx++) {
                float d = (float) Math.sqrt(dx * dx + (dy * 1.3f) * (dy * 1.3f));
                if (d <= size) {
                    float edge = d / size;
                    int r = (int)(80 * (1 - edge * 0.5f));
                    int g = (int)(12 * (1 - edge * 0.5f));
                    int b = (int)(12 * (1 - edge * 0.5f));
                    blendFace(img, cx + dx, cy + dy, argb(255, r, g, b), 0.6f);
                }
            }
        }
        // Jaw
        for (int dx = -size / 2; dx <= size / 2; dx++) {
            for (int dy = size / 2; dy <= size / 2 + 3; dy++) {
                blendFace(img, cx + dx, cy + dy, argb(255, 60, 8, 8), 0.5f);
            }
        }
        // Eye sockets (glowing)
        int eyeOff = size / 3;
        for (int dy = -2; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                fillFace(img, cx - eyeOff + dx, cy - size / 4 + dy, argb(255, 255, 80, 0));
                fillFace(img, cx + eyeOff + dx, cy - size / 4 + dy, argb(255, 255, 80, 0));
            }
        }
        // Nose
        blendFace(img, cx, cy + 1, argb(255, 40, 5, 5), 0.7f);
        blendFace(img, cx - 1, cy + 2, argb(255, 40, 5, 5), 0.5f);
    }

    // ========== DRIFT ==========
    private static void generateDrift(NativeImage img) {
        // Pastel vaporwave gradient that shifts across the face
        for (int y = 0; y < FH; y++) {
            float t = (float) y / (FH - 1);
            for (int x = 0; x < FW; x++) {
                float xt = (float) x / FW;
                float n = fbm(x * 0.03f, y * 0.03f, 2) * 0.08f;
                float phase = (xt * 0.5f + t * 0.5f + n) * 3;
                int r = clamp((int)(200 + 55 * Math.sin(phase)));
                int g = clamp((int)(180 + 60 * Math.sin(phase + 2.1)));
                int b = clamp((int)(220 + 35 * Math.sin(phase + 4.2)));
                fillFace(img, x, y, argb(255, r, g, b));
            }
            float p = t * 3;
            int er = clamp((int)(200 + 55 * Math.sin(p)));
            int eg = clamp((int)(180 + 60 * Math.sin(p + 2.1)));
            int eb = clamp((int)(220 + 35 * Math.sin(p + 4.2)));
            fillEdges(img, y, argb(255, er, eg, eb));
        }
        for (int x = 0; x < FW; x++) {
            float p = ((float) x / FW) * 1.5f;
            fillTop(img, x, argb(255, clamp((int)(200 + 55 * Math.sin(p))), clamp((int)(180 + 60 * Math.sin(p + 2.1))), clamp((int)(220 + 35 * Math.sin(p + 4.2)))));
        }

        // Vaporwave sun lines at bottom
        int sunCy = FH - 20;
        for (int stripe = 0; stripe < 5; stripe++) {
            int sy = sunCy + stripe * 4;
            for (int x = 0; x < FW; x++) {
                float d = Math.abs(x - FW / 2f) / (FW / 2f);
                if (d < 0.8f) blendFace(img, x, sy, argb(255, 255, 120, 200), 0.15f * (1 - d));
            }
        }

        // Soft geometric shapes
        drawLine(img, 10, 20, 70, 25, argb(255, 255, 180, 220), 0.2f);
        drawLine(img, 5, 50, 75, 55, argb(255, 180, 220, 255), 0.15f);
        drawLine(img, 15, 80, 65, 82, argb(255, 220, 180, 255), 0.15f);

        // Sparkle dots
        for (int i = 0; i < 15; i++) {
            int px = (int)(pseudoRand(i * 11, 33) * FW);
            int py = (int)(pseudoRand(i * 7, 51) * FH);
            fillFace(img, px, py, argb(255, 255, 255, 255));
        }
    }

    // ========== OBSIDIAN ==========
    private static void generateObsidian(NativeImage img) {
        // Very dark gradient with subtle texture
        for (int y = 0; y < FH; y++) {
            float t = (float) y / (FH - 1);
            for (int x = 0; x < FW; x++) {
                float n = fbm(x * 0.06f, y * 0.06f, 3) * 0.05f;
                float tt = Math.max(0, Math.min(1, t + n));
                int r = lerp(30, 10, tt), g = lerp(18, 5, tt), b = lerp(38, 16, tt);
                fillFace(img, x, y, argb(255, r, g, b));
            }
            fillEdges(img, y, argb(255, lerp(28, 10, t), lerp(16, 5, t), lerp(35, 16, t)));
        }
        for (int x = 0; x < FW; x++) fillTop(img, x, argb(255, 30, 18, 38));

        // Purple glowing cracks (Voronoi-based)
        for (int y = 0; y < FH; y++) {
            for (int x = 0; x < FW; x++) {
                float v = voronoi(x * 0.1f, y * 0.08f);
                if (v < 0.1f) {
                    float crack = (0.1f - v) / 0.1f;
                    int cr = (int)(120 * crack), cg = (int)(40 * crack), cb = (int)(180 * crack);
                    blendFace(img, x, y, argb(255, cr, cg, cb), crack * 0.8f);
                }
                // Purple glow around cracks
                if (v < 0.18f && v >= 0.1f) {
                    float glow = (0.18f - v) / 0.08f;
                    blendFace(img, x, y, argb(255, 80, 20, 120), glow * 0.25f);
                }
            }
        }

        // Obsidian sheen (glossy reflection bands)
        for (int band = 0; band < 4; band++) {
            float bandY = 15 + band * 30;
            float angle = 0.15f + band * 0.1f;
            for (int x = 0; x < FW; x++) {
                int y = (int)(bandY + x * angle);
                for (int w = -1; w <= 1; w++) {
                    if (y + w >= 0 && y + w < FH) {
                        float alpha = w == 0 ? 0.15f : 0.06f;
                        blendFace(img, x, y + w, argb(255, 50, 40, 60), alpha);
                    }
                }
            }
        }

        // Highlight sparkles (rare bright purple dots)
        for (int i = 0; i < 10; i++) {
            int px = (int)(pseudoRand(i * 13, 55) * FW);
            int py = (int)(pseudoRand(i * 9, 77) * FH);
            fillFace(img, px, py, argb(255, 160, 80, 220));
            blendFace(img, px + 1, py, argb(255, 120, 60, 180), 0.5f);
            blendFace(img, px, py + 1, argb(255, 120, 60, 180), 0.5f);
        }

        applyVignette(img, argb(255, 5, 2, 10), 0.15f);
    }

    // ========== BLACK HOLE ==========
    private static void generateBlackHole(NativeImage img) {
        float cx = FW / 2.0f, cy = FH * 0.42f; // center of the black hole

        // Deep space background with subtle blue-purple nebula
        for (int y = 0; y < FH; y++) {
            float t = (float) y / (FH - 1);
            for (int x = 0; x < FW; x++) {
                float n = fbm(x * 0.03f, y * 0.03f, 3);
                int r = (int)(2 + n * 8);
                int g = (int)(2 + n * 5);
                int b = (int)(5 + n * 15);
                fillFace(img, x, y, argb(255, r, g, b));
                fillEdges(img, y, argb(255, r, g, b));
                fillTop(img, x, argb(255, 2, 2, 6));
            }
        }

        // Distant stars
        for (int i = 0; i < 60; i++) {
            int sx = (int)(pseudoRand(i * 7, 33) * FW);
            int sy = (int)(pseudoRand(i * 13, 41) * FH);
            float bright = 0.3f + pseudoRand(i * 3, 19) * 0.7f;
            int c = (int)(180 + bright * 75);
            fillFace(img, sx, sy, argb(255, c, c, (int)(c * 0.95f)));
        }

        // Accretion disk — elliptical ring around the black hole
        for (int y = 0; y < FH; y++) {
            for (int x = 0; x < FW; x++) {
                float dx = x - cx, dy = (y - cy) * 2.8f; // stretch vertically to make ellipse
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                float angle = (float) Math.atan2(dy, dx);

                // Disk ring band (inner radius 14, outer radius 35)
                if (dist > 12 && dist < 38) {
                    float ringT = (dist - 12) / 26.0f; // 0 at inner, 1 at outer
                    float intensity = (float)(Math.sin(ringT * Math.PI) * 0.9f);

                    // Angular variation for spiral arms
                    float spiral = (float)(Math.sin(angle * 2 + dist * 0.15f) * 0.3f + 0.7f);
                    intensity *= spiral;

                    // Hot inner edge (white-yellow), cooler outer (orange-red)
                    int r, g, b;
                    if (ringT < 0.3f) {
                        // Inner: white-hot
                        float it = ringT / 0.3f;
                        r = lerp(255, 255, it);
                        g = lerp(250, 200, it);
                        b = lerp(230, 120, it);
                    } else if (ringT < 0.6f) {
                        // Middle: orange
                        float it = (ringT - 0.3f) / 0.3f;
                        r = lerp(255, 255, it);
                        g = lerp(200, 120, it);
                        b = lerp(120, 30, it);
                    } else {
                        // Outer: deep red fading to nothing
                        float it = (ringT - 0.6f) / 0.4f;
                        r = lerp(255, 120, it);
                        g = lerp(120, 20, it);
                        b = lerp(30, 5, it);
                        intensity *= (1.0f - it * 0.8f);
                    }

                    // Noise for turbulence in the disk
                    float n = fbm(x * 0.08f + angle * 2, y * 0.08f, 3);
                    intensity *= (0.7f + n * 0.6f);

                    blendFace(img, x, y, argb(255, r, g, b), Math.min(1.0f, intensity));
                }

                // Event horizon — pure black circle
                if (dist < 13) {
                    float fade = dist < 10 ? 1.0f : 1.0f - (dist - 10) / 3.0f;
                    blendFace(img, x, y, argb(255, 0, 0, 0), fade);
                }
            }
        }

        // Gravitational lensing — bright arc above and below the hole
        for (int x = 0; x < FW; x++) {
            float dx = x - cx;
            if (Math.abs(dx) < 20) {
                float lensY = cy - 6; // top arc
                float arcDist = Math.abs(dx);
                float bright = (1.0f - arcDist / 20.0f) * 0.4f;
                int ly = (int)(lensY - (float)Math.sqrt(Math.max(0, 20*20 - dx*dx)) * 0.15f);
                blendFace(img, x, ly, argb(255, 255, 220, 180), bright);
                blendFace(img, x, ly + 1, argb(255, 255, 200, 150), bright * 0.6f);

                // Bottom arc
                int ly2 = (int)(cy + 6 + (float)Math.sqrt(Math.max(0, 20*20 - dx*dx)) * 0.15f);
                blendFace(img, x, ly2, argb(255, 255, 200, 150), bright * 0.5f);
            }
        }

        // Jets — faint vertical beams from poles
        for (int y = 0; y < FH; y++) {
            float distFromCenter = Math.abs(y - cy);
            if (distFromCenter > 14) {
                float jetIntensity = 0.12f * (1.0f - Math.min(1.0f, distFromCenter / (FH * 0.5f)));
                for (int dx = -2; dx <= 2; dx++) {
                    float falloff = 1.0f - Math.abs(dx) / 3.0f;
                    blendFace(img, (int)cx + dx, y, argb(255, 180, 160, 255), jetIntensity * falloff);
                }
            }
        }

        applyVignette(img, argb(255, 0, 0, 0), 0.25f);
    }

    // ========== HELPERS ==========
    private static int argb(int a, int r, int g, int b) {
        return (a << 24) | (clamp(r) << 16) | (clamp(g) << 8) | clamp(b);
    }
    private static int clamp(int v) { return Math.max(0, Math.min(255, v)); }
    private static int lerp(int a, int b, float t) { t = Math.max(0, Math.min(1, t)); return (int)(a + (b - a) * t); }
    private static float lerpF(float a, float b, float t) { return a + (b - a) * t; }

    static void blend(NativeImage img, int x, int y, int color, float alpha) {
        int w = img.getWidth(), h = img.getHeight();
        if (x < 0 || x >= w || y < 0 || y >= h) return;
        int e = img.getColorArgb(x, y);
        if (((e >> 24) & 0xFF) == 0) return;
        int er = (e >> 16) & 0xFF, eg = (e >> 8) & 0xFF, eb = e & 0xFF;
        int nr = (color >> 16) & 0xFF, ng = (color >> 8) & 0xFF, nb = color & 0xFF;
        img.setColorArgb(x, y, argb(255, (int)(er + (nr - er) * alpha), (int)(eg + (ng - eg) * alpha), (int)(eb + (nb - eb) * alpha)));
    }
}
