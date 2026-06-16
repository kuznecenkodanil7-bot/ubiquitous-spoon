package com.raidmine.admintools.auth;

import com.raidmine.admintools.RaidMineAdminTools;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class AuthScreen extends Screen {
    private TextFieldWidget loginField;
    private TextFieldWidget passwordField;
    private ButtonWidget loginButton;
    private String errorMessage = "";
    private int errorTimer = 0;
    private boolean closing = false;

    public AuthScreen() {
        super(Text.translatable("raidmine.auth.title"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.loginField = new TextFieldWidget(this.textRenderer, centerX - 100, centerY - 60, 200, 20,
                Text.translatable("raidmine.auth.login"));
        this.loginField.setPlaceholder(Text.translatable("raidmine.auth.login"));
        this.loginField.setChangedListener(s -> errorMessage = "");

        this.passwordField = new TextFieldWidget(this.textRenderer, centerX - 100, centerY - 25, 200, 20,
                Text.translatable("raidmine.auth.password"));
        this.passwordField.setPlaceholder(Text.translatable("raidmine.auth.password"));
        this.passwordField.setChangedListener(s -> errorMessage = "");

        this.loginButton = ButtonWidget.builder(
                Text.translatable("raidmine.auth.login_btn"),
                this::tryLogin
        ).dimensions(centerX - 100, centerY + 20, 200, 30).build();

        this.addDrawableChild(this.loginField);
        this.addDrawableChild(this.passwordField);
        this.addDrawableChild(this.loginButton);
    }

    private void tryLogin(ButtonWidget button) {
        String username = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorMessage = "Заполните все поля!";
            errorTimer = 100;
            return;
        }

        if (RaidMineAdminTools.getInstance().getAuthManager().authenticate(username, password)) {
            errorMessage = "";
            closing = true;
            this.client.setScreen(null);
        } else {
            errorMessage = "Неверный логин или пароль!";
            errorTimer = 100;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        String title = "RaidMine Admin Tools";
        context.drawCenteredTextWithShadow(this.textRenderer, title, centerX, centerY - 110, 0x4A9EFF);

        String subtitle = "Авторизация персонала";
        context.drawCenteredTextWithShadow(this.textRenderer, subtitle, centerX, centerY - 95, 0xAAAAAA);

        if (!errorMessage.isEmpty()) {
            context.drawCenteredTextWithShadow(this.textRenderer, errorMessage, centerX, centerY + 60, 0xFF4444);
            errorTimer--;
            if (errorTimer <= 0) errorMessage = "";
        }

        context.drawCenteredTextWithShadow(this.textRenderer, "Login: " + loginField.getText(), centerX, centerY + 85, 0x666666);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 || keyCode == 335) {
            tryLogin(loginButton);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
