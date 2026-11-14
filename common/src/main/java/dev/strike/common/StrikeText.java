package dev.strike.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Все тексты мода: сообщения в чате и страницы книги-гайда.
 * <p>
 * Форматирование — классические коды {@code §}, их понимают все версии Minecraft.
 */
public final class StrikeText {

	private static final String BRUSH_NAME = "§a§lКисть Strike";
	private static final String BOOK_NAME = "§6§lStrike §7— гайд по кисти";

	private StrikeText() {
	}

	/* ============================ Сообщения в чат ============================ */

	public static String request(int x, int y, int z, int chance, int remaining, int radius, String rarity) {
		String extra = radius > 1
				? " §7• §dрадиус " + radius + " (" + StrikeBalance.radiusSide(radius) + "×"
						+ StrikeBalance.radiusSide(radius) + "×" + StrikeBalance.radiusSide(radius) + ")"
				: "";
		String rarityLine = rarity == null || rarity.isEmpty()
				? ""
				: " §7• §e" + rarity + " блок";
		return "§e§lЗапрос на удаление блока §f[" + x + ", " + y + ", " + z + "] "
				+ "§7• §aшанс " + chance + "% "
				+ "§7• §bосталось " + remaining + " блоков" + extra + rarityLine;
	}

	public static String success(int removed) {
		if (removed <= 1) {
			return "§a✔ §aБлок удалён! Шанс сработал.";
		}
		return "§a✔ §aУдалено блоков: §f" + removed + "§a. Шанс сработал.";
	}

	public static String failure() {
		return "§c✘ §cЗапрос отклонён — шанс не сработал.";
	}

	public static String quota(int capacity, String cost) {
		return "§c✘ Лимит блоков исчерпан (§f" + capacity + "§c). Введите §e/strike limit§c, "
				+ "чтобы восстановить (" + cost + "§c).";
	}

	/** Сообщение, что лимит ещё не исчерпан. */
	public static String quotaNotEmpty(int used, int capacity) {
		return "§eЛимит не исчерпан: §f" + used + "§7/§f" + capacity
				+ "§7. Восстанавливается командой §e/strike limit§7.";
	}

	/** Сообщение об успешном восстановлении лимита. */
	public static String limitRestored(int capacity) {
		return "§a✔ §aЛимит блоков восстановлен: §f0§7/§f" + capacity + "§a блоков.";
	}

	public static String noBrush() {
		return "§c✘ В руке нет кисти Strike. Возьмите кисть или введите §e/strike";
	}

	public static String protectedBlock() {
		return "§c✘ Этот блок защищён от удаления кистью.";
	}

	public static String multiplayerOnly() {
		return "§c✘ Мод Strike настроен работать только на мультиплеере (multiplayerOnly=true).";
	}

	public static String brushGiven() {
		return "§a✔ §aВы получили кисть Strike (уровень §f1§a). ЛКМ по блоку — запрос на удаление.";
	}

	public static String alreadyHaveBrush() {
		return "§eУ вас уже есть кисть Strike. Улучшить её: §e/strike upgrade";
	}

	public static String maxLevel(int level) {
		return "§eКисть уже максимального уровня (§f" + level + "§e).";
	}

	public static String needMaterial(String name, int need, int have) {
		return "§c✘ Не хватает: §f" + name + "§c — нужно §f" + need + "§c, есть §f" + have + "§c.";
	}

	public static String needXp(int need) {
		return "§c✘ Не хватает опыта: нужно §f" + need + "§c уровня опыта.";
	}

	public static String upgraded(int level, int chance, int capacity) {
		String radiusNote = StrikeBalance.radiusAvailable(level)
				? " §7Радиус доступен: §dПКМ§7."
				: "";
		return "§a✔ §aКисть улучшена до уровня §f" + level + "§a! Шанс §a" + chance + "%§a, лимит §f"
				+ capacity + "§a блоков." + radiusNote;
	}

	/** Строка с требованиями к следующему уровню (для чата и книги). */
	public static String upgradeCost(String materialName, int count, int xp) {
		return materialName + " §7×§f" + count + " §8+ §f" + xp + " §7опыта";
	}

	public static String guideGiven() {
		return "§a✔ Книга-гайд выдана.";
	}

	public static String confirmHint() {
		return "§7Подтвердите запрос: §e/strike confirm§7 (или §e/strike cancel§7).";
	}

	public static String noPendingRequest() {
		return "§cНет активного запроса на удаление блока.";
	}

	public static String pendingExists() {
		return "§eУ вас уже есть активный запрос — дождитесь результата.";
	}

