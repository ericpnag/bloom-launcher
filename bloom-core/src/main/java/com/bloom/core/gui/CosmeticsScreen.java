package com.bloom.core.gui;

import com.bloom.core.module.modules.CosmeticsCape;
import net.minecraft.client.MinecraftClient;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CosmeticsScreen extends Screen {
    private final Screen parent;
    private int selectedCape = 0;

    private static final String[] CAPE_NAMES = {
        "Cherry Blossom", "Midnight", "Frost", "Flame",
        "Ocean", "Emerald", "Sunset", "Galaxy", "None"
    };
    private static final String[] CAPE_FILES = {
        "bloom_cape.png", "midnight_cape.png", "frost_cape.png", "flame_cape.png",
        "ocean_cape.png", "emerald_cape.png", "sunset_cape.png", "galaxy_cape.png", null
    };
    private static final int[][] CAPE_TOP = {
        {255,190,210}, {30,15,50}, {140,200,255}, {255,120,40},
        {20,80,160}, {30,150,70}, {255,150,80}, {15,8,30}, {40,40,40}
    };
    private static final int[][] CAPE_BOT = {
        {215,120,150}, {15,8,30}, {80,140,220}, {200,60,20},
        {10,40,100}, {15,100,40}, {120,50,150}, {5,2,15}, {30,30,30}
    };

    public CosmeticsScreen(Screen parent) {
        super(Text.literal("Cosmetics"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        if (!CosmeticsCape.showCape) {
            selectedCape = CAPE_NAMES.length - 1;
        } else {
            for (int i = 0; i < CAPE_FILES.length; i++) {
                if (CAPE_FILES[i] != null && CAPE_FILES[i].equals(CosmeticsCape.capeFile)) {
                    selectedCape = i; break;
                }
            }
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int w = this.width, h = this.height, cx = w / 2;
        ctx.fill(0, 0, w, h, 0xEE0a0611);

        String title = "WARDROBE";
        ctx.drawText(this.textRenderer, title, cx - this.textRenderer.getWidth(title) / 2, 8, 0xFFFFD1DC, false);
        ctx.fill(cx - 40, 20, cx + 40, 21, 0x22FFB7C9);

        // === LEFT: Cape grid ===
        int leftEnd = w / 2 - 5;
        ctx.drawText(this.textRenderer, "CAPES", leftEnd / 2 - this.textRenderer.getWidth("CAPES") / 2, 26, 0xFFFFB7C9, false);

        int cols = 3, cardW = 50, cardH = 56, gap = 4;
        int gridW = cols * cardW + (cols - 1) * gap;
        int startX = leftEnd / 2 - gridW / 2, startY = 38;

        for (int i = 0; i < CAPE_NAMES.length; i++) {
            int col = i % cols, row = i / cols;
            int x = startX + col * (cardW + gap), y = startY + row * (cardH + gap);
            boolean hov = mouseX >= x && mouseX <= x + cardW && mouseY >= y && mouseY <= y + cardH;
            boolean sel = i == selectedCape;

            ctx.fill(x, y, x + cardW, y + cardH, sel ? 0x44FFB7C9 : (hov ? 0x28FFB7C9 : 0x15FFFFFF));
            if (sel) {
                ctx.fill(x, y, x + cardW, y + 1, 0xAAFFB7C9);
                ctx.fill(x, y + cardH - 1, x + cardW, y + cardH, 0xAAFFB7C9);
                ctx.fill(x, y, x + 1, y + cardH, 0xAAFFB7C9);
                ctx.fill(x + cardW - 1, y, x + cardW, y + cardH, 0xAAFFB7C9);
            }

            if (i < CAPE_TOP.length - 1) {
                int px = x + 12, py = y + 4, pw = 26, ph = 28;
                for (int r2 = 0; r2 < ph; r2++) {
                    float t = (float) r2 / (ph - 1);
                    int rv = (int)(CAPE_TOP[i][0]*(1-t) + CAPE_BOT[i][0]*t);
                    int gv = (int)(CAPE_TOP[i][1]*(1-t) + CAPE_BOT[i][1]*t);
                    int bv = (int)(CAPE_TOP[i][2]*(1-t) + CAPE_BOT[i][2]*t);
                    int indent = Math.max(0, 3 - r2 / 5);
                    ctx.fill(px+indent, py+r2, px+pw-indent, py+r2+1, 0xFF000000|(rv<<16)|(gv<<8)|bv);
                }
            } else {
                ctx.drawText(this.textRenderer, "OFF", x + cardW/2 - this.textRenderer.getWidth("OFF")/2, y + 14, 0xFF5A4550, false);
            }

            String name = CAPE_NAMES[i]; int nw = this.textRenderer.getWidth(name);
            if (nw > cardW - 4) { name = name.substring(0, Math.min(name.length(), 6)) + ".."; nw = this.textRenderer.getWidth(name); }
            ctx.drawText(this.textRenderer, name, x + cardW/2 - nw/2, y + 35, hov||sel ? 0xFFF0E4E8 : 0xFF8A7080, false);
            if (sel) ctx.drawText(this.textRenderer, "Equipped", x + cardW/2 - this.textRenderer.getWidth("Equipped")/2, y + 46, 0xFF6EE7A0, false);
        }

        // === RIGHT: 2D Back preview with cape ===
        int previewX = w / 2 + 5, previewW = w / 2 - 15;
        int previewCx = previewX + previewW / 2;

        ctx.fill(previewX, 26, previewX + previewW, h - 30, 0x18FFFFFF);
        ctx.fill(previewX, 26, previewX + previewW, 27, 0x22FFB7C9);
        ctx.drawText(this.textRenderer, "BACK VIEW", previewCx - this.textRenderer.getWidth("BACK VIEW")/2, 30, 0xFF8A7080, false);

        // Draw 2D back preview — always works (no drawEntity issues)
        int charX = previewCx, charY = h / 2 + 20;
        int s = 5; // scale

        // Draw cape behind character
        if (selectedCape < CAPE_FILES.length - 1) {
            int capeW = 10 * s, capeH = 16 * s;
            int cX = charX - capeW / 2, cY = charY - 26 * s;
            for (int row = 0; row < capeH; row++) {
                float t = (float) row / (capeH - 1);
                int rv = (int)(CAPE_TOP[selectedCape][0]*(1-t) + CAPE_BOT[selectedCape][0]*t);
                int gv = (int)(CAPE_TOP[selectedCape][1]*(1-t) + CAPE_BOT[selectedCape][1]*t);
                int bv = (int)(CAPE_TOP[selectedCape][2]*(1-t) + CAPE_BOT[selectedCape][2]*t);
                int widen = row / (s * 2);
                ctx.fill(cX - widen, cY + row, cX + capeW + widen, cY + row + 1, 0xFF000000|(rv<<16)|(gv<<8)|bv);
            }
        }

        // Draw player back using skin texture
        MinecraftClient mc = MinecraftClient.getInstance();
        Identifier skinTex = null;
        try {
            java.util.UUID uuid = mc.getSession().getUuidOrNull();
            if (uuid != null) {
                var profile = new com.mojang.authlib.GameProfile(uuid, mc.getSession().getUsername());
                var st = mc.getSkinProvider().getSkinTextures(profile);
                if (st != null && st.texture() != null) skinTex = st.texture();
            }
        } catch (Exception ignored) {}

        if (skinTex != null) {
            
            // Back of skin: head(24,8), body(32,20), arms(52,20 / 44,52), legs(12,20 / 28,52)
            ctx.drawTexture(skinTex, charX-4*s, charY-32*s, 24f, 8f, 8*s, 8*s, 64, 64); // head back
            ctx.drawTexture(skinTex, charX-4*s, charY-24*s, 32f, 20f, 8*s, 12*s, 64, 64); // body back
            ctx.drawTexture(skinTex, charX-8*s, charY-24*s, 52f, 20f, 4*s, 12*s, 64, 64); // right arm back
            ctx.drawTexture(skinTex, charX+4*s, charY-24*s, 44f, 52f, 4*s, 12*s, 64, 64); // left arm back
            ctx.drawTexture(skinTex, charX-4*s, charY-12*s, 12f, 20f, 4*s, 12*s, 64, 64); // right leg back
            ctx.drawTexture(skinTex, charX, charY-12*s, 28f, 52f, 4*s, 12*s, 64, 64); // left leg back
        } else {
            // Fallback Steve silhouette
            ctx.fill(charX-4*s, charY-32*s, charX+4*s, charY-24*s, 0xFFD4A574);
            ctx.fill(charX-4*s, charY-24*s, charX+4*s, charY-12*s, 0xFF3B3B3B);
            ctx.fill(charX-8*s, charY-24*s, charX-4*s, charY-12*s, 0xFF3B3B3B);
            ctx.fill(charX+4*s, charY-24*s, charX+8*s, charY-12*s, 0xFF3B3B3B);
            ctx.fill(charX-4*s, charY-12*s, charX, charY, 0xFF2A2A2A);
            ctx.fill(charX, charY-12*s, charX+4*s, charY, 0xFF2A2A2A);
        }

        // Back button
        int backW = 60, backH = 14, bx = cx - backW/2, by = h - 22;
        boolean bh = mouseX >= bx && mouseX <= bx+backW && mouseY >= by && mouseY <= by+backH;
        ctx.fill(bx, by, bx+backW, by+backH, bh ? 0x33FFB7C9 : 0x18FFFFFF);
        ctx.drawText(this.textRenderer, "Back", cx - this.textRenderer.getWidth("Back")/2, by + 3, bh ? 0xFFF0E4E8 : 0xFF8A7080, false);

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX2, double mouseY2, int button) {
        if (button != 0) return super.mouseClicked(mouseX2, mouseY2, button); double mx = mouseX2, my = mouseY2;
        int w = this.width, cx = w / 2;

        // Cape cards
        int leftEnd = w / 2 - 5, cols = 3, cardW = 50, cardH = 56, gap = 4;
        int gridW = cols * cardW + (cols - 1) * gap;
        int startX = leftEnd / 2 - gridW / 2, startY = 38;
        for (int i = 0; i < CAPE_NAMES.length; i++) {
            int col = i % cols, row = i / cols;
            int x = startX + col * (cardW + gap), y = startY + row * (cardH + gap);
            if (mx >= x && mx <= x + cardW && my >= y && my <= y + cardH) {
                selectedCape = i;
                if (CAPE_FILES[i] == null) { CosmeticsCape.showCape = false; }
                else {
                    CosmeticsCape.showCape = true; CosmeticsCape.capeFile = CAPE_FILES[i];
                    Identifier id = Identifier.of("bloom-core", "textures/cape/" + CAPE_FILES[i]);
                    MinecraftClient.getInstance().getTextureManager().registerTexture(id, new ResourceTexture(id));
                }
                return true;
            }
        }

        // Back button
        int backW = 60, backH = 14, bx = cx - backW/2, by = this.height - 22;
        if (mx >= bx && mx <= bx+backW && my >= by && my <= by+backH) { client.setScreen(parent); return true; }
        return super.mouseClicked(mouseX2, mouseY2, button);
    }

    @Override public boolean shouldCloseOnEsc() { return true; }
    @Override public void close() { client.setScreen(parent); }
}
