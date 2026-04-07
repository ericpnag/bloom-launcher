package com.bloom.core.gui;

import com.bloom.core.module.modules.CosmeticsCape;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CosmeticsScreen extends Screen {
    private final Screen parent;
    private int selectedCape = 0;
    private boolean showBack = false;

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

        // 3D avatar preview — front view with cape info
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) {
            if (CosmeticsCape.showCape && CosmeticsCape.capeFile != null) {
                Identifier capeId = Identifier.of("bloom-core", "textures/cape/" + CosmeticsCape.capeFile);
                mc.getTextureManager().registerTexture(capeId, new ResourceTexture(capeId));
            }
            int entitySize = Math.min(previewW / 3, (h - 120));
            int x1 = previewX + 8, y1 = 44, x2 = previewX + previewW - 8, y2 = h - 40;
            // Both views use 3D drawEntity with mouse control
            // Back view: offset mouseX so default position shows back, move mouse to rotate
            float lookX, lookY;
            if (showBack) {
                // Offset mouseX: when mouse is at center of preview, entity faces away
                int centerX = (x1 + x2) / 2;
                lookX = (float)(2 * centerX - mouseX); // mirror around center
                lookY = (float)(y1 + (y2 - y1) / 3); // look at upper third to keep head level
            } else {
                lookX = (float)mouseX;
                lookY = (float)mouseY;
            }
            InventoryScreen.drawEntity(ctx, x1, y1, x2, y2, entitySize, 0.1f, lookX, lookY, mc.player);
        } else {
            String np = "Join a world to preview";
            ctx.drawText(this.textRenderer, np, previewCx - this.textRenderer.getWidth(np)/2, h/2, 0xFF5A4550, false);
        }

        // Cape name label
        if (selectedCape < CAPE_FILES.length - 1) {
            String capeName = "Cape: " + CAPE_NAMES[selectedCape];
            ctx.drawText(this.textRenderer, capeName, previewCx - this.textRenderer.getWidth(capeName)/2, h - 55, 0xFF6EE7A0, false);
        }

        // Front/Back toggle button
        int toggleW = 80, toggleH = 14;
        int toggleX = previewCx - toggleW / 2, toggleY = h - 42;
        boolean toggleHov = mouseX >= toggleX && mouseX <= toggleX + toggleW && mouseY >= toggleY && mouseY <= toggleY + toggleH;
        ctx.fill(toggleX, toggleY, toggleX + toggleW, toggleY + toggleH, toggleHov ? 0x44FFB7C9 : 0x22FFFFFF);
        ctx.fill(toggleX, toggleY, toggleX + toggleW, toggleY + 1, toggleHov ? 0x33FFB7C9 : 0x11FFFFFF);
        String toggleText = showBack ? "Show Front" : "Show Back";
        ctx.drawText(this.textRenderer, toggleText, previewCx - this.textRenderer.getWidth(toggleText)/2, toggleY + 3, toggleHov ? 0xFFF0E4E8 : 0xFF8A7080, false);

        // Front/Back toggle button render area for click detection later

        // Back button
        int backW = 60, backH = 14, bx = cx - backW/2, by = h - 22;
        boolean bh = mouseX >= bx && mouseX <= bx+backW && mouseY >= by && mouseY <= by+backH;
        ctx.fill(bx, by, bx+backW, by+backH, bh ? 0x33FFB7C9 : 0x18FFFFFF);
        ctx.drawText(this.textRenderer, "Back", cx - this.textRenderer.getWidth("Back")/2, by + 3, bh ? 0xFFF0E4E8 : 0xFF8A7080, false);

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(Click click, boolean bl) {
        double mx = click.x(), my = click.y();
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

        // Front/Back toggle
        int previewCx2 = (w / 2 + 5) + (w / 2 - 15) / 2;
        int toggleW = 80, toggleH = 14;
        int toggleX = previewCx2 - toggleW / 2, toggleY = this.height - 42;
        if (mx >= toggleX && mx <= toggleX + toggleW && my >= toggleY && my <= toggleY + toggleH) {
            showBack = !showBack; return true;
        }

        // Back button
        int backW = 60, backH = 14, bx = cx - backW/2, by = this.height - 22;
        if (mx >= bx && mx <= bx+backW && my >= by && my <= by+backH) { client.setScreen(parent); return true; }
        return super.mouseClicked(click, bl);
    }

    @Override public boolean shouldCloseOnEsc() { return true; }
    @Override public void close() { client.setScreen(parent); }
}