	public static String requestConfirmed() {
		return "§eЗапрос подтверждён, бросаем кости…";
	}

	/** Сообщение о перезарядке кисти. */
	public static String cooldown(double secondsLeft) {
		return "§c✘ Кисть перезаряжается: §f" + (Math.round(secondsLeft * 10.0) / 10.0) + "§c с";
	}

	/** Сообщение о смене радиуса. */
	public static String radiusChanged(int radius, int side) {
		return "§d◆ §dРадиус кисти: §f" + radius + " §7(§f" + side + "×" + side + "×" + side + "§7)";
	}

	/** Сообщение о том, что радиус ещё заблокирован. */
	public static String radiusLocked(int unlockLevel) {
		return "§c✘ Радиус доступен с §f" + unlockLevel + "§c уровня кисти (сделайте "
				+ (unlockLevel - 1) + " улучшения).";
	}

	public static String info(int level, int chance, int used, int capacity, int radius, String limitCost) {
		int side = StrikeBalance.radiusSide(radius);
		String radiusLine = StrikeBalance.radiusAvailable(level)
				? "§7Радиус: §d" + radius + " §8(§d" + side + "×" + side + "×" + side + "§8) §7ПКМ — изменить\n"
				: "§7Радиус: §8откроется на " + StrikeConfig.radiusUnlockLevel + " уровне\n";
		return "§6§lStrike §7— кисть\n"
				+ "§7Уровень: §f" + level + " §8| §7Шанс (обычный блок): §a" + chance + "%\n"
				+ "§7Удалено блоков: §f" + used + "§7/§f" + capacity + "\n"
				+ radiusLine
				+ "§7Перезарядка: §f" + StrikeConfig.cooldownSeconds + " с\n"
				+ "§7Восстановить лимит: §e/strike limit §8(§7" + limitCost + "§8)\n"
				+ "§7Улучшение: §e/strike upgrade";
	}

	public static String help() {
		return "§6§lStrike §7— кисть с шансом удаления блоков\n"
				+ "§e/strike §7— выдать кисть и показать статус\n"
				+ "§e/strike upgrade §7— улучшить кисть\n"
				+ "§e/strike info §7— подробности о кисти\n"
				+ "§e/strike guide §7— выдать книгу-гайд\n"
				+ "§e/strike confirm §7— подтвердить запрос на удаление\n"
				+ "§e/strike cancel §7— отменить запрос\n"
				+ "§7ЛКМ по блоку — запрос, §7ПКМ§7 — радиус (с 4 уровня).";
	}

	public static String playerOnly() {
		return "§cЭту команду может выполнить только игрок.";
	}

	public static String givenTo(String player) {
		return "§a✔ Кисть Strike выдана игроку §f" + player + "§a.";
	}

	/* ============================ Страницы книги ============================ */

	public static String bookTitle() {
		return "Strike — кисть";
	}

	public static String bookAuthor() {
		return "Strike";
	}

	/** Понятные названия материалов прокачки для книги. */
	public static String materialName(String id) {
		switch (id) {
			case "minecraft:dirt":
				return "Земля";
			case "minecraft:cobblestone":
				return "Булыжник";
			case "minecraft:sand":
				return "Песок";
			case "minecraft:oak_log":
				return "Дубовое бревно";
			case "minecraft:iron_ingot":
				return "Железный слиток";
			case "minecraft:gold_ingot":
				return "Золотой слиток";
			case "minecraft:diamond":
				return "Алмаз";
			case "minecraft:obsidian":
				return "Обсидиан";
			case "minecraft:netherite_ingot":
				return "Незеритовый слиток";
			default:
				return id;
		}
	}

