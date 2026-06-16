package com.raidmine.admintools.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.raidmine.admintools.RaidMineAdminTools;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private Path databasePath;
    private Map<String, StaffEntry> staffDatabase;
    private Map<String, String> rankPasswords;
    private Map<String, List<String>> recentMessages;
    private Map<String, List<TimedMessage>> timedMessages;

    public static class StaffEntry {
        public String username;
        public String rank;
        public long lastLogin;
        public boolean active;

        public StaffEntry() {}

        public StaffEntry(String username, String rank) {
            this.username = username;
            this.rank = rank;
            this.lastLogin = 0;
            this.active = true;
        }
    }

    public DatabaseManager() {
        this.databasePath = FabricLoader.getInstance().getConfigDir().resolve("raidmine-staff.json");
        this.staffDatabase = new ConcurrentHashMap<>();
        this.rankPasswords = new ConcurrentHashMap<>();
        this.recentMessages = new ConcurrentHashMap<>();
        this.timedMessages = new ConcurrentHashMap<>();
    }

    public void initialize() {
        if (Files.exists(databasePath)) {
            loadDatabase();
        } else {
            createDefaultDatabase();
        }

        if (rankPasswords.isEmpty()) {
            generateDefaultPasswords();
        }
    }

    private void loadDatabase() {
        try {
            String json = Files.readString(databasePath);
            DatabaseData data = GSON.fromJson(json, DatabaseData.class);
            if (data != null) {
                if (data.staff != null) {
                    for (StaffEntry entry : data.staff) {
                        staffDatabase.put(entry.username.toLowerCase(), entry);
                    }
                }
                if (data.rankPasswords != null) {
                    rankPasswords.putAll(data.rankPasswords);
                }
            }
        } catch (IOException e) {
            RaidMineAdminTools.LOGGER.error("Failed to load staff database", e);
        }
    }

    private void createDefaultDatabase() {
        rankPasswords.put("helper", generatePassword());
        rankPasswords.put("sthelper", generatePassword());
        rankPasswords.put("moder", generatePassword());
        rankPasswords.put("stmoder", generatePassword());
        rankPasswords.put("hmoder", generatePassword());
        rankPasswords.put("admin", generatePassword());
        staffDatabase.put("owner", new StaffEntry("owner", "admin"));
        staffDatabase.put("nekroz1990", new StaffEntry("nekroz1990", "admin"));
        saveDatabase();
    }

    private String generatePassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder("RM-");
        for (int i = 0; i < 14; i++) {
            builder.append(chars.charAt(random.nextInt(chars.length())));
        }
        return builder.toString();
    }

    private void generateDefaultPasswords() {
        if (rankPasswords.isEmpty()) {
            createDefaultDatabase();
        }
    }

    public void saveDatabase() {
        try {
            DatabaseData data = new DatabaseData();
            data.staff = new ArrayList<>(staffDatabase.values());
            data.rankPasswords = new HashMap<>(rankPasswords);
            Files.createDirectories(databasePath.getParent());
            Files.writeString(databasePath, GSON.toJson(data));
        } catch (IOException e) {
            RaidMineAdminTools.LOGGER.error("Failed to save staff database", e);
        }
    }

    public boolean addStaffMember(String username, String rank) {
        String key = username.toLowerCase();
        if (staffDatabase.containsKey(key)) return false;
        staffDatabase.put(key, new StaffEntry(username, rank));
        saveDatabase();
        return true;
    }

    public boolean removeStaffMember(String username) {
        String key = username.toLowerCase();
        if (!staffDatabase.containsKey(key)) return false;
        staffDatabase.remove(key);
        saveDatabase();
        return true;
    }

    public String getStaffRank(String username) {
        StaffEntry entry = staffDatabase.get(username.toLowerCase());
        return entry != null ? entry.rank : null;
    }

    public boolean isStaff(String username) {
        return staffDatabase.containsKey(username.toLowerCase());
    }

    public List<StaffEntry> getAllStaff() {
        return new ArrayList<>(staffDatabase.values());
    }

    public void setRankPassword(String rank, String password) {
        rankPasswords.put(rank.toLowerCase(), password);
        saveDatabase();
    }

    public String getRankPassword(String rank) {
        return rankPasswords.get(rank.toLowerCase());
    }

    public Map<String, String> loadCredentials() {
        return new HashMap<>(rankPasswords);
    }

    public void addRecentMessage(String username, String message) {
        String key = username.toLowerCase();
        recentMessages.computeIfAbsent(key, k -> Collections.synchronizedList(new ArrayList<>()));
        List<String> messages = recentMessages.get(key);
        messages.add(message);
        timedMessages.computeIfAbsent(key, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(new TimedMessage(message, System.currentTimeMillis()));
        int limit = RaidMineAdminTools.getInstance().getConfigManager().get().getRecentMessagesLimit();
        while (messages.size() > limit) {
            messages.remove(0);
        }
    }

    public List<TimedMessage> getTimedMessages(String username) {
        return timedMessages.getOrDefault(username.toLowerCase(), Collections.emptyList());
    }

    public List<String> getRecentMessages(String username) {
        return recentMessages.getOrDefault(username.toLowerCase(), Collections.emptyList());
    }

    public void syncFromRemote(String jsonData) {
        try {
            DatabaseData remoteData = GSON.fromJson(jsonData, DatabaseData.class);
            if (remoteData != null) {
                if (remoteData.staff != null) {
                    for (StaffEntry entry : remoteData.staff) {
                        staffDatabase.put(entry.username.toLowerCase(), entry);
                    }
                }
                if (remoteData.rankPasswords != null) {
                    rankPasswords.clear();
                    rankPasswords.putAll(remoteData.rankPasswords);
                }
                saveDatabase();
                RaidMineAdminTools.LOGGER.info("Database synced from remote successfully");
            }
        } catch (Exception e) {
            RaidMineAdminTools.LOGGER.error("Failed to sync database from remote", e);
        }
    }

    private static class DatabaseData {
        List<StaffEntry> staff;
        Map<String, String> rankPasswords;
    }

    public record TimedMessage(String message, long timeMillis) {}
}
