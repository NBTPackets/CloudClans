<p align="center">CloudClans<br><br><img src="https://img.shields.io/badge/Minecraft-1.20–1.21+-orange" alt="Supported versions"/><img src="https://img.shields.io/badge/Platform-Paper%20%7C%20Spigot-blue" alt="Platform"/><img src="https://img.shields.io/badge/Java-17%2B-red" alt="Java"/><a href="https://t.me/Vladimir_Vladimirovich_Putln"><img src="https://img.shields.io/badge/Telegram-NBTPackets-2CA5E0?logo=telegram&logoColor=white"/></a></p>

>### Что может этот плагин:
>
>- Поддержка своих аддонов
>- Система кланов с приглашениями и управлением участниками
>- Система уровней и опыта для кланов
>- Банковская система (баланс клана, инвестиции, вывод средств)
>- Подсветка участников клана (glow) через PacketEvents
>- Настраиваемые меню через конфиги
>- Система прав внутри клана
>- Поддержка PlaceholderAPI
>- Интеграция с Vault Economy
>- Поддержка MySQL и SQLite
>- Кастомные действия, команды, требования и плейсхолдеры через API
>- Система событий для аддонов
>- Многоязычность (ru, en, ua)

>**CloudClans** — это мощный плагин для создания и управления кланами на вашем сервере с гибкой системой аддонов

# Подключение

### Maven
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

```xml
<dependency>
    <groupId>com.github.NBTPackets</groupId>
    <artifactId>CloudClans</artifactId>
    <version>1.0.0</version> <!-- укажите актуальную версию -->
    <scope>provided</scope>
</dependency>
```

### Gradle
```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

```groovy
dependencies {
    implementation 'com.github.NBTPackets:CloudClans:1.0.0' // укажите актуальную версию
}
```

### plugin.yml
```yaml
depend: [CloudClans]
# или
softdepend: [CloudClans]
```

# Как использовать API?

### Получение провайдера кланов:
```java
ClanProvider provider = AddonAPI.getClanProvider();

// получить клан по имени
provider.getClan("MyClan").ifPresent(clan -> {
    // работа с кланом
});

// получить клан игрока
provider.getPlayerClan(player.getUniqueId()).ifPresent(clan -> {
    // работа с кланом игрока
});

// проверка что игроки в одном клане
if (provider.isSameClan(uuid1, uuid2)) {
    // они в одном клане
}
```

### Регистрация кастомных действий:
```java
@Override
public void onEnable() {
    // регистрация действия для использования в меню
    AddonAPI.registerAction("[boost]", (player, arg, ctx) -> {
        player.setWalkSpeed(0.4f);
        player.sendMessage("Скорость увеличена!");
    });
}
```

Использование в конфиге меню:
```yaml
left_click_actions:
  - "[boost] 5"
```

### Регистрация подкоманд:
```java
AddonAPI.registerSubCommand(new AddonSubCommand() {
    @Override
    public String getName() {
        return "home";
    }

    @Override
    public void execute(Player player, String[] args) {
        // телепорт в дом клана
    }

    @Override
    public boolean requiresClan() {
        return true; // команда доступна только участникам клана
    }
});
```

Теперь доступна команда `/clan home`

### Регистрация прав клана:
```java
// регистрация нового права
AddonAPI.registerPerm(
    "HOME_SET",           // ключ права
    "home_set",           // ключ для конфигов
    "<green>Установка дома",  // отображаемое имя
    "Право на установку дома клана"  // описание
);
```

Использование в player-perm.yml:
```yaml
- slot: 20
  material: "{perm_home_set_material}"
  name: "<white>Установка дома"
  lore:
    - "Статус: {perm_home_set_status}"
  left_click_actions:
    - "[toggle-perm] HOME_SET"
```

### Регистрация плейсхолдеров:
```java
// плейсхолдер для использования в меню
AddonAPI.registerPlaceholder("clan_home", (clan, player) -> {
    Location home = getHome(clan);
    if (home == null) return "Не установлен";
    return home.getBlockX() + ", " + home.getBlockY() + ", " + home.getBlockZ();
});
```

Использование в конфиге:
```yaml
lore:
  - "Дом клана: {clan_home}"
```

### Регистрация кастомных меню:
```java
AddonAPI.registerMenu("clan_shop", clan -> {
    return new Menu(54, "Магазин клана") {
        @Override
        protected void build() {
            // создание меню
        }
    };
});
```

Открытие через действие:
```yaml
left_click_actions:
  - "[open_menu] clan_shop"
