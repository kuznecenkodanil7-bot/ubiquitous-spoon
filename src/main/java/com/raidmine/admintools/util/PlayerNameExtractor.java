package com.raidmine.admintools.util;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlayerNameExtractor {
    private static final Pattern PLAYER_NAME = Pattern.compile("(?<![A-Za-z0-9_])([A-Za-z0-9_]{3,16})(?![A-Za-z0-9_])");

    private PlayerNameExtractor() {}

    public static Optional<String> extract(Style style) {
        if (style == null) return Optional.empty();

        if (isPlayerName(style.getInsertion())) return Optional.of(style.getInsertion());

        ClickEvent click = style.getClickEvent();
        if (click != null) {
            Optional<String> fromClick = scan(click.toString());
            if (fromClick.isPresent()) return fromClick;
        }

        HoverEvent hover = style.getHoverEvent();
        return hover == null ? Optional.empty() : scan(hover.toString());
    }

    public static Optional<String> scan(String text) {
        if (text == null || text.isBlank()) return Optional.empty();
        Matcher matcher = PLAYER_NAME.matcher(text);
        return matcher.find() ? Optional.of(matcher.group(1)) : Optional.empty();
    }

    public static boolean isPlayerName(String value) {
        return value != null && PLAYER_NAME.matcher(value).matches();
    }
}
