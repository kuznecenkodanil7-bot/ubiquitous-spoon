package com.raidmine.admintools.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raidmine.admintools.RaidMineAdminTools;
import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private ModConfig config;
    private Path configPath;

    public ConfigManager() {
        this.configPath = FabricLoader.getInstance().getConfigDir().resolve("raidmine-admin-tools.json");
    }

    public void load() {
        if (Files.exists(configPath)) {
            try {
                String json = Files.readString(configPath);
                config = GSON.fromJson(json, ModConfig.class);
            } catch (IOException e) {
                RaidMineAdminTools.LOGGER.error("Failed to load config", e);
                config = new ModConfig();
            }
        } else {
            config = new ModConfig();
            save();
        }
    }

    public void save() {
        try {
            Files.createDirectories(configPath.getParent());
            String json = GSON.toJson(config);
            Files.writeString(configPath, json);
        } catch (IOException e) {
            RaidMineAdminTools.LOGGER.error("Failed to save config", e);
        }
    }

    public ModConfig get() {
        return config;
    }
}
