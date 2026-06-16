# RaidMine Admin Tools

Client-side Fabric mod for Minecraft `1.21.11`.

## Что реализовано

- Авторизация персонала по нику и паролю ранга.
- Уникальные сгенерированные пароли для `helper`, `sthelper`, `moder`, `stmoder`, `hmoder`, `admin` при первом запуске.
- Локальная JSON-база `config/raidmine-staff.json` и удаленная синхронизация через URL из настроек.
- ЛКМ по нику в открытом чате открывает меню наказаний.
- ПКМ по игроку открывает меню наказаний.
- Подсветка запрещенных слов/ссылок красным и звуковое уведомление.
- Панель нарушения над хотбаром и открытие последнего нарушителя через `ALT+F6`.
- Меню наказаний с последними сообщениями, правилами RaidMine, копированием и отправкой команды.
- Меню настроек внутри мода.

## Формат удаленной базы

URL должен отдавать JSON такого вида:

```json
{
  "staff": [
    { "username": "NickName", "rank": "helper", "lastLogin": 0, "active": true }
  ],
  "rankPasswords": {
    "helper": "RM-example1",
    "sthelper": "RM-example2",
    "moder": "RM-example3",
    "stmoder": "RM-example4",
    "hmoder": "RM-example5",
    "admin": "RM-example6"
  }
}
```

По умолчанию создаются админы `owner` и `nekroz1990`. Пароли смотри после первого запуска в `config/raidmine-staff.json`.

## Сборка

В проект добавлен Gradle wrapper, но `gradle-wrapper.jar` нужно иметь настоящий, не заглушку. После этого:

```bash
./gradlew build
```

Готовый jar будет в `build/libs/`.
