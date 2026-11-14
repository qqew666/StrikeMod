package dev.strike.common;

/**
 * Баланс кисти: шанс удаления блока, лимит блоков и цена улучшения
 * в зависимости от уровня кисти.
 */
public final class StrikeBalance {

	private StrikeBalance() {
	}

	/** Приводит уровень к допустимому диапазону 1..maxLevel. */
	public static int clampLevel(int level) {
		return Math.max(1, Math.min(level, StrikeConfig.maxLevel));
	}

	/** Шанс удаления блока на указанном уровне, %. */
	public static int chancePercent(int level) {
		int lvl = clampLevel(level);
		int chance = StrikeConfig.baseChance + (lvl - 1) * StrikeConfig.chancePerLevel;
		return Math.min(chance, StrikeConfig.maxChance);
	}

	/** Сколько блоков кисть может удалить на указанном уровне. */
	public static int capacity(int level) {
		int lvl = clampLevel(level);
		return StrikeConfig.baseCapacity * lvl;
	}

	/** Цена улучшения с указанного уровня (в изумрудах). */
	public static int upgradeEmeraldCost(int level) {
		int lvl = clampLevel(level);
		return StrikeConfig.upgradeEmeraldBase + (lvl - 1) * StrikeConfig.upgradeEmeraldPerLevel;
	}

	/** Цена улучшения с указанного уровня (в уровнях опыта). */
	public static int upgradeXpCost(int level) {
		return StrikeConfig.upgradeXpPerLevel;
	}

	/** Можно ли улучшить кисть с этого уровня. */
	public static boolean canUpgrade(int level) {
		return level < StrikeConfig.maxLevel;
	}

	/** Приводит радиус к допустимому диапазону 1..maxRadius. */
	public static int clampRadius(int radius) {
		return Math.max(1, Math.min(radius, Math.max(1, StrikeConfig.maxRadius)));
	}

	/** Размер стороны области удаления для радиуса: 1 → 1×1×1, 2 → 3×3×3, 3 → 5×5×5, 4 → 7×7×7. */
	public static int radiusSide(int radius) {
		return clampRadius(radius) * 2 - 1;
	}

	/** Доступен ли выбор радиуса на этом уровне кисти. */
	public static boolean radiusAvailable(int level) {
		return clampLevel(level) >= StrikeConfig.radiusUnlockLevel;
	}

	/** Перезарядка кисти в миллисекундах. */
	public static long cooldownMillis() {
		return Math.max(0, StrikeConfig.cooldownSeconds) * 1000L;
	}

	/** Задержка перед броском шанса (время обводки блока) в миллисекундах. */
	public static long requestDelayMillis() {
		return Math.max(0, StrikeConfig.requestDelaySeconds) * 1000L;
	}
}
