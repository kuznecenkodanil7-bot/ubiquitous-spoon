package com.raidmine.admintools.gui;

import com.raidmine.admintools.RaidMineAdminTools;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class NotificationOverlay {
    private String player = "";
    private String reason = "";
    private String message = "";
    private long until;

    public void register() {
        HudRenderCallback.EVENT.register((context, tickCounter) -> render(context));
    }

    public void push(String player, String reason, String message) {
        if (!RaidMineAdminTools.getInstance().getConfigManager().get().isNotificationsEnabled()) return;
        this.player = player;
        this.reason = reason;
        this.message = message;
        this.until = System.currentTimeMillis() + 7000L;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && RaidMineAdminTools.getInstance().getConfigManager().get().isSoundEnabled()) {
            client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 1.0f, 1.6f);
        }
    }

    private void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.textRenderer == null || System.currentTimeMillis() > until) return;

        int w = client.getWindow().getScaledWidth();
        int h = client.getWindow().getScaledHeight();
        int x = w / 2 - 150;
        int y = h - 92;
        float pulse = (float) (0.65 + Math.sin(System.currentTimeMillis() / 120.0) * 0.15);
        int accent = ((int) (pulse * 255) << 16) | 0x0033AA;

        context.fill(x - 2, y - 2, x + 302, y + 42, 0xAA050814);
        context.fill(x - 2, y - 2, x + 302, y + 1, 0xFF000000 | accent);
        context.fill(x - 2, y + 39, x + 302, y + 42, 0xFF000000 | accent);
        context.drawTextWithShadow(client.textRenderer, Text.literal("RaidMine Alert: " + reason), x + 8, y + 7, 0xFF7777);
        context.drawTextWithShadow(client.textRenderer, Text.literal("Игрок: " + player + " | ALT+F6 открыть наказание"), x + 8, y + 19, 0xFFFFFF);
        String trimmed = message.length() > 48 ? message.substring(0, 48) + "..." : message;
        context.drawTextWithShadow(client.textRenderer, Text.literal(trimmed), x + 8, y + 31, 0xB8C7FF);
    }
}
