package dev.strike.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

	/** Перезарядка кисти после каждого запроса, секунды. */
	public static int cooldownSeconds = 3;

	/** Сколько секунд блок обводится рамкой перед броском шанса. */
	public static int requestDelaySeconds = 2;

	/** Уровень кисти, с которого доступен выбор радиуса (4 = после 3 улучшений). */
	public static int radiusUnlockLevel = 4;

	/** Максимальный радиус удаления: 1 — только целевой блок, 4 — область 7×7×7. */
	public static int maxRadius = 4;

	/** Множитель опыта при улучшении (1 — по таблице уровней). */
	public static int upgradeXpPerLevel = 1;

	/** Учитывать редкость блока при броске шанса. */
	public static boolean rarityEnabled = true;

	/** Шанс для обычных блоков, множитель. */
	public static double commonMultiplier = 1.0;

	/** Шанс для необычных блоков, множитель. */
	public static double uncommonMultiplier = 0.85;

	/** Шанс для редких блоков, множитель. */
	public static double rareMultiplier = 0.7;

	/** Шанс для очень редких блоков, множитель. */
	public static double veryRareMultiplier = 0.55;

	/** Шанс для легендарных блоков, множитель. */
	public static double legendaryMultiplier = 0.4;

	/** Необычные блоки — шанс удаления ниже. */
	public static List<String> uncommonBlocks = new ArrayList<>(Arrays.asList(
			"minecraft:iron_block",
			"minecraft:copper_block",
			"minecraft:coal_block",
			"minecraft:lapis_block",
			"minecraft:quartz_block",
			"minecraft:amethyst_block",
			"minecraft:redstone_block",
			"minecraft:honey_block",
			"minecraft:sponge",
			"minecraft:prismarine",
			"minecraft:prismarine_bricks",
			"minecraft:hay_block",
			"minecraft:melon",
			"minecraft:bone_block",
			"minecraft:moss_block",
			"minecraft:tuff",
			"minecraft:calcite",
			"minecraft:dripstone_block",
			"minecraft:packed_ice",
			"minecraft:snow_block",
			"minecraft:glowstone",
			"minecraft:magma_block",
			"minecraft:nether_bricks",
			"minecraft:sandstone",
			"minecraft:cut_sandstone"
	));

	/** Редкие блоки — шанс удаления заметно ниже. */
	public static List<String> rareBlocks = new ArrayList<>(Arrays.asList(
			"minecraft:obsidian",
			"minecraft:crying_obsidian",
			"minecraft:gold_block",
			"minecraft:emerald_block",
			"minecraft:diamond_block",
			"minecraft:sea_lantern",
			"minecraft:shulker_box",
			"minecraft:end_stone",
			"minecraft:end_stone_bricks",
			"minecraft:purpur_block",
			"minecraft:purpur_pillar",
			"minecraft:lodestone",
			"minecraft:respawn_anchor",
			"minecraft:bell",
			"minecraft:target",
			"minecraft:sculk_sensor"
	));

	/** Очень редкие блоки — шанс удаления низкий. */
	public static List<String> veryRareBlocks = new ArrayList<>(Arrays.asList(
			"minecraft:netherite_block",
			"minecraft:ancient_debris",
			"minecraft:enchanting_table",
			"minecraft:brewing_stand",
			"minecraft:cauldron",
			"minecraft:blast_furnace",
			"minecraft:smoker",
			"minecraft:furnace",
			"minecraft:smithing_table",
			"minecraft:cartography_table",
			"minecraft:grindstone",
			"minecraft:stonecutter",
			"minecraft:lectern",
			"minecraft:composter",
			"minecraft:fletching_table",
			"minecraft:chorus_plant",
			"minecraft:chorus_flower"
	));

	/** Легендарные блоки — шанс удаления минимальный. */
	public static List<String> legendaryBlocks = new ArrayList<>(Arrays.asList(
			"minecraft:dragon_egg",
			"minecraft:budding_amethyst",
			"minecraft:reinforced_deepslate",
			"minecraft:end_gateway",
			"minecraft:beacon",
			"minecraft:conduit",
			"minecraft:spawner",
			"minecraft:sculk_catalyst",
			"minecraft:sculk_shrieker"
	));

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

	/** Требования к улучшению: целевой уровень → материал (блок и количество). */
	public static Map<Integer, Material> upgradeMaterials = defaultUpgradeMaterials();

	private StrikeConfig() {
	}

	/** Материал улучшения: id предмета и нужное количество. */
	public static final class Material {

		/** id предмета, например {@code minecraft:dirt}. */
		public final String item;

		/** Сколько таких предметов нужно. */
		public final int count;

		public Material(String item, int count) {
			this.item = item;
			this.count = count;
		}
	}

	/** Таблица прокачки по умолчанию: каждый уровень требует свой блок. */
	private static Map<Integer, Material> defaultUpgradeMaterials() {
		Map<Integer, Material> map = new LinkedHashMap<>();
		map.put(2, new Material("minecraft:dirt", 4));
		map.put(3, new Material("minecraft:cobblestone", 4));
		map.put(4, new Material("minecraft:sand", 4));
		map.put(5, new Material("minecraft:oak_log", 4));
		map.put(6, new Material("minecraft:iron_ingot", 4));
		map.put(7, new Material("minecraft:gold_ingot", 4));
		map.put(8, new Material("minecraft:diamond", 4));
		map.put(9, new Material("minecraft:obsidian", 4));
		map.put(10, new Material("minecraft:netherite_ingot", 1));
		return map;
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
		cooldownSeconds = integer(props, "cooldownSeconds", cooldownSeconds);
		requestDelaySeconds = integer(props, "requestDelaySeconds", requestDelaySeconds);
		radiusUnlockLevel = integer(props, "radiusUnlockLevel", radiusUnlockLevel);
		maxRadius = integer(props, "maxRadius", maxRadius);
		upgradeXpPerLevel = integer(props, "upgradeXpPerLevel", upgradeXpPerLevel);
		rarityEnabled = bool(props, "rarityEnabled", rarityEnabled);
		commonMultiplier = decimal(props, "commonMultiplier", commonMultiplier);
		uncommonMultiplier = decimal(props, "uncommonMultiplier", uncommonMultiplier);
		rareMultiplier = decimal(props, "rareMultiplier", rareMultiplier);
		veryRareMultiplier = decimal(props, "veryRareMultiplier", veryRareMultiplier);
		legendaryMultiplier = decimal(props, "legendaryMultiplier", legendaryMultiplier);

		uncommonBlocks = list(props, "uncommonBlocks", uncommonBlocks);
		rareBlocks = list(props, "rareBlocks", rareBlocks);
		veryRareBlocks = list(props, "veryRareBlocks", veryRareBlocks);
		legendaryBlocks = list(props, "legendaryBlocks", legendaryBlocks);
		protectedBlocks = list(props, "protectedBlocks", protectedBlocks);

		Map<Integer, Material> materials = new LinkedHashMap<>(defaultUpgradeMaterials());
		for (int level = 2; level <= maxLevel; level++) {
			String value = props.getProperty("upgrade" + level);
			if (value == null || value.isBlank()) {
				continue;
			}
			Material parsed = parseMaterial(value.trim());
			if (parsed != null) {
				materials.put(level, parsed);
			}
		}
		upgradeMaterials = materials;

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
		props.setProperty("cooldownSeconds", String.valueOf(cooldownSeconds));
		props.setProperty("requestDelaySeconds", String.valueOf(requestDelaySeconds));
		props.setProperty("radiusUnlockLevel", String.valueOf(radiusUnlockLevel));
		props.setProperty("maxRadius", String.valueOf(maxRadius));
		props.setProperty("upgradeXpPerLevel", String.valueOf(upgradeXpPerLevel));
		props.setProperty("rarityEnabled", String.valueOf(rarityEnabled));
		props.setProperty("commonMultiplier", String.valueOf(commonMultiplier));
		props.setProperty("uncommonMultiplier", String.valueOf(uncommonMultiplier));
		props.setProperty("rareMultiplier", String.valueOf(rareMultiplier));
		props.setProperty("veryRareMultiplier", String.valueOf(veryRareMultiplier));
		props.setProperty("legendaryMultiplier", String.valueOf(legendaryMultiplier));
		props.setProperty("uncommonBlocks", String.join(",", uncommonBlocks));
		props.setProperty("rareBlocks", String.join(",", rareBlocks));
		props.setProperty("veryRareBlocks", String.join(",", veryRareBlocks));
		props.setProperty("legendaryBlocks", String.join(",", legendaryBlocks));
		props.setProperty("protectedBlocks", String.join(",", protectedBlocks));
		for (Map.Entry<Integer, Material> entry : upgradeMaterials.entrySet()) {
			props.setProperty("upgrade" + entry.getKey(),
					entry.getValue().item + "," + entry.getValue().count);
		}

		try {
			Files.createDirectories(file.getParent());
			try (OutputStream out = Files.newOutputStream(file)) {
				props.store(out, "Strike mod configuration");
			}
		} catch (IOException e) {
			System.err.println("[Strike] Не удалось создать " + file + ": " + e.getMessage());
		}
	}

	/** Разбирает запись вида {@code minecraft:dirt,4}. */
	private static Material parseMaterial(String value) {
		int comma = value.indexOf(',');
		if (comma <= 0) {
			return null;
		}
		String id = value.substring(0, comma).trim();
		try {
			int count = Integer.parseInt(value.substring(comma + 1).trim());
			if (count <= 0) {
				return null;
			}
			return new Material(id, count);
		} catch (NumberFormatException e) {
			return null;
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

	private static double decimal(Properties props, String key, double def) {
		String value = props.getProperty(key);
		if (value == null) {
			return def;
		}
		try {
			return Double.parseDouble(value.trim());
		} catch (NumberFormatException e) {
			return def;
		}
	}

	private static List<String> list(Properties props, String key, List<String> def) {
		String value = props.getProperty(key);
		if (value == null || value.isBlank()) {
			return def;
		}
		List<String> parsed = new ArrayList<>();
		for (String id : value.split(",")) {
			String trimmed = id.trim();
			if (!trimmed.isEmpty()) {
				parsed.add(trimmed);
			}
		}
		return parsed;
	}
}
