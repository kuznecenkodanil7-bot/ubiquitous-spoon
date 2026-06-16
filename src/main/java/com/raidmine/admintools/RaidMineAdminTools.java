package com.raidmine.admintools;

import com.raidmine.admintools.auth.AuthManager;
import com.raidmine.admintools.auth.AuthScreen;
import com.raidmine.admintools.chat.ChatFilter;
import com.raidmine.admintools.chat.ChatListener;
import com.raidmine.admintools.config.ConfigManager;
import com.raidmine.admintools.database.DatabaseManager;
import com.raidmine.admintools.gui.AdminMenuScreen;
import com.raidmine.admintools.gui.NotificationOverlay;
import com.raidmine.admintools.keybind.KeybindManager;
import com.raidmine.admintools.remote.RemoteSyncClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RaidMineAdminTools implements ClientModInitializer {
    public static final String MOD_ID = "raidmine-admin-tools";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static RaidMineAdminTools instance;
    private ConfigManager configManager;
    private AuthManager authManager;
    private DatabaseManager databaseManager;
    private ChatFilter chatFilter;
    private ChatListener chatListener;
    private NotificationOverlay notificationOverlay;
    private KeybindManager keybindManager;
    private RemoteSyncClient remoteSyncClient;
    private boolean initialized = false;

    @Override
    public void onInitializeClient() {
        instance = this;
        LOGGER.info("Initializing RaidMine Admin Tools...");

        this.configManager = new ConfigManager();
        this.configManager.load();

        this.databaseManager = new DatabaseManager();
        this.databaseManager.initialize();

        this.authManager = new AuthManager();
        this.chatFilter = new ChatFilter();
        this.chatListener = new ChatListener();
        this.notificationOverlay = new NotificationOverlay();
        this.keybindManager = new KeybindManager();
        this.remoteSyncClient = new RemoteSyncClient();

        registerEvents();
        this.chatListener.register();
        this.notificationOverlay.register();
        this.keybindManager.register();
        this.remoteSyncClient.syncAsync();
        this.initialized = true;
        LOGGER.info("RaidMine Admin Tools initialized successfully!");
    }

    private void registerEvents() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof TitleScreen && !authManager.isAuthenticated()) {
                client.execute(() -> {
                    client.setScreen(new AuthScreen());
                });
            }
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient() && entity instanceof PlayerEntity target && authManager.isAuthenticated()) {
                MinecraftClient.getInstance().setScreen(new AdminMenuScreen(null, target.getName().getString()));
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
    }

    public static RaidMineAdminTools getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public AuthManager getAuthManager() {
        return authManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public ChatFilter getChatFilter() {
        return chatFilter;
    }

    public ChatListener getChatListener() {
        return chatListener;
    }

    public NotificationOverlay getNotificationOverlay() {
        return notificationOverlay;
    }

    public KeybindManager getKeybindManager() {
        return keybindManager;
    }

    public RemoteSyncClient getRemoteSyncClient() {
        return remoteSyncClient;
    }

    public boolean isInitialized() {
        return initialized;
    }
}
