package com.raidmine.admintools.config;

public class ModConfig {
    private boolean chatFilterEnabled = true;
    private boolean notificationsEnabled = true;
    private boolean soundEnabled = true;
    private boolean autoUpdateEnabled = true;
    private String remoteSyncUrl = "https://raidmine.ru/api/staff/credentials";
    private String language = "ru_RU";
    private int recentMessagesLimit = 20;
    private boolean capsDetection = true;
    private boolean spamDetection = true;
    private boolean swearDetection = true;
    private boolean advertisingDetection = true;
    private int capsPercentageThreshold = 50;
    private int spamMessageLimit = 3;
    private int spamTimeWindow = 60;

    public boolean isChatFilterEnabled() { return chatFilterEnabled; }
    public void setChatFilterEnabled(boolean v) { this.chatFilterEnabled = v; }

    public boolean isNotificationsEnabled() { return notificationsEnabled; }
    public void setNotificationsEnabled(boolean v) { this.notificationsEnabled = v; }

    public boolean isSoundEnabled() { return soundEnabled; }
    public void setSoundEnabled(boolean v) { this.soundEnabled = v; }

    public boolean isAutoUpdateEnabled() { return autoUpdateEnabled; }
    public void setAutoUpdateEnabled(boolean v) { this.autoUpdateEnabled = v; }

    public String getRemoteSyncUrl() { return remoteSyncUrl; }
    public void setRemoteSyncUrl(String v) { this.remoteSyncUrl = v; }

    public String getLanguage() { return language; }
    public void setLanguage(String v) { this.language = v; }

    public int getRecentMessagesLimit() { return recentMessagesLimit; }
    public void setRecentMessagesLimit(int v) { this.recentMessagesLimit = v; }

    public boolean isCapsDetection() { return capsDetection; }
    public void setCapsDetection(boolean v) { this.capsDetection = v; }

    public boolean isSpamDetection() { return spamDetection; }
    public void setSpamDetection(boolean v) { this.spamDetection = v; }

    public boolean isSwearDetection() { return swearDetection; }
    public void setSwearDetection(boolean v) { this.swearDetection = v; }

    public boolean isAdvertisingDetection() { return advertisingDetection; }
    public void setAdvertisingDetection(boolean v) { this.advertisingDetection = v; }

    public int getCapsPercentageThreshold() { return capsPercentageThreshold; }
    public void setCapsPercentageThreshold(int v) { this.capsPercentageThreshold = v; }

    public int getSpamMessageLimit() { return spamMessageLimit; }
    public void setSpamMessageLimit(int v) { this.spamMessageLimit = v; }

    public int getSpamTimeWindow() { return spamTimeWindow; }
    public void setSpamTimeWindow(int v) { this.spamTimeWindow = v; }
}
