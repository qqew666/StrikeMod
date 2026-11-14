package dev.strike.common;

/**
 * Баланс кисти: шанс удаления блока, лимит блоков и цена улучшения
 * в зависимости от уровня кисти.
 */
public final class StrikeBalance {

	/** Сколько уровней опыта нужно за каждый целевой уровень кисти. */
	private static final int[] XP_COST = { 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5 };

	private StrikeBalance() {
	}

	/** Приводит уровень к допустимому диапазону 1..maxLevel. */
	public static int clampLevel(int level) {
		return Math.max(1, Math.min(level, StrikeConfig.maxLevel));
	}

	/** Шанс удаления блока на указанном уровне, % (без учёта редкости). */
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

	/** Материал (блок и количество), необходимый для улучшения до целевого уровня. */
	public static StrikeConfig.Material upgradeMaterial(int targetLevel) {
		return StrikeConfig.upgradeMaterials.get(targetLevel);
	}

	/** Сколько уровней опыта нужно за улучшение до целевого уровня. */
	public static int upgradeXpCost(int targetLevel) {
		int base = targetLevel >= 0 && targetLevel < XP_COST.length ? XP_COST[targetLevel] : 5;
		return base * Math.max(0, StrikeConfig.upgradeXpPerLevel);
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
