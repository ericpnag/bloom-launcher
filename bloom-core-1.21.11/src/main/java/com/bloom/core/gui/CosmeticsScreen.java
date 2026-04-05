package com.bloom.core.gui;

import com.bloom.core.module.modules.CosmeticsCape;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
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

        // Background
        context.fill(0, 0, w, h, 0xEE0d0810);

        // Title
        String title = "COSMETICS";
        int tw = this.textRenderer.getWidth(title);
        context.drawText(this.textRenderer, title, cx - tw / 2, 12, 0xFFFFFFFF, true);

        String sub = "Customize your look";
        int sw = this.textRenderer.getWidth(sub);
        context.drawText(this.textRenderer, sub, cx - sw / 2, 24, 0xFF998899, true);
        context.fill(cx - 50, 36, cx + 50, 37, 0x33FFB0C0);

        // === LEFT SIDE: Cape selection ===
        int leftW = w / 2 - 20;

        String capeTitle = "CAPES";
        int ctw = this.textRenderer.getWidth(capeTitle);
        context.drawText(this.textRenderer, capeTitle, leftW / 2 + 10 - ctw / 2, 44, 0xFFFFB7C9, true);

        int cardW = 60;
        int cardH = 50;
        int gap = 4;
        int cols = 3;
        int rows = 2;
        int gridW = cols * cardW + (cols - 1) * gap;
        int startX = leftW / 2 + 10 - gridW / 2;
        int startY = 58;

        for (int i = 0; i < CAPE_NAMES.length; i++) {
            int col = i % cols;
            int row = i / cols;
            int x = startX + col * (cardW + gap);
            int y = startY + row * (cardH + gap);
            boolean hovered = mouseX >= x && mouseX <= x + cardW && mouseY >= y && mouseY <= y + cardH;
            boolean selected = i == selectedCape;

            // Card
            int bg = selected ? 0x55FFB0C0 : (hovered ? 0x33FFB0C0 : 0x18FFFFFF);
            context.fill(x, y, x + cardW, y + cardH, bg);

            if (selected) {
                context.fill(x, y, x + cardW, y + 1, 0xCCFFB0C0);
                context.fill(x, y + cardH - 1, x + cardW, y + cardH, 0xCCFFB0C0);
                context.fill(x, y, x + 1, y + cardH, 0xCCFFB0C0);
                context.fill(x + cardW - 1, y, x + cardW, y + cardH, 0xCCFFB0C0);
            }

            // Color swatch
            if (i < CAPE_COLORS.length - 1) {
                int sx = x + cardW / 2 - 8;
                int sy = y + 6;
                context.fill(sx, sy, sx + 16, sy + 16, 0xFF000000 | CAPE_COLORS[i]);
            } else {
                String xStr = "X";
                int xw = this.textRenderer.getWidth(xStr);
                context.drawText(this.textRenderer, xStr, x + cardW / 2 - xw / 2, y + 10, 0xFF665566, true);
            }

            // Name
            String name = CAPE_NAMES[i];
            int nw = this.textRenderer.getWidth(name);
            int nameX = x + cardW / 2 - nw / 2;
            context.drawText(this.textRenderer, name, nameX, y + 26, hovered || selected ? 0xFFFFFFFF : 0xFFBBAABB, true);

            if (selected) {
                String eq = "Equipped";
                int eqw = this.textRenderer.getWidth(eq);
                context.drawText(this.textRenderer, eq, x + cardW / 2 - eqw / 2, y + 38, 0xFF55DD88, true);
            }
        }

        // === RIGHT SIDE: Player preview ===
        int previewX = w / 2 + 10;
        int previewW = w / 2 - 20;
        int previewCx = previewX + previewW / 2;

        // Preview panel background
        context.fill(previewX, 44, previewX + previewW, h - 50, 0x22FFFFFF);
        context.fill(previewX, 44, previewX + previewW, 45, 0x33FFB0C0);

        String previewTitle = "YOUR PLAYER";
        int ptw = this.textRenderer.getWidth(previewTitle);
        context.drawText(this.textRenderer, previewTitle, previewCx - ptw / 2, 50, 0xFF998899, true);

        // Draw player entity
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) {
            int entityX = previewCx;
            int entityY = h - 80;
            int entitySize = Math.min(previewW / 3, (h - 150));

            // Render the player from behind to show cape
            float lookX = entityX - (float)Math.sin(Math.toRadians(playerRotation)) * 100;
            float lookY = entityY - 50;

            InventoryScreen.drawEntity(
                context,
                previewX + 10, 65,
                previewX + previewW - 10, h - 55,
                entitySize,
                0.1f,
                lookX, lookY,
                mc.player
            );
        } else {
            String noPlayer = "Launch game to preview";
            int npw = this.textRenderer.getWidth(noPlayer);
            context.drawText(this.textRenderer, noPlayer, previewCx - npw / 2, h / 2, 0xFF665566, true);
        }

        // Drag hint
        String hint = "Drag to rotate";
        int hw = this.textRenderer.getWidth(hint);
        context.drawText(this.textRenderer, hint, previewCx - hw / 2, h - 62, 0xFF554455, true);

        // Back button
        int backW = 80;
        int backH = 18;
        int backX = cx - backW / 2;
        int backY = h - 35;
        boolean backHovered = mouseX >= backX && mouseX <= backX + backW && mouseY >= backY && mouseY <= backY + backH;
        context.fill(backX, backY, backX + backW, backY + backH, backHovered ? 0x44FFB0C0 : 0x22FFFFFF);
        if (backHovered) context.fill(backX, backY, backX + backW, backY + 1, 0x44FFB0C0);
        String backStr = "Back";
        int bsw = this.textRenderer.getWidth(backStr);
        context.drawText(this.textRenderer, backStr, cx - bsw / 2, backY + 5, backHovered ? 0xFFFFFFFF : 0xFFBBAABB, true);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(Click click, boolean bl) {
        double mouseX = click.x();
        double mouseY = click.y();
        int w = this.width;

        // Check if clicking in player preview area for drag
        int previewX = w / 2 + 10;
        int previewW = w / 2 - 20;
        if (mouseX >= previewX && mouseX <= previewX + previewW && mouseY >= 44 && mouseY <= this.height - 50) {
            draggingPlayer = true;
            lastDragX = mouseX;
            return true;
        }

        // Cape cards
        int leftW = w / 2 - 20;
        int cardW = 60;
        int cardH = 50;
        int gap = 4;
        int cols = 3;
        int gridW = cols * cardW + (cols - 1) * gap;
        int startX = leftW / 2 + 10 - gridW / 2;
        int startY = 58;

        for (int i = 0; i < CAPE_NAMES.length; i++) {
            int col = i % cols;
            int row = i / cols;
            int x = startX + col * (cardW + gap);
            int y = startY + row * (cardH + gap);
            if (mouseX >= x && mouseX <= x + cardW && mouseY >= y && mouseY <= y + cardH) {
                selectedCape = i;
                if (i == CAPE_NAMES.length - 1) {
                    CosmeticsCape.showCape = false;
                } else {
                    CosmeticsCape.showCape = true;
                    CosmeticsCape.capeColor = CAPE_COLORS[i];
                }
                return true;
            }
        }

        // Back button
        int cx = w / 2;
        int backW = 80;
        int backH = 18;
        int backX = cx - backW / 2;
        int backY = this.height - 35;
        if (mouseX >= backX && mouseX <= backX + backW && mouseY >= backY && mouseY <= backY + backH) {
            client.setScreen(parent);
            return true;
        }

        return super.mouseClicked(click, bl);
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        if (draggingPlayer) {
            playerRotation += (float) deltaX * 1.5f;
            return true;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        draggingPlayer = false;
        return super.mouseReleased(click);
    }

    @Override
    public boolean shouldCloseOnEsc() { return true; }

    @Override
    public void close() { client.setScreen(parent); }
}
