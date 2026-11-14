# Strike

**Strike** — мод для Minecraft: волшебная **кисть**, которая удаляет блоки не напрямую, а **по запросу**.
Нажал левую кнопку мыши по блоку — появился запрос на удаление, дальше бросается кость: сработал шанс — блок исчез.

Работает **только на мультиплеере** (на выделенном сервере). В одиночной игре мод выключается.

---

## Поддерживаемые версии

| Minecraft | Java | Fabric API | Модуль в проекте |
|------------|------|-----------|------------------|
| 1.20.1     | 17   | 0.92.12+  | `fabric-1.20.1`  |
| 1.21.1     | 21   | 0.116.18+ | `fabric-1.21.1`  |
| 1.21.11    | 21   | 0.141.6+  | `fabric-1.21.11` |
| 26.3       | 25   | 0.166.2+  | `fabric-26.3`    |

Все сборки — на **Fabric** и **Mojang mappings**.

---

## Установка

Готовые сборки — в разделе [Releases](../../releases):

| Файл | Версия Minecraft | Java | Нужен Fabric API |
|------|------------------|------|------------------|
| `strike-fabric-1.20.1-1.0.0.jar`  | 1.20.1  | 17 | 0.92.12+  |
| `strike-fabric-1.21.1-1.0.0.jar`  | 1.21.1  | 21 | 0.116.18+ |
| `strike-fabric-1.21.11-1.0.0.jar` | 1.21.11 | 21 | 0.141.6+  |
| `strike-fabric-26.3-1.0.0.jar`    | 26.3   | 25 | 0.166.2+  |

