package com.raidmine.admintools.model;

import net.minecraft.text.Text;

public enum PunishmentType {
    WARN("warn", "/warn", Text.literal("Варн")),
    MUTE("mute", "/mute", Text.literal("Мут")),
    BAN("ban", "/ban", Text.literal("Бан")),
    IP_BAN("ipban", "/ipban", Text.literal("IP-Бан"));

    private final String id;
    private final String command;
    private final Text title;

    PunishmentType(String id, String command, Text title) {
        this.id = id;
        this.command = command;
        this.title = title;
    }

    public String id() { return id; }
    public String command() { return command; }
    public Text title() { return title; }
}
