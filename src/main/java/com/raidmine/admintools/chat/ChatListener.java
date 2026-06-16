package com.raidmine.admintools.chat;

import com.raidmine.admintools.RaidMineAdminTools;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;

public class ChatListener {
    public void register() {
        ClientReceiveMessageEvents.MODIFY_GAME.register((message, overlay) -> process(message));
    }

    private Text process(Text message) {
        if (!RaidMineAdminTools.getInstance().getAuthManager().isAuthenticated()) return message;
        return RaidMineAdminTools.getInstance().getChatFilter().highlight(message);
    }
}