```

### Регистрация требований:
```java
// кастомное требование для меню
AddonAPI.registerRequirement("MIN_LEVEL", (player, clan, config) -> {
    int minLevel = config.getInt("value", 1);
    return clan.getLevel() >= minLevel;
});
```

Использование в конфиге:
```yaml
requirements:
  - type: MIN_LEVEL
    value: 5
    deny_actions:
      - "[message] Клан должен быть 5 уровня!"
```

### Подписка на события:
```java
@Override
public void onEnable() {
    // событие создания клана
    AddonAPI.onClanCreate(event -> {
        Clan clan = event.getClan();
        Player creator = event.getCreator();
        // действия при создании клана
    });

    // событие повышения уровня
    AddonAPI.onLevelUp(event -> {
        Clan clan = event.getClan();
        int newLevel = event.getNewLevel();
        // действия при повышении уровня
    });

    // событие вступления в клан
    AddonAPI.onMemberJoin(event -> {
        Clan clan = event.getClan();
        UUID member = event.getMember();
        // действия при вступлении
    });

    // событие изменения баланса
    AddonAPI.onBalanceChange(event -> {
        Clan clan = event.getClan();
        double oldBalance = event.getOldBalance();
        double newBalance = event.getNewBalance();
        // действия при изменении баланса
    });
}
```

### Пример комплексного использования:
```java
public class ClanHomesAddon extends AbstractAddon {

    @Override
    protected void onEnable() {
        // регистрация права
        AddonAPI.registerPerm("HOME_SET", "home_set", "<green>Установка дома");
        AddonAPI.registerPerm("HOME_TP", "home_tp", "<blue>Телепорт домой");

        // регистрация команд
        AddonAPI.registerSubCommand(new SetHomeCommand());
        AddonAPI.registerSubCommand(new HomeCommand());

        // регистрация действия
        AddonAPI.registerAction("[home]", (player, arg, ctx) -> {
            AddonAPI.getClanProvider()
                .getPlayerClan(player.getUniqueId())
                .ifPresent(clan -> teleportHome(player, clan));
        });

        // регистрация плейсхолдера
        AddonAPI.registerPlaceholder("home_location", (clan, player) -> {
            return getHomeString(clan);
        });

        // подписка на события
        AddonAPI.onClanCreate(event -> {
            // установка дефолтного дома
            setDefaultHome(event.getClan());
        });
    }

    @Override
    protected void onDisable() {
        // очистка не требуется, всё удаляется автоматически
    }
}
```

# Команды плагина

- `/clan create <название>` — создать клан
- `/clan delete` — удалить клан (только лидер)
- `/clan invite <игрок>` — пригласить игрока
- `/clan accept <клан>` — принять приглашение
- `/clan kick <игрок>` — исключить участника
- `/clan leave` — покинуть клан
- `/clan setleader <игрок>` — передать лидерство
- `/clan info [клан]` — информация о клане
- `/clan menu` — открыть меню клана
- `/clan chat` — переключить чат клана
- `/clan pvp` — переключить пвп внутри клана
- `/clan glow` — переключить подсветку участников
- `/clan balance` — баланс клана
- `/clan invest <сумма>` — внести деньги в клан
- `/clan withdraw <сумма>` — вывести деньги (только лидер)

# Создание аддона

### Структура аддона:
```
MyAddon/
├── addon.yml
└── MyAddon.jar
```

### addon.yml:
```yaml
name: MyAddon
version: 1.0.0
main: com.example.MyAddon
```

### Главный класс:
```java
package com.example;

import github.com.nbtpackets.addon.AbstractAddon;
import github.com.nbtpackets.api.AddonAPI;

public class MyAddon extends AbstractAddon {

    @Override
    protected void onEnable() {
        getLogger().info("MyAddon включен!");
        
        // регистрация функционала
        AddonAPI.registerSubCommand(new MyCommand());
        AddonAPI.registerAction("[myaction]", this::handleAction);
    }

    @Override
    protected void onDisable() {
        getLogger().info("MyAddon выключен!");
    }

    private void handleAction(Player player, String arg, Map<String, Object> ctx) {
        // обработка действия
    }
}
```

Поместите jar аддона в папку `plugins/CloudClans/addons/`

# Зависимости

- CloudLibsAPI (обязательно)
- Vault (обязательно)
- PlaceholderAPI (обязательно)
- PacketEvents (обязательно для подсветки)
