package com.raidmine.admintools.gui;

import com.raidmine.admintools.RaidMineAdminTools;
import com.raidmine.admintools.model.PresetReason;
import com.raidmine.admintools.model.PunishmentType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.List;

public class AdminMenuScreen extends Screen {
    private static final int PAGE_SIZE = 8;
    private final Screen parent;
    private final String playerName;
    private final List<PresetReason> presets = PresetReason.defaults();
    private TextFieldWidget customReason;
    private TextFieldWidget durationField;
    private PunishmentType selectedType = PunishmentType.MUTE;
    private String selectedReason = "Оскорбление";
    private String selectedDetails = "Выбери пункт правил слева или впиши свою причину.";
    private int page;
    private String status = "";

    public AdminMenuScreen(Screen parent, String playerName) {
        super(Text.literal("RaidMine Admin Tools"));
        this.parent = parent;
        this.playerName = playerName;
    }

    @Override
    protected void init() {
        clearChildren();
        int left = width / 2 - 205;
        int right = width / 2 + 12;
        int top = 62;

        customReason = new TextFieldWidget(textRenderer, right, top + 118, 190, 20, Text.literal("Причина"));
        customReason.setText(selectedReason);
        customReason.setMaxLength(180);
        addDrawableChild(customReason);

        durationField = new TextFieldWidget(textRenderer, right, top + 146, 86, 20, Text.literal("Срок"));
        durationField.setText(defaultDuration(selectedType));
        durationField.setMaxLength(16);
        addDrawableChild(durationField);

        int y = top;
        int start = page * PAGE_SIZE;
        for (int i = start; i < Math.min(start + PAGE_SIZE, presets.size()); i++) {
            PresetReason preset = presets.get(i);
            addDrawableChild(ButtonWidget.builder(Text.literal(preset.rule() + " " + preset.shortReason()), b -> selectPreset(preset))
                    .dimensions(left, y, 205, 20).build());
            y += 23;
        }

        addDrawableChild(ButtonWidget.builder(Text.literal("<"), b -> changePage(-1)).dimensions(left, top + 188, 24, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal(">"), b -> changePage(1)).dimensions(left + 30, top + 188, 24, 20).build());

        int buttonY = top + 174;
        for (PunishmentType type : PunishmentType.values()) {
            addDrawableChild(ButtonWidget.builder(type.title(), b -> {
                selectedType = type;
                durationField.setText(defaultDuration(type));
            }).dimensions(right, buttonY, 92, 20).build());
            right += 98;
            if (right > width / 2 + 115) {
                right = width / 2 + 12;
                buttonY += 24;
            }
        }

        int bottom = height - 32;
        addDrawableChild(ButtonWidget.builder(Text.literal("Отправить наказание"), b -> sendCommand()).dimensions(width / 2 - 205, bottom, 132, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Скопировать"), b -> copyCommand()).dimensions(width / 2 - 67, bottom, 95, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Настройки"), b -> client.setScreen(new SettingsScreen(this))).dimensions(width / 2 + 34, bottom, 90, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Закрыть"), b -> close()).dimensions(width / 2 + 130, bottom, 75, 20).build());
    }

    private void selectPreset(PresetReason preset) {
        selectedType = preset.type();
        selectedReason = preset.rule() + " " + preset.shortReason();
        selectedDetails = preset.details();
        customReason.setText(selectedReason);
        durationField.setText(preset.duration());
    }

    private void changePage(int delta) {
        int maxPage = Math.max(0, (presets.size() - 1) / PAGE_SIZE);
        page = Math.max(0, Math.min(maxPage, page + delta));
        init();
    }

    private String defaultDuration(PunishmentType type) {
        return switch (type) {
            case WARN -> "";
            case MUTE -> "2h";
            case BAN, IP_BAN -> "30d";
        };
    }

    private String buildCommand() {
        String reason = customReason.getText().trim();
        if (reason.isBlank()) {
            status = "Укажи причину";
            return null;
        }
        String duration = durationField.getText().trim();
        return selectedType.command() + " " + playerName + (duration.isBlank() ? " " : " " + duration + " ") + reason;
    }

    private void sendCommand() {
        String command = buildCommand();
        MinecraftClient mc = MinecraftClient.getInstance();
        if (command != null && mc.player != null) {
            mc.player.networkHandler.sendChatCommand(command.substring(1));
            mc.setScreen(null);
        }
    }

    private void copyCommand() {
        String command = buildCommand();
        if (command != null && client != null) {
            client.keyboard.setClipboard(command);
            status = "Команда скопирована";
        }
    }

    @Override
    public void close() {
        if (client != null) client.setScreen(parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        int x1 = width / 2 - 225;
        int y1 = 28;
        int x2 = width / 2 + 225;
        int y2 = height - 8;
        context.fill(x1, y1, x2, y2, 0xDD050711);
        context.fill(x1, y1, x2, y1 + 3, 0xFF248BFF);
        context.fill(x1, y2 - 3, x2, y2, 0xFFFF335D);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("RaidMine Admin Tools"), width / 2, 36, 0x66AAFF);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Цель: " + playerName + " | Выбрано: " + selectedType.title().getString()), width / 2, 49, 0xFFFFFF);

        context.drawTextWithShadow(textRenderer, Text.literal("Пункты правил"), width / 2 - 205, 53, 0xFF6688);
        context.drawTextWithShadow(textRenderer, Text.literal("Последние сообщения"), width / 2 + 12, 63, 0xFF6688);
        int msgY = 77;
        List<String> messages = RaidMineAdminTools.getInstance().getDatabaseManager().getRecentMessages(playerName);
        if (messages.isEmpty()) context.drawTextWithShadow(textRenderer, Text.literal("Нет сохраненных сообщений"), width / 2 + 12, msgY, 0x888888);
        for (int i = Math.max(0, messages.size() - 5); i < messages.size(); i++) {
            String msg = messages.get(i);
            if (msg.length() > 40) msg = msg.substring(0, 40) + "...";
            context.drawTextWithShadow(textRenderer, Text.literal(msg), width / 2 + 12, msgY, 0xD7E0FF);
            msgY += 11;
        }

        context.drawTextWithShadow(textRenderer, Text.literal("Описание: " + selectedDetails), width / 2 + 12, 167, 0xAAB8FF);
        context.drawTextWithShadow(textRenderer, Text.literal("Причина"), width / 2 + 12, 174, 0xFFFFFF);
        context.drawTextWithShadow(textRenderer, Text.literal("Срок"), width / 2 + 104, 202, 0xFFFFFF);
        if (!status.isBlank()) context.drawCenteredTextWithShadow(textRenderer, Text.literal(status), width / 2, height - 54, 0x66FF99);
        super.render(context, mouseX, mouseY, delta);
    }
}
