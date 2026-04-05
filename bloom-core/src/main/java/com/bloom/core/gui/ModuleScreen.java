package com.bloom.core.gui;

import com.bloom.core.BloomCore;
import com.bloom.core.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import java.util.List;

public class ModuleScreen extends Screen {
    private int scrollOffset = 0;

    public ModuleScreen() { super(Text.literal("Bloom Mods")); }

    @Override
    protected void init() {
        scrollOffset = 0;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int w = this.width;
        int h = this.height;
        int cx = w / 2;

        // Background
        context.fill(0, 0, w, h, 0xEE0d0810);

        // Title bar
        String title = "BLOOM MODS";
        int tw = this.textRenderer.getWidth(title);
        context.drawText(this.textRenderer, title, cx - tw / 2, 10, 0xFFFFFFFF, true);
        String sub = "Click to toggle modules";
        int sw = this.textRenderer.getWidth(sub);
        context.drawText(this.textRenderer, sub, cx - sw / 2, 22, 0xFF776070, true);
        context.fill(cx - 60, 34, cx + 60, 35, 0x33FFB0C0);

        // Module grid
        List<Module> modules = BloomCore.MODULES.getModules();
        int cols = Math.max(2, Math.min(4, (w - 40) / 130));
        int cardW = (w - 40 - (cols - 1) * 8) / cols;
        int cardH = 70;
        int gapX = 8;
        int gapY = 8;
        int gridW = cols * cardW + (cols - 1) * gapX;
        int startX = cx - gridW / 2;
        int startY = 42;

        for (int i = 0; i < modules.size(); i++) {
            Module m = modules.get(i);
            int col = i % cols;
            int row = i / cols;
            int x = startX + col * (cardW + gapX);
            int y = startY + row * (cardH + gapY) - scrollOffset;

            if (y + cardH < startY || y > h) continue;

            boolean hovered = mouseX >= x && mouseX <= x + cardW && mouseY >= y && mouseY <= y + cardH;
            boolean enabled = m.isEnabled();

            // Card background
            context.fill(x, y, x + cardW, y + cardH, hovered ? 0x33FFB0C0 : 0x15FFFFFF);
            context.fill(x, y, x + cardW, y + 1, hovered ? 0x44FFB0C0 : 0x11FFB0C0);

            if (enabled) {
                context.fill(x, y + cardH - 1, x + cardW, y + cardH, 0x4455DD88);
            }

            // Module name
            context.drawText(this.textRenderer, m.getName(), x + 8, y + 8, hovered ? 0xFFFFFFFF : 0xFFDDCCCC, true);

            // Description
            String desc = m.getDescription();
            if (this.textRenderer.getWidth(desc) > cardW - 16) {
                int maxLen = 0;
                for (int c = 0; c < desc.length(); c++) {
                    if (this.textRenderer.getWidth(desc.substring(0, c + 1)) > cardW - 24) break;
                    maxLen = c + 1;
                }
                desc = desc.substring(0, maxLen) + "..";
            }
            context.drawText(this.textRenderer, desc, x + 8, y + 22, 0xFF665566, true);

            // Toggle button
            int btnW = cardW - 16;
            int btnH = 16;
            int btnX = x + 8;
            int btnY = y + cardH - btnH - 8;
            boolean btnHovered = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;

            if (enabled) {
                context.fill(btnX, btnY, btnX + btnW, btnY + btnH, btnHovered ? 0xCC44CC77 : 0xBB33AA55);
                String eStr = "Enabled";
                context.drawText(this.textRenderer, eStr, btnX + btnW / 2 - this.textRenderer.getWidth(eStr) / 2, btnY + 4, 0xFFFFFFFF, true);
            } else {
                context.fill(btnX, btnY, btnX + btnW, btnY + btnH, btnHovered ? 0x55FFFFFF : 0x33FFFFFF);
                String dStr = "Disabled";
                context.drawText(this.textRenderer, dStr, btnX + btnW / 2 - this.textRenderer.getWidth(dStr) / 2, btnY + 4, 0xFF998899, true);
            }
        }

        // Bottom bar
        context.fill(0, h - 22, w, h, 0xCC0d0810);
        context.fill(0, h - 22, w, h - 21, 0x22FFB0C0);
        String info = modules.size() + " modules  |  ESC to close";
        context.drawText(this.textRenderer, info, cx - this.textRenderer.getWidth(info) / 2, h - 15, 0xFF776070, true);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        List<Module> modules = BloomCore.MODULES.getModules();
        int w = this.width;
        int cx = w / 2;
        int cols = Math.max(2, Math.min(4, (w - 40) / 130));
        int cardW = (w - 40 - (cols - 1) * 8) / cols;
        int cardH = 70;
        int gapX = 8;
        int gapY = 8;
        int gridW = cols * cardW + (cols - 1) * gapX;
        int startX = cx - gridW / 2;
        int startY = 42;

        for (int i = 0; i < modules.size(); i++) {
            int col = i % cols;
            int row = i / cols;
            int x = startX + col * (cardW + gapX);
            int y = startY + row * (cardH + gapY) - scrollOffset;
            if (mouseX >= x && mouseX <= x + cardW && mouseY >= y && mouseY <= y + cardH) {
                modules.get(i).toggle();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollOffset -= (int)(verticalAmount * 20);
        if (scrollOffset < 0) scrollOffset = 0;
        List<Module> modules = BloomCore.MODULES.getModules();
        int cols = Math.max(2, Math.min(4, (this.width - 40) / 130));
        int rows = (modules.size() + cols - 1) / cols;
        int maxScroll = Math.max(0, rows * 78 - (this.height - 80));
        if (scrollOffset > maxScroll) scrollOffset = maxScroll;
        return true;
    }

    @Override public boolean shouldPause() { return false; }
}
