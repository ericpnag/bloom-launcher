package com.bloom.core.gui;

import com.bloom.core.module.modules.CosmeticsCape;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.text.Text;

public class CosmeticsScreen extends Screen {
    private final Screen parent;
    private int selectedCape = 0;
    private float playerRotation = 155f;
    private boolean draggingPlayer = false;
    private double lastDragX = 0;

    private static final String[] CAPE_NAMES = {
        "Cherry Blossom", "Midnight", "Frost", "Flame", "None"
    };
    private static final int[] CAPE_COLORS = {
        0xFFB7C9, 0x2d1b3d, 0x66CCFF, 0xFF6633, 0x333333
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
            for (int i = 0; i < CAPE_COLORS.length - 1; i++) {
                if (CosmeticsCape.capeColor == CAPE_COLORS[i]) {
                    selectedCape = i;
                    break;
                }
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int w = this.width;
        int h = this.height;
        int cx = w / 2;

        context.fill(0, 0, w, h, 0xEE0d0810);

        String title = "COSMETICS";
        int tw = this.textRenderer.getWidth(title);
        context.drawText(this.textRenderer, title, cx - tw / 2, 12, 0xFFFFFFFF, true);
        String sub = "Customize your look";
        int sw = this.textRenderer.getWidth(sub);
        context.drawText(this.textRenderer, sub, cx - sw / 2, 24, 0xFF998899, true);
        context.fill(cx - 50, 36, cx + 50, 37, 0x33FFB0C0);

        // === LEFT: Cape selection ===
        int leftW = w / 2 - 20;
        String capeTitle = "CAPES";
        int ctw = this.textRenderer.getWidth(capeTitle);
        context.drawText(this.textRenderer, capeTitle, leftW / 2 + 10 - ctw / 2, 44, 0xFFFFB7C9, true);

        int cardW = 60, cardH = 50, gap = 4, cols = 3;
        int gridW = cols * cardW + (cols - 1) * gap;
        int startX = leftW / 2 + 10 - gridW / 2;
        int startY = 58;

        for (int i = 0; i < CAPE_NAMES.length; i++) {
            int col = i % cols, row = i / cols;
            int x = startX + col * (cardW + gap);
            int y = startY + row * (cardH + gap);
            boolean hovered = mouseX >= x && mouseX <= x + cardW && mouseY >= y && mouseY <= y + cardH;
            boolean selected = i == selectedCape;

            context.fill(x, y, x + cardW, y + cardH, selected ? 0x55FFB0C0 : (hovered ? 0x33FFB0C0 : 0x18FFFFFF));
            if (selected) {
                context.fill(x, y, x + cardW, y + 1, 0xCCFFB0C0);
                context.fill(x, y + cardH - 1, x + cardW, y + cardH, 0xCCFFB0C0);
                context.fill(x, y, x + 1, y + cardH, 0xCCFFB0C0);
                context.fill(x + cardW - 1, y, x + cardW, y + cardH, 0xCCFFB0C0);
            }

            if (i < CAPE_COLORS.length - 1) {
                context.fill(x + cardW / 2 - 8, y + 6, x + cardW / 2 + 8, y + 22, 0xFF000000 | CAPE_COLORS[i]);
            } else {
                String xStr = "X";
                context.drawText(this.textRenderer, xStr, x + cardW / 2 - this.textRenderer.getWidth(xStr) / 2, y + 10, 0xFF665566, true);
            }

            String name = CAPE_NAMES[i];
            context.drawText(this.textRenderer, name, x + cardW / 2 - this.textRenderer.getWidth(name) / 2, y + 26, hovered || selected ? 0xFFFFFFFF : 0xFFBBAABB, true);
            if (selected) {
                String eq = "Equipped";
                context.drawText(this.textRenderer, eq, x + cardW / 2 - this.textRenderer.getWidth(eq) / 2, y + 38, 0xFF55DD88, true);
            }
        }

        // === RIGHT: Player preview ===
        int previewX = w / 2 + 10;
        int previewW = w / 2 - 20;
        int previewCx = previewX + previewW / 2;

        context.fill(previewX, 44, previewX + previewW, h - 50, 0x22FFFFFF);
        context.fill(previewX, 44, previewX + previewW, 45, 0x33FFB0C0);

        String pt = "YOUR PLAYER";
        context.drawText(this.textRenderer, pt, previewCx - this.textRenderer.getWidth(pt) / 2, 50, 0xFF998899, true);

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) {
            float lookX = previewCx - (float) Math.sin(Math.toRadians(playerRotation)) * 100;
            float lookY = h / 2 - 50;
            InventoryScreen.drawEntity(context, previewX + 10, 65, previewX + previewW - 10, h - 55,
                Math.min(previewW / 3, h - 150), 0.1f, lookX, lookY, mc.player);
        } else {
            String np = "Launch game to preview";
            context.drawText(this.textRenderer, np, previewCx - this.textRenderer.getWidth(np) / 2, h / 2, 0xFF665566, true);
        }

        String hint = "Drag to rotate";
        context.drawText(this.textRenderer, hint, previewCx - this.textRenderer.getWidth(hint) / 2, h - 62, 0xFF554455, true);

        // Back
        int backW = 80, backH = 18, backX2 = cx - backW / 2, backY = h - 35;
        boolean bh = mouseX >= backX2 && mouseX <= backX2 + backW && mouseY >= backY && mouseY <= backY + backH;
        context.fill(backX2, backY, backX2 + backW, backY + backH, bh ? 0x44FFB0C0 : 0x22FFFFFF);
        context.drawText(this.textRenderer, "Back", cx - this.textRenderer.getWidth("Back") / 2, backY + 5, bh ? 0xFFFFFFFF : 0xFFBBAABB, true);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        int w = this.width;

        // Player preview drag
        int previewX = w / 2 + 10, previewW = w / 2 - 20;
        if (mouseX >= previewX && mouseX <= previewX + previewW && mouseY >= 44 && mouseY <= this.height - 50) {
            draggingPlayer = true;
            lastDragX = mouseX;
            return true;
        }

        // Cape cards
        int leftW = w / 2 - 20;
        int cardW = 60, cardH = 50, gap = 4, cols = 3;
        int gridW = cols * cardW + (cols - 1) * gap;
        int startX = leftW / 2 + 10 - gridW / 2;
        int startY = 58;

        for (int i = 0; i < CAPE_NAMES.length; i++) {
            int col = i % cols, row = i / cols;
            int x = startX + col * (cardW + gap);
            int y = startY + row * (cardH + gap);
            if (mouseX >= x && mouseX <= x + cardW && mouseY >= y && mouseY <= y + cardH) {
                selectedCape = i;
                if (i == CAPE_NAMES.length - 1) { CosmeticsCape.showCape = false; }
                else { CosmeticsCape.showCape = true; CosmeticsCape.capeColor = CAPE_COLORS[i]; }
                return true;
            }
        }

        // Back
        int cx = w / 2, backW = 80, backH = 18, backX2 = cx - backW / 2, backY = this.height - 35;
        if (mouseX >= backX2 && mouseX <= backX2 + backW && mouseY >= backY && mouseY <= backY + backH) {
            client.setScreen(parent);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingPlayer) { playerRotation += (float)(mouseX - lastDragX) * 1.5f; lastDragX = mouseX; return true; }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingPlayer = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override public boolean shouldCloseOnEsc() { return true; }
    @Override public void close() { client.setScreen(parent); }
}
