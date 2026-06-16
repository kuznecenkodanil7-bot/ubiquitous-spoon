package com.raidmine.admintools.chat;

import com.raidmine.admintools.RaidMineAdminTools;
import com.raidmine.admintools.database.DatabaseManager;
import com.raidmine.admintools.util.PlayerNameExtractor;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class ChatFilter {
    private static final Pattern LINK = Pattern.compile("(?i)(https?://|www\\.|\\b\\d{1,3}(?:\\.\\d{1,3}){3}\\b|\\b[a-z0-9-]+\\.(ru|com|net|org|gg|me)\\b)");
    private static final List<String> BAD_WORDS = List.of(
            "хуй", "хуе", "хуё", "пизд", "еба", "ебл", "ёба", "бля", "сука", "долбо", "шлюх",
            "мамк", "мать", "родн", "слава ", "негр", "хохл", "чурк", "докс", "сват", "чит", "xray", "baritone"
    );
    private String lastViolator = "";
    private String lastReason = "";

    public Text highlight(Text message) {
        String raw = message.getString();
        if (!RaidMineAdminTools.getInstance().getConfigManager().get().isChatFilterEnabled()) return message;

        recordMessage(raw);
        Analysis analysis = analyze(raw);
        if (analysis.violation()) {
            RaidMineAdminTools.getInstance().getNotificationOverlay().push(analysis.player(), analysis.reason(), raw);
            lastViolator = analysis.player();
            lastReason = analysis.reason();
        }

        MutableText out = Text.literal("");
        String[] parts = raw.split("(?<=\\s)|(?=\\s)");
        for (String part : parts) {
            if (isForbiddenPart(part)) out.append(Text.literal(part).formatted(Formatting.RED, Formatting.BOLD));
            else out.append(Text.literal(part));
        }
        return out;
    }

    public Analysis analyze(String raw) {
        String lower = raw.toLowerCase(Locale.ROOT);
        String player = extractPlayer(raw);

        if (RaidMineAdminTools.getInstance().getConfigManager().get().isAdvertisingDetection() && LINK.matcher(lower).find()) {
            return new Analysis(true, player, "2.6/2.8 реклама или ссылка");
        }
        if (RaidMineAdminTools.getInstance().getConfigManager().get().isSwearDetection()) {
            long swearCount = BAD_WORDS.stream().filter(w -> lower.contains(w)).count();
            if (swearCount > 0) return new Analysis(true, player, swearCount >= 3 ? "2.1 чрезмерный мат" : "2.2 запрещенная лексика");
        }
        if (RaidMineAdminTools.getInstance().getConfigManager().get().isCapsDetection() && isCaps(raw)) {
            return new Analysis(true, player, "2.1 капс");
        }
        if (RaidMineAdminTools.getInstance().getConfigManager().get().isSpamDetection() && isRepeatSpam(player, raw)) {
            return new Analysis(true, player, "2.1 повтор сообщений");
        }
        return new Analysis(false, player, "");
    }

    private void recordMessage(String raw) {
        String player = extractPlayer(raw);
        RaidMineAdminTools.getInstance().getDatabaseManager().addRecentMessage(player, raw);
    }

    private boolean isForbiddenPart(String part) {
        String lower = part.toLowerCase(Locale.ROOT);
        return LINK.matcher(lower).find() || BAD_WORDS.stream().anyMatch(lower::contains);
    }

    private boolean isCaps(String raw) {
        String[] words = raw.split("\\s+");
        if (words.length < 3) return false;
        int letters = 0;
        int upper = 0;
        for (char c : raw.toCharArray()) {
            if (Character.isLetter(c)) {
                letters++;
                if (Character.isUpperCase(c)) upper++;
            }
        }
        return letters >= 8 && upper * 100 / letters >= RaidMineAdminTools.getInstance().getConfigManager().get().getCapsPercentageThreshold();
    }

    private boolean isRepeatSpam(String player, String raw) {
        DatabaseManager db = RaidMineAdminTools.getInstance().getDatabaseManager();
        long now = System.currentTimeMillis();
        String normalized = raw.replaceFirst("^.*?[>:]\\s*", "").trim().toLowerCase(Locale.ROOT);
        int count = 0;
        for (DatabaseManager.TimedMessage message : db.getTimedMessages(player)) {
            if (now - message.timeMillis() <= RaidMineAdminTools.getInstance().getConfigManager().get().getSpamTimeWindow() * 1000L) {
                String other = message.message().replaceFirst("^.*?[>:]\\s*", "").trim().toLowerCase(Locale.ROOT);
                if (other.equals(normalized)) count++;
            }
        }
        return count >= RaidMineAdminTools.getInstance().getConfigManager().get().getSpamMessageLimit();
    }

    private String extractPlayer(String raw) {
        return PlayerNameExtractor.scan(raw).orElse("unknown");
    }

    public String getLastViolator() { return lastViolator; }
    public String getLastReason() { return lastReason; }

    public record Analysis(boolean violation, String player, String reason) {}
}
