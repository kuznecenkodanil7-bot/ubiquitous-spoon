package com.raidmine.admintools.gui;

import com.raidmine.admintools.RaidMineAdminTools;
import com.raidmine.admintools.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class SettingsScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget syncUrl;
    private String status = "";

    public SettingsScreen(Screen parent) {
        super(Text.literal("Настройки RaidMine Tools"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        clearChildren();
        ModConfig cfg = RaidMineAdminTools.getInstance().getConfigManager().get();
        int cx = width / 2;
        int y = height / 2 - 86;

        syncUrl = new TextFieldWidget(textRenderer, cx - 160, y, 320, 20, Text.literal("URL базы"));
        syncUrl.setText(cfg.getRemoteSyncUrl());
        syncUrl.setMaxLength(512);
        addDrawableChild(syncUrl);

        addDrawableChild(toggle("Фильтр чата", cfg.isChatFilterEnabled(), cx - 160, y + 28, b -> cfg.setChatFilterEnabled(!cfg.isChatFilterEnabled())));
        addDrawableChild(toggle("Уведомления", cfg.isNotificationsEnabled(), cx + 5, y + 28, b -> cfg.setNotificationsEnabled(!cfg.isNotificationsEnabled())));
        addDrawableChild(toggle("Звук", cfg.isSoundEnabled(), cx - 160, y + 56, b -> cfg.setSoundEnabled(!cfg.isSoundEnabled())));
        addDrawableChild(toggle("Авто-синхронизация", cfg.isAutoUpdateEnabled(), cx + 5, y + 56, b -> cfg.setAutoUpdateEnabled(!cfg.isAutoUpdateEnabled())));
        addDrawableChild(ButtonWidget.builder(Text.literal("Сохранить"), b -> save()).dimensions(cx - 160, y + 92, 155, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Синхронизировать"), b -> sync()).dimensions(cx + 5, y + 92, 155, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Назад"), b -> close()).dimensions(cx - 80, y + 122, 160, 20).build());
    }

    private ButtonWidget toggle(String label, boolean value, int x, int y, ButtonWidget.PressAction action) {
        return ButtonWidget.builder(Text.literal(label + ": " + (value ? "ON" : "OFF")), b -> {
            action.onPress(b);
            init();
        }).dimensions(x, y, 155, 20).build();
    }

    private void save() {
        RaidMineAdminTools.getInstance().getConfigManager().get().setRemoteSyncUrl(syncUrl.getText().trim());
        RaidMineAdminTools.getInstance().getConfigManager().save();
        status = "Настройки сохранены";
    }

    private void sync() {
        save();
        RaidMineAdminTools.getInstance().getRemoteSyncClient().syncAsync();
        status = "Синхронизация запущена";
    }

    @Override
    public void close() {
        if (client != null) client.setScreen(parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        context.fill(width / 2 - 190, height / 2 - 120, width / 2 + 190, height / 2 + 95, 0xCC060914);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 2 - 108, 0x66AAFF);
        context.drawTextWithShadow(textRenderer, Text.literal("Удаленная JSON-база staff/rankPasswords"), width / 2 - 160, height / 2 - 98, 0xAAB8FF);
        if (!status.isBlank()) context.drawCenteredTextWithShadow(textRenderer, Text.literal(status), width / 2, height / 2 + 75, 0x66FF99);
        super.render(context, mouseX, mouseY, delta);
    }
}
