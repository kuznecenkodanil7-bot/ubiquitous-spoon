package com.raidmine.admintools.mixin;

import com.raidmine.admintools.RaidMineAdminTools;
import com.raidmine.admintools.gui.AdminMenuScreen;
import com.raidmine.admintools.util.PlayerNameExtractor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void raidmine$openFromChat(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        if (click.button() != 0 || !RaidMineAdminTools.getInstance().getAuthManager().isAuthenticated()) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.inGameHud == null) return;

        ChatHud chatHud = client.inGameHud.getChatHud();
        Style style = chatHud.getTextStyleAt(click.x(), click.y());
        PlayerNameExtractor.extract(style).ifPresent(name -> {
            client.setScreen(new AdminMenuScreen(client.currentScreen, name));
            cir.setReturnValue(true);
        });
    }
}
