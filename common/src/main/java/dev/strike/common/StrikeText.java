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

	public static String request(int x, int y, int z, int chance, int remaining) {
		return "§e§lЗапрос на удаление блока §f[" + x + ", " + y + ", " + z + "] "
				+ "§7• §aшанс " + chance + "% "
				+ "§7• §bосталось " + remaining + " блоков";
	}

	public static String success() {
		return "§a✔ §aБлок удалён! Шанс сработал.";
	}

	public static String failure() {
		return "§c✘ §cЗапрос отклонён — шанс не сработал.";
	}

	public static String quota(int capacity) {
		return "§c✘ Лимит блоков исчерпан (§f" + capacity + "§c). Улучшите кисть: §e/strike upgrade";
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

	public static String needEmeralds(int need, int have) {
		return "§c✘ Не хватает изумрудов: нужно §f" + need + "§c, есть §f" + have + "§c.";
	}

	public static String needXp(int need) {
		return "§c✘ Не хватает опыта: нужно §f" + need + "§c уровня опыта.";
	}

	public static String upgraded(int level, int chance, int capacity) {
		return "§a✔ §aКисть улучшена до уровня §f" + level + "§a! Шанс §a" + chance + "%§a, лимит §f"
				+ capacity + "§a блоков.";
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

	public static String requestConfirmed() {
		return "§eЗапрос подтверждён, бросаем кости…";
	}

	public static String info(int level, int chance, int used, int capacity) {
		return "§6§lStrike §7— кисть\n"
				+ "§7Уровень: §f" + level + " §8| §7Шанс: §a" + chance + "%\n"
				+ "§7Удалено блоков: §f" + used + "§7/§f" + capacity + "\n"
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
				+ "§7Кисть выдаётся только командой и недоступна в креативе.";
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

	/** Страницы книги-гайда. */
	public static List<String> bookPages() {
		List<String> pages = new ArrayList<>();

		pages.add("§6§lStrike §7— модульная кисть\n"
				+ "\n"
				+ "§fКисть удаляет блоки по запросу:\n"
				+ "каждый запрос проходит проверку шанса.\n"
				+ "\n"
				+ "§aУровень 1 §7— §a50%§7 шанс, §f10§7 блоков.\n"
				+ "С каждым уровнем растёт и шанс, и лимит\n"
				+ "удаляемых блоков.\n"
				+ "\n"
				+ "§7Мод работает только на мультиплеере.");

		pages.add("§6§lКоманды\n"
				+ "\n"
				+ "§e/strike §7— выдать кисть\n"
				+ "§e/strike upgrade §7— улучшить кисть\n"
				+ "§e/strike info §7— состояние кисти\n"
				+ "§e/strike guide §7— эта книга\n"
				+ "§e/strike confirm §7— подтвердить запрос\n"
				+ "§e/strike cancel §7— отменить запрос\n"
				+ "\n"
				+ "§7Кисть нельзя получить из креатива:\n"
				+ "только команда §e/strike§7.");

		pages.add("§6§lКак пользоваться\n"
				+ "\n"
				+ "§71. Возьмите кисть в руку.\n"
				+ "§72. Наведите прицел на блок.\n"
				+ "§73. Нажмите §fЛКМ§7.\n"
				+ "§74. Появится §eзапрос на удаление§7:\n"
				+ "   шанс и остаток блоков.\n"
				+ "§75. Шанс сработал §a✔§7 — блок исчезнет.\n"
				+ "   Не сработал §c✘§7 — ничего не изменится.\n"
				+ "\n"
				+ "§7Ломать блоки обычным способом кисть\n"
				+ "не даёт — только запрос.");

		List<String> levels = new ArrayList<>();
		levels.add("§6§lУровни кисти\n"
				+ "\n"
				+ "§7Шанс и лимит растут с уровнем.");
		for (int level = 1; level <= StrikeConfig.maxLevel; level++) {
			levels.add("§fУр. " + level + " §7— §a" + StrikeBalance.chancePercent(level) + "%§7, §f"
					+ StrikeBalance.capacity(level) + "§7 блоков");
		}
		pages.addAll(levels);

		pages.add("§6§lУлучшение кисти\n"
				+ "\n"
				+ "§7Возьмите кисть в руку и введите:\n"
				+ "§e/strike upgrade\n"
				+ "\n"
				+ "§7Цена следующего уровня:\n"
				+ "§a" + StrikeBalance.upgradeEmeraldCost(1) + "§7 изумрудов и §f"
				+ StrikeBalance.upgradeXpCost(1) + "§7 уровней опыта\n"
				+ "§7(цена растёт с каждым уровнем).\n"
				+ "\n"
				+ "§7Лимит блоков сбрасывается при улучшении,\n"
				+ "шанс удаления растёт.\n"
				+ "\n"
				+ "§8Strike — только для мультиплеера.");

		return pages;
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
