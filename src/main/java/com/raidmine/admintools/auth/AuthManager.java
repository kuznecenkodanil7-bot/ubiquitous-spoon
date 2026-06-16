package com.raidmine.admintools.auth;

import com.raidmine.admintools.RaidMineAdminTools;
import com.raidmine.admintools.database.DatabaseManager;

import java.security.SecureRandom;
import java.util.*;

public class AuthManager {
    private boolean authenticated = false;
    private String currentUsername = "";
    private String currentRank = "";
    private Map<String, String> staffPasswords;
    private Map<String, String> rankColors;
    private String sessionToken;

    public enum Rank {
        HELPER("helper", "§7[Helper]"),
        STHELPER("sthelper", "§b[StHelper]"),
        MODER("moder", "§a[Moder]"),
        STMODER("stmoder", "§2[StModer]"),
        HMODER("hmoder", "§c[HModer]"),
        ADMIN("admin", "§4[Admin]");

        private final String id;
        private final String display;

        Rank(String id, String display) {
            this.id = id;
            this.display = display;
        }

        public String getId() { return id; }
        public String getDisplay() { return display; }

        public static Rank fromId(String id) {
            for (Rank r : values()) {
                if (r.id.equalsIgnoreCase(id)) return r;
            }
            return HELPER;
        }
    }

    public AuthManager() {
        rankColors = new HashMap<>();
        rankColors.put("helper", "§7");
        rankColors.put("sthelper", "§b");
        rankColors.put("moder", "§a");
        rankColors.put("stmoder", "§2");
        rankColors.put("hmoder", "§c");
        rankColors.put("admin", "§4");

        loadCredentials();
    }

    private void loadCredentials() {
        staffPasswords = RaidMineAdminTools.getInstance().getDatabaseManager().loadCredentials();
    }

    public boolean authenticate(String username, String password) {
        DatabaseManager db = RaidMineAdminTools.getInstance().getDatabaseManager();
        String rank = db.getStaffRank(username);

        if (rank == null) return false;

        String expectedPassword = db.getRankPassword(rank);
        if (expectedPassword == null) return false;

        if (password.equals(expectedPassword)) {
            this.authenticated = true;
            this.currentUsername = username;
            this.currentRank = rank;
            this.sessionToken = generateToken();
            db.addStaffMember(username, rank);
            RaidMineAdminTools.LOGGER.info("Staff {} authenticated as {}", username, rank);
            return true;
        }
        return false;
    }

    public void logout() {
        this.authenticated = false;
        this.currentUsername = "";
        this.currentRank = "";
        this.sessionToken = null;
    }

    private String generateToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public String generateRankPassword(String rank) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        String password = sb.toString();
        RaidMineAdminTools.getInstance().getDatabaseManager().setRankPassword(rank, password);
        return password;
    }

    public boolean isAuthenticated() { return authenticated; }
    public String getCurrentUsername() { return currentUsername; }
    public String getCurrentRank() { return currentRank; }
    public String getSessionToken() { return sessionToken; }
    public String getRankColor(String rank) { return rankColors.getOrDefault(rank, "§f"); }
    public Map<String, String> getStaffPasswords() { return staffPasswords; }

    public boolean hasPermission(String... allowedRanks) {
        if (!authenticated) return false;
        for (String rank : allowedRanks) {
            if (currentRank.equalsIgnoreCase(rank)) return true;
        }
        return false;
    }

    public boolean canUseCommand(String command) {
        if (!authenticated) return false;
        switch (currentRank.toLowerCase()) {
            case "admin": return true;
            case "hmoder": return true;
            case "stmoder": return !command.startsWith("config");
            case "moder": return !command.startsWith("config") && !command.startsWith("admin");
            case "sthelper": return command.startsWith("warn") || command.startsWith("kick") || command.startsWith("mute");
            case "helper": return command.equals("warn") || command.equals("kick");
            default: return false;
        }
    }
}