1. Установите на сервер [Fabric Loader](https://fabricmc.net/use/installer/) под нужную версию Minecraft.
2. Положите в папку `mods`:
   * **Fabric API** (версии из таблицы выше),
   * **Strike** — соответствующий `.jar`.
3. Перезапустите сервер.

Клиентам мод ставить **не нужно** — вся логика выполняется на сервере, а кисть выглядит как обычная ванильная кисть.

> **Кисть нельзя получить из креатива.** Это обычный предмет `minecraft:brush` с особой серверной меткой;
> в креативе меток нет, поэтому «настоящая» кисть выдаётся только командой `/strike`.

---

## Как пользоваться

1. Введите `/strike` — сервер выдаст кисть.
2. Возьмите её в руку, наведитесь на блок и нажмите **ЛКМ**.
3. Появится **запрос на удаление**: координаты блока, текущий шанс и остаток блоков.
4. Шанс срабатывает → блок исчезает (частицы и звук), счётчик удалённых блоков растёт.
   Шанс не сработал → ничего не происходит, блок остаётся на месте.

Кисть блокирует обычное разрушение блоков: пока она в руке, ЛКМ всегда означает «запрос на удаление».

В начале игры (при первом входе на сервер) рядом с игроком появляется **сундук с книгой-гайдом** — там описаны все команды и правила.
Если сундук потерялся, книгу можно взять командой `/strike guide`.

---

## Команды

| Команда | Описание |
|---------|----------|
| `/strike` | Выдать кисть (если её ещё нет) и показать её состояние |
| `/strike upgrade` | Улучшить кисть (тратит изумруды и опыт) |
| `/strike info` | Показать уровень, шанс и лимит кисти |
| `/strike guide` | Выдать книгу-гайд |
| `/strike help` | Список команд |
| `/strike confirm` | Подтвердить отложенный запрос (если включён `requireConfirmation`) |
| `/strike cancel` | Отменить отложенный запрос |
| `/strike give <игрок>` | Выдать кисть другому игроку (право оператора) |

---

## Уровни кисти

С каждым уровнем растёт **и шанс удаления, и количество блоков, которые можно удалить**.

| Уровень | Шанс | Лимит блоков | Цена улучшения |
|:-------:|-----:|:------------:|:--------------:|
| 1  | 50% | 10  | 4 изумруда + 1 опыт |
| 2  | 55% | 20  | 8 изумрудов + 1 опыт |
| 3  | 60% | 30  | 12 изумрудов + 1 опыт |
| 4  | 65% | 40  | 16 изумрудов + 1 опыт |
| 5  | 70% | 50  | 20 изумрудов + 1 опыт |
| 6  | 75% | 60  | 24 изумруда + 1 опыт |
| 7  | 80% | 70  | 28 изумрудов + 1 опыт |
| 8  | 85% | 80  | 32 изумруда + 1 опыт |
| 9  | 90% | 90  | 36 изумрудов + 1 опыт |
| 10 | 95% | 100 | — максимальный уровень |

Лимит блоков обнуляется при улучшении кисти.

---

## Настройки сервера

При первом запуске создаётся файл `config/strike.properties`:

```properties
multiplayerOnly=true          # мод работает только на мультиплеере
requireConfirmation=false     # требовать /strike confirm перед удалением
baseChance=50                 # шанс на 1 уровне, %
chancePerLevel=5              # прирост шанса за уровень, %
maxChance=95                  # максимальный шанс, %
baseCapacity=10               # блоков на 1 уровне
maxLevel=10                   # максимальный уровень кисти
upgradeEmeraldBase=4          # базовая цена улучшения в изумрудах
upgradeEmeraldPerLevel=4      # прирост цены за уровень
upgradeXpPerLevel=1           # сколько уровней опыта тратится на улучшение
protectedBlocks=minecraft:bedrock,minecraft:barrier,...   # блоки, которые нельзя удалить
```

---

## Сборка из исходников

Нужен JDK 25 (Gradle сам подстроит `--release` под версию Minecraft).

```bash
git clone https://github.com/qqew666/StrikeMod.git
cd StrikeMod

./gradlew :fabric-1.20.1:build      # или 1.21.1 / 1.21.11 / 26.3
./gradlew buildAll                  # собрать все версии сразу
```

Готовые `.jar` появятся в `fabric-<версия>/build/libs/`.

### Локальный запуск сервера

```bash
mkdir -p fabric-1.20.1/run
echo "eula=true" > fabric-1.20.1/run/eula.txt
./gradlew :fabric-1.20.1:runServer
```

В папке `fabric-1.20.1/run` появится мир, а настройки мода — в `fabric-1.20.1/run/config/strike.properties`.

### Публикация на площадки (CurseForge / Modrinth)

При теге `v*` (или ручном запуске workflow **Build** → *Run workflow*) CI дополнительно выгружает
все 4 джарника на **CurseForge** и **Modrinth**. Чтобы это заработало, добавь секреты в
**Settings → Secrets and variables → Actions** репозитория:

| Секрет | Что это |
|--------|---------|
| `MODRINTH_TOKEN` | PAT с <https://modrinth.com/settings/tokens> |
| `MODRINTH_PROJECT_ID` | slug или ID проекта на Modrinth |
| `CURSEFORGE_API_KEY` | ключ с <https://console.curseforge.com> → **API Keys** |
| `CURSEFORGE_PROJECT_ID` | числовой ID проекта на CurseForge |

Если секретов нет — job пропускается без ошибки, сборка и GitHub Release работают как обычно.

### Структура проекта

```
common/          общий код на чистой Java (баланс, тексты, конфиг)
fabric-1.20.1/   сборка под Minecraft 1.20.1
fabric-1.21.1/   сборка под Minecraft 1.21.1
fabric-1.21.11/  сборка под Minecraft 1.21.11
fabric-26.3/     сборка под Minecraft 26.3
```

Общая логика (шансы, лимиты, цены, тексты книги) лежит в `common/` и подключается к каждой версии,
поэтому поведение мода во всех сборках одинаковое. Различаются только реализации работы с
предметами: в 1.20.1 данные хранятся в NBT, в 1.21+ — в компонентах предметов.

---

## English

**Strike** is a Fabric mod that adds a magical brush which deletes blocks *by request*.
Left-click a block with the brush and a deletion request appears; a chance roll decides
whether the block is actually removed (50% on level 1, up to 95% on level 10).

* Multiplayer only (disabled in singleplayer).
* The brush is only obtainable with `/strike` — never from the creative inventory.
* Upgrading the brush raises both the deletion chance and the number of blocks it can delete.
* A chest with a guide book appears next to the player on their first join.
* Supported versions: **1.20.1, 1.21.1, 1.21.11, 26.3** (Fabric, Mojang mappings).
* Server-side only — clients do not need the mod installed.

---

## Лицензия

[MIT](LICENSE) © 2026 qqew666
