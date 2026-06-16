package com.raidmine.admintools.model;

import java.util.List;

public record PresetReason(String rule, String shortReason, String duration, PunishmentType type, String details) {
    public static List<PresetReason> defaults() {
        return List.of(
                new PresetReason("2.1", "Флуд / капс / спам", "", PunishmentType.WARN, "[Варн] 3 однотипных сообщения, флуд символами, организация флуда, капс, чрезмерный мат."),
                new PresetReason("2.2", "Оскорбление", "12h", PunishmentType.MUTE, "[Мут 1-12 часов] Оскорбления игроков, персонала, администрации или проекта."),
                new PresetReason("2.3", "Оскорбление родных", "5d", PunishmentType.MUTE, "[Мут 1-5 дней] Упоминание чужих родных как оскорбление."),
                new PresetReason("2.4", "Сексуальный характер", "2h", PunishmentType.MUTE, "[Мут 2 часа] Сообщения сексуального характера."),
                new PresetReason("2.5", "Неадекватное поведение", "2h", PunishmentType.MUTE, "[Мут 2 часа] Грубые фразы, призыв к суициду, пропаганда запрещенных веществ."),
                new PresetReason("2.6", "Реклама серверов / читов", "14d", PunishmentType.BAN, "[Мут 1 день / Бан 1-14 дней] Реклама серверов, читов, IP или ссылок."),
                new PresetReason("2.7", "Разжигание ненависти", "9h", PunishmentType.MUTE, "[Мут 9 часов] Нацизм, ненависть, религия/политика в провокационном контексте."),
                new PresetReason("2.8", "Сторонние ссылки", "3d", PunishmentType.MUTE, "[Мут 8 часов - 3 дня] Ссылки, реклама стримов и видео."),
                new PresetReason("2.9", "Выдача за администратора", "12h", PunishmentType.MUTE, "[Мут 12 часов] Выдача себя за игрового администратора."),
                new PresetReason("2.10", "Угрозы вне игры", "12h", PunishmentType.MUTE, "[Мут 12 часов] Докс, сват, распространение личных данных."),
                new PresetReason("2.11", "Угроза наказанием", "6h", PunishmentType.MUTE, "[Мут 6 часов] Угрозы наказанием без реальной причины."),
                new PresetReason("2.12", "Политика", "7d", PunishmentType.MUTE, "[Мут 12 часов - 7 дней] Обсуждение политики."),
                new PresetReason("2.13", "Введение в заблуждение", "2h", PunishmentType.MUTE, "[Мут 2 часа] Ложные советы вроде alt+f4."),
                new PresetReason("2.14", "Попрошайничество у администрации", "6h", PunishmentType.MUTE, "[Мут 6 часов] Просьбы ресурсов/привилегий у администрации."),
                new PresetReason("3.1", "Запрещенный никнейм", "", PunishmentType.BAN, "[Бан навсегда] Ник нарушает правила или похож на ник администрации."),
                new PresetReason("3.7", "Запрещенное ПО", "30d", PunishmentType.BAN, "[Бан 30 дней] Читы, X-Ray, Baritone, макросы и запрещенные моды.")
        );
    }
}