	/** Страницы книги-гайда: подробная инструкция по использованию мода. */
	public static List<String> bookPages() {
		List<String> pages = new ArrayList<>();

		pages.add("§6§lStrike §7— гайд по кисти\n"
				+ "\n"
				+ "§fМодульная кисть удаляет блоки\n"
				+ "§fпо запросу: сначала шанс,\n"
				+ "§fпотом дело.\n"
				+ "\n"
				+ "§aУровень 1 §7— §a50%§7 шанс, §f10§7 блоков.\n"
				+ "§7С уровнем растут и шанс,\n"
				+ "§7и лимит удаляемых блоков.\n"
				+ "\n"
				+ "§7Работает в одиночной игре\n"
				+ "§7и на серверах.");

		pages.add("§6§lКак начать\n"
				+ "\n"
				+ "§71. Введите §e/strike§7 —\n"
				+ "   выдаётся кисть (1 уровень).\n"
				+ "§72. Возьмите кисть в руку.\n"
				+ "§73. Наведите прицел на блок.\n"
				+ "§74. Нажмите §fЛКМ§7.\n"
				+ "\n"
				+ "§7Кисть нельзя получить из креатива:\n"
				+ "§7только команда §e/strike§7.");

		pages.add("§6§lЧто происходит при клике\n"
				+ "\n"
				+ "§71. Блок §eобводится§7 рамочкой.\n"
				+ "§72. Ждём §f" + StrikeConfig.requestDelaySeconds + " сек§7 — видно,\n"
				+ "   что именно будет удалено.\n"
				+ "§73. Бросается шанс (§a50%§7 на 1 уровне).\n"
				+ "§74. Сработал §a✔§7 — блок исчезает.\n"
				+ "   Не сработал §c✘§7 — ничего не изменится.\n"
				+ "\n"
				+ "§7Кисть не ломает блоки обычным\n"
				+ "§7способом: только запросом.");

		pages.add("§6§lРедкость блока\n"
				+ "\n"
				+ "§7Чем реже блок — тем ниже шанс\n"
				+ "§7его удаления:\n"
				+ "\n"
				+ "§fобычный §7× §a1.0\n"
				+ "§fнеобычный §7× §e0.85\n"
				+ "§fредкий §7× §e0.7\n"
				+ "§foчень редкий §7× §e0.55\n"
				+ "§fлегендарный §7× §c0.4\n"
				+ "\n"
				+ "§7Пример: на §fобсидиане§7 шанс\n"
				+ "§7ниже, чем на §fземле§7.");

		pages.add("§6§lПерезарядка\n"
				+ "\n"
				+ "§7После каждого запроса кисть\n"
				+ "§7перезаряжается (§f" + StrikeConfig.cooldownSeconds + " секунды§7).\n"
				+ "\n"
				+ "§7Пока кисть не готова, новый\n"
				+ "§7запрос не создаётся — нужно\n"
				+ "§7подождать.\n"
				+ "\n"
				+ "§7Настройка: §fcooldownSeconds§7\n"
				+ "§7в §fconfig/strike.properties§7.");

		pages.add("§6§lРадиус §7(с " + StrikeConfig.radiusUnlockLevel + " уровня)\n"
				+ "\n"
				+ "§7После §f" + (StrikeConfig.radiusUnlockLevel - 1)
				+ " улучшений§7 можно выбирать\n"
				+ "§7радиус §fПКМ§7 по кисти.\n"
				+ "\n"
				+ "§71 §7— только целевой блок\n"
				+ "§72 §7— область §f3×3×3§7\n"
				+ "§73 §7— область §f5×5×5§7\n"
				+ "§74 §7— область §f7×7×7§7\n"
				+ "\n"
				+ "§7Радиус переключается по кругу.\n"
				+ "§7Шанс бросается §fодин раз§7\n"
				+ "§7на весь запрос.");

		pages.add("§6§lЛимит блоков\n"
				+ "\n"
				+ "§7У каждого уровня свой лимит:\n"
				+ "§7уровень §f1§7 — §f10§7 блоков,\n"
				+ "§7уровень §f5§7 — §f50§7, уровень §f10§7 — §f100§7.\n"
				+ "\n"
				+ "§7Когда лимит исчерпан, введите\n"
				+ "§e/strike limit§7 — лимит восстановится.\n"
				+ "\n"
				+ "§7Платить нужно те же блоки, что и за\n"
				+ "§7прокачку, но §fвдвое меньше§7:\n"
				+ "§7на 1 уровне — §f2 земли§7,\n"
				+ "§7на 5 уровне — §f2 бревна§7,\n"
				+ "§7на 8 уровне — §f2 алмаза§7.\n"
				+ "\n"
				+ "§7Улучшение (§e/strike upgrade§7)\n"
				+ "§7всё равно выгоднее: лимит растёт.");

		pages.add("§6§lПрокачка кисти\n"
				+ "\n"
				+ "§7Каждый уровень требует §fсвои§7\n"
				+ "§7блоки и опыт:\n"
				+ "\n"
				+ upgradeCostLine(2) + "\n"
				+ upgradeCostLine(3) + "\n"
				+ upgradeCostLine(4) + "\n"
				+ upgradeCostLine(5) + "\n"
				+ upgradeCostLine(6) + "\n"
				+ upgradeCostLine(7) + "\n"
				+ upgradeCostLine(8) + "\n"
				+ upgradeCostLine(9) + "\n"
				+ upgradeCostLine(10) + "\n"
				+ "\n"
				+ "§7Команда: §e/strike upgrade");

		List<String> levels = new ArrayList<>();
		levels.add("§6§lУровни кисти\n"
				+ "\n"
				+ "§7Шанс и лимит растут с уровнем.");
		for (int level = 1; level <= StrikeConfig.maxLevel; level++) {
			levels.add("§fУр. " + level + " §7— §a" + StrikeBalance.chancePercent(level) + "%§7, §f"
					+ StrikeBalance.capacity(level) + "§7 блоков"
					+ (StrikeBalance.radiusAvailable(level) ? " §8• радиус" : ""));
		}
		pages.addAll(levels);

		pages.add("§6§lКоманды\n"
				+ "\n"
				+ "§e/strike §7— выдать кисть и статус\n"
				+ "§e/strike upgrade §7— улучшить кисть\n"
				+ "§e/strike info §7— подробности о кисти\n"
				+ "§e/strike guide §7— эта книга\n"
				+ "§e/strike confirm §7— подтвердить запрос\n"
				+ "§e/strike cancel §7— отменить запрос\n"
				+ "§e/strike limit §7— восстановить лимит блоков\n"
				+ "§e/strike help §7— список команд");

		// Правила — одной страницей, всё жёлтым
		pages.add("§e§lПРАВИЛА §eStrike\n"
				+ "§e1. Кисть выдаёт только команда\n"
				+ "§e   §e/strike§e, из креатива её не достать.\n"
				+ "§e2. ЛКМ по блоку — это запрос на\n"
				+ "§e   удаление, а не обычное копание.\n"
				+ "§e3. Сначала блок обводится рамкой,\n"
				+ "§e   потом бросается шанс.\n"
				+ "§e4. Шанс = шанс уровня × редкость:\n"
				+ "§e   обычный ×1, необычный ×0.85,\n"
				+ "§e   редкий ×0.7, очень редкий ×0.55,\n"
				+ "§e   легендарный ×0.4.\n"
				+ "§e5. Чем реже блок, тем ниже шанс\n"
				+ "§e   его удаления.\n"
				+ "§e6. После запроса кисть\n"
				+ "§e   перезаряжается " + StrikeConfig.cooldownSeconds + " с.\n"
				+ "§e7. Один запрос — один бросок шанса\n"
				+ "§e   на всю область.\n"
				+ "§e8. С 4 уровня ПКМ меняет радиус:\n"
				+ "§e   1 → 3×3×3 → 5×5×5 → 7×7×7.\n"
				+ "§e9. Улучшение тратит блоки и опыт,\n"
				+ "§e   лимит блоков сбрасывается.\n"
				+ "§e10. Бедрок, барьер, командные\n"
				+ "§e   блоки, порталы и спавнеры\n"
				+ "§e   удалить нельзя.\n"
				+ "§e11. Мод работает в одиночной игре\n"
				+ "§e   и на сервере.\n"
				+ "§e12. Лимит блоков исчерпался —\n"
				+ "§e   введите §e/strike limit§e и\n"
				+ "§e   заплатите те же блоки, что за\n"
				+ "§e   прокачку, но вдвое меньше.");

		pages.add("§6§lПрава и сервер\n"
				+ "\n"
				+ "§7§e/strike give <ник>§7 — выдать\n"
				+ "§7кисть другому игроку (нужны права\n"
				+ "§7оператора).\n"
				+ "\n"
				+ "§7Мод работает на сервере — клиентам\n"
				+ "§7ставить его необязательно, но для\n"
				+ "§7своей модели кисти можно поставить\n"
				+ "§7и на клиенте.");

		return pages;
	}

	/** Строка требований для указанного уровня (используется в книге). */
	private static String upgradeCostLine(int targetLevel) {
		StrikeConfig.Material material = StrikeBalance.upgradeMaterial(targetLevel);
		if (material == null) {
			return "§fУр. " + targetLevel + " §7— §8максимум";
		}
		return "§fУр. " + targetLevel + " §7— §a" + materialName(material.item) + " §7×§f" + material.count
				+ " §8+ §f" + StrikeBalance.upgradeXpCost(targetLevel) + " §7опыта";
	}

	/** Имя кисти (для отображения в инвентаре). */
	public static String brushName() {
		return BRUSH_NAME;
	}

	/** Имя книги-гайда. */
	public static String bookName() {
		return BOOK_NAME;
	}
}
