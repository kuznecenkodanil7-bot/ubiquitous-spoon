package com.raidmine.admintools.keybind;

import com.raidmine.admintools.RaidMineAdminTools;
import com.raidmine.admintools.gui.AdminMenuScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeybindManager {
    private KeyBinding openPunishment;

    public void register() {
        openPunishment = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.raidmine-admin-tools.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F6,
                "category.raidmine-admin-tools"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openPunishment.wasPressed()) {
                if (isAltDown(client) && RaidMineAdminTools.getInstance().getAuthManager().isAuthenticated()) {
                    String target = RaidMineAdminTools.getInstance().getChatFilter().getLastViolator();
                    if (target == null || target.isBlank()) target = "unknown";
                    client.setScreen(new AdminMenuScreen(null, target));
                }
            }
        });
    }

    private boolean isAltDown(MinecraftClient client) {
        long window = client.getWindow().getHandle();
        return GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_ALT) == GLFW.GLFW_PRESS
                || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_ALT) == GLFW.GLFW_PRESS;
    }
}
