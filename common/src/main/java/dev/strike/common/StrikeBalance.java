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
}
