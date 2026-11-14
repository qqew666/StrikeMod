package dev.strike.common;

/**
 * Редкость блока влияет на шанс его удаления: чем реже блок, тем ниже шанс.
 * <p>
 * Списки блоков и множители настраиваются в {@code config/strike.properties}.
 */
public final class StrikeRarity {

	/** Категория редкости блока. */
	public enum Tier {

		/** Обычный блок (земля, камень, дерево) — шанс как у уровня кисти. */
		COMMON("обычный", 1.0),
		/** Необычный блок (железный блок, руды в блоке) — шанс ниже. */
		UNCOMMON("необычный", 0.85),
		/** Редкий блок (обсидиан, золотой/алмазный блок) — шанс ещё ниже. */
		RARE("редкий", 0.7),
		/** Очень редкий блок (незеритовый блок, древний обломок) — шанс низкий. */
		VERY_RARE("очень редкий", 0.55),
		/** Легендарный блок (драконье яйцо, маяк) — шанс минимальный. */
		LEGENDARY("легендарный", 0.4);

		private final String title;
		private final double defaultMultiplier;

		Tier(String title, double defaultMultiplier) {
			this.title = title;
			this.defaultMultiplier = defaultMultiplier;
		}

		/** Название категории для чата и книги. */
		public String title() {
			return title;
		}
	}

	private StrikeRarity() {
	}

	/** Категория редкости для блока по его id. */
	public static Tier tierOf(String blockId) {
		if (blockId == null) {
			return Tier.COMMON;
		}
		if (StrikeConfig.legendaryBlocks.contains(blockId)) {
			return Tier.LEGENDARY;
		}
		if (StrikeConfig.veryRareBlocks.contains(blockId)) {
			return Tier.VERY_RARE;
		}
		if (StrikeConfig.rareBlocks.contains(blockId)) {
			return Tier.RARE;
		}
		if (StrikeConfig.uncommonBlocks.contains(blockId)) {
			return Tier.UNCOMMON;
		}
		return Tier.COMMON;
	}

	/** Множитель шанса для категории редкости (из конфига). */
	public static double multiplierFor(Tier tier) {
		switch (tier) {
			case LEGENDARY:
				return StrikeConfig.legendaryMultiplier;
			case VERY_RARE:
				return StrikeConfig.veryRareMultiplier;
			case RARE:
				return StrikeConfig.rareMultiplier;
			case UNCOMMON:
				return StrikeConfig.uncommonMultiplier;
			case COMMON:
			default:
				return StrikeConfig.commonMultiplier;
		}
	}

	/** Множитель шанса для блока. */
	public static double multiplierFor(String blockId) {
		if (!StrikeConfig.rarityEnabled) {
			return 1.0;
		}
		return multiplierFor(tierOf(blockId));
	}

	/** Итоговый шанс удаления блока с учётом уровня кисти и редкости, %. */
	public static int chanceFor(int level, String blockId) {
		int base = StrikeBalance.chancePercent(level);
		double multiplier = multiplierFor(blockId);
		int chance = (int) Math.round(base * multiplier);
		return Math.max(1, Math.min(chance, StrikeConfig.maxChance));
	}
}
