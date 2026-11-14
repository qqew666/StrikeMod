package dev.strike.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Настройки мода. Чистая Java — общий код для всех версий Minecraft.
 * <p>
 * Значения по умолчанию можно переопределить файлом {@code config/strike.properties}.
 */
public final class StrikeConfig {

	/** Выключает мод в одиночной игре (true — только мультиплеер; false — везде). */
	public static boolean multiplayerOnly = false;

	/** Требовать подтверждения запроса командой {@code /strike confirm}. */
	public static boolean requireConfirmation = false;

	/** Шанс удаления на 1 уровне, %. */
	public static int baseChance = 50;

	/** Прирост шанса за каждый уровень, %. */
	public static int chancePerLevel = 5;

	/** Максимальный шанс, %. */
	public static int maxChance = 95;

	/** Сколько блоков можно удалить на 1 уровне (умножается на уровень). */
	public static int baseCapacity = 10;

	/** Максимальный уровень кисти. */
	public static int maxLevel = 10;

	/** Базовая цена улучшения в изумрудах. */
	public static int upgradeEmeraldBase = 4;

	/** Прирост цены улучшения (в изумрудах) за каждый уровень. */
	public static int upgradeEmeraldPerLevel = 4;

	/** Сколько уровней опыта тратится на улучшение. */
	public static int upgradeXpPerLevel = 1;

	/** Блоки, которые нельзя удалить кистью. */
	public static List<String> protectedBlocks = new ArrayList<>(Arrays.asList(
			"minecraft:bedrock",
			"minecraft:barrier",
			"minecraft:command_block",
			"minecraft:chain_command_block",
			"minecraft:repeating_command_block",
			"minecraft:structure_block",
			"minecraft:structure_void",
			"minecraft:jigsaw",
			"minecraft:light",
			"minecraft:end_portal",
			"minecraft:end_portal_frame",
			"minecraft:end_gateway",
			"minecraft:nether_portal",
			"minecraft:spawner",
			"minecraft:reinforced_deepslate",
			"minecraft:moving_piston"
	));

	private StrikeConfig() {
	}

	/** Загружает конфиг из каталога конфигов, создаёт его со значениями по умолчанию, если нет. */
	public static void load(Path configDir) {
		Path file = configDir.resolve("strike.properties");
		Properties props = new Properties();

		if (Files.exists(file)) {
			try (InputStream in = Files.newInputStream(file)) {
				props.load(in);
			} catch (IOException e) {
				System.err.println("[Strike] Не удалось прочитать " + file + ": " + e.getMessage());
			}
		}

		multiplayerOnly = bool(props, "multiplayerOnly", multiplayerOnly);
		requireConfirmation = bool(props, "requireConfirmation", requireConfirmation);
		baseChance = integer(props, "baseChance", baseChance);
		chancePerLevel = integer(props, "chancePerLevel", chancePerLevel);
		maxChance = integer(props, "maxChance", maxChance);
		baseCapacity = integer(props, "baseCapacity", baseCapacity);
		maxLevel = integer(props, "maxLevel", maxLevel);
		upgradeEmeraldBase = integer(props, "upgradeEmeraldBase", upgradeEmeraldBase);
		upgradeEmeraldPerLevel = integer(props, "upgradeEmeraldPerLevel", upgradeEmeraldPerLevel);
		upgradeXpPerLevel = integer(props, "upgradeXpPerLevel", upgradeXpPerLevel);

		String protectedList = props.getProperty("protectedBlocks");
		if (protectedList != null && !protectedList.isBlank()) {
			List<String> parsed = new ArrayList<>();
			for (String id : protectedList.split(",")) {
				String trimmed = id.trim();
				if (!trimmed.isEmpty()) {
					parsed.add(trimmed);
				}
			}
			protectedBlocks = parsed;
		}

		if (!Files.exists(file)) {
			writeDefaults(file, props);
		}
	}

	private static void writeDefaults(Path file, Properties props) {
		props.setProperty("multiplayerOnly", String.valueOf(multiplayerOnly));
		props.setProperty("requireConfirmation", String.valueOf(requireConfirmation));
		props.setProperty("baseChance", String.valueOf(baseChance));
		props.setProperty("chancePerLevel", String.valueOf(chancePerLevel));
		props.setProperty("maxChance", String.valueOf(maxChance));
		props.setProperty("baseCapacity", String.valueOf(baseCapacity));
		props.setProperty("maxLevel", String.valueOf(maxLevel));
		props.setProperty("upgradeEmeraldBase", String.valueOf(upgradeEmeraldBase));
		props.setProperty("upgradeEmeraldPerLevel", String.valueOf(upgradeEmeraldPerLevel));
		props.setProperty("upgradeXpPerLevel", String.valueOf(upgradeXpPerLevel));
		props.setProperty("protectedBlocks", String.join(",", protectedBlocks));

		try {
			Files.createDirectories(file.getParent());
			try (OutputStream out = Files.newOutputStream(file)) {
				props.store(out, "Strike mod configuration");
			}
		} catch (IOException e) {
			System.err.println("[Strike] Не удалось создать " + file + ": " + e.getMessage());
		}
	}

	private static boolean bool(Properties props, String key, boolean def) {
		String value = props.getProperty(key);
		return value == null ? def : Boolean.parseBoolean(value.trim());
	}

	private static int integer(Properties props, String key, int def) {
		String value = props.getProperty(key);
		if (value == null) {
			return def;
		}
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			return def;
		}
	}
}
