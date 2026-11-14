package dev.strike;

import dev.strike.common.StrikeBalance;
import dev.strike.common.StrikeConfig;
import dev.strike.common.StrikeText;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Кисть Strike. В Minecraft 1.20.1 данные хранятся в NBT предмета.
 * <p>
 * Кисть — обычная ванильная кисть ({@code minecraft:brush}) с особой меткой,
 * поэтому её нельзя получить из креатива: в креативе у предметов нет меток.
 */
public final class StrikeBrush {

	private static final String ROOT = "Strike";
	private static final String KEY_BRUSH = "Brush";
	private static final String KEY_LEVEL = "Level";
	private static final String KEY_USED = "Used";
	private static final String KEY_RADIUS = "Radius";

	private StrikeBrush() {
	}

	/** Создаёт новую кисть указанного уровня. */
	public static ItemStack create(int level) {
		ItemStack stack = new ItemStack(Items.BRUSH);
		CompoundTag root = new CompoundTag();
		root.putBoolean(KEY_BRUSH, true);
		root.putInt(KEY_LEVEL, StrikeBalance.clampLevel(level));
		root.putInt(KEY_USED, 0);
		root.putInt(KEY_RADIUS, 1);
		stack.getOrCreateTag().put(ROOT, root);
		refresh(stack);
		return stack;
	}

	/** Является ли предмет кистью Strike. */
	public static boolean isBrush(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return false;
		}
		CompoundTag tag = stack.getTag();
		if (tag == null) {
			return false;
		}
		return tag.getCompound(ROOT).getBoolean(KEY_BRUSH);
	}

	/** Уровень кисти. */
	public static int getLevel(ItemStack stack) {
		int level = root(stack).getInt(KEY_LEVEL);
		return level <= 0 ? 1 : StrikeBalance.clampLevel(level);
	}

	/** Сколько блоков уже удалено этой кистью. */
	public static int getUsed(ItemStack stack) {
		return root(stack).getInt(KEY_USED);
	}

	/** Выбранный радиус удаления (1 = только целевой блок). */
	public static int getRadius(ItemStack stack) {
		int radius = root(stack).getInt(KEY_RADIUS);
		return radius <= 0 ? 1 : StrikeBalance.clampRadius(radius);
	}

	/** Устанавливает уровень кисти и сбрасывает счётчик удалённых блоков. */
	public static void setLevel(ItemStack stack, int level) {
		CompoundTag tag = stack.getOrCreateTag();
		CompoundTag root = tag.getCompound(ROOT);
		root.putInt(KEY_LEVEL, StrikeBalance.clampLevel(level));
		root.putInt(KEY_USED, 0);
		tag.put(ROOT, root);
		refresh(stack);
	}

	/** Увеличивает счётчик удалённых блоков. */
	public static void addUsed(ItemStack stack, int count) {
		if (count <= 0) {
			return;
		}
		CompoundTag tag = stack.getOrCreateTag();
		CompoundTag root = tag.getCompound(ROOT);
		root.putInt(KEY_USED, root.getInt(KEY_USED) + count);
		tag.put(ROOT, root);
		refresh(stack);
	}

	/** Устанавливает количество уже удалённых блоков. */
	public static void setUsed(ItemStack stack, int used) {
		CompoundTag tag = stack.getOrCreateTag();
		CompoundTag root = tag.getCompound(ROOT);
		root.putInt(KEY_USED, Math.max(0, used));
		tag.put(ROOT, root);
		refresh(stack);
	}

	/** Устанавливает радиус удаления. */
	public static void setRadius(ItemStack stack, int radius) {
		CompoundTag tag = stack.getOrCreateTag();
		CompoundTag root = tag.getCompound(ROOT);
		root.putInt(KEY_RADIUS, StrikeBalance.clampRadius(radius));
		tag.put(ROOT, root);
		refresh(stack);
	}

	/** Переключает радиус по кругу (1 → 2 → … → max → 1) и возвращает новый. */
	public static int cycleRadius(ItemStack stack) {
		int max = Math.max(1, StrikeConfig.maxRadius);
		int next = getRadius(stack) % max + 1;
		setRadius(stack, next);
		return next;
	}

	private static CompoundTag root(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		return tag == null ? new CompoundTag() : tag.getCompound(ROOT);
	}

	/** Ищет предмет по id (например {@code minecraft:dirt}). */
	public static Item materialItem(String id) {
		for (Item item : BuiltInRegistries.ITEM) {
			if (BuiltInRegistries.ITEM.getKey(item).toString().equals(id)) {
				return item;
			}
		}
		return null;
	}

	/** Стоимость восстановления лимита блоков для этой кисти (текст для чата). */
	public static String limitCost(ItemStack brush) {
		StrikeConfig.Material material = StrikeBalance.limitMaterial(getLevel(brush));
		if (material == null) {
			return "бесплатно";
		}
		Item item = materialItem(material.item);
		String name = item == null ? material.item : new ItemStack(item).getHoverName().getString();
		return "§f" + name + " §7×§f" + material.count;
	}

	/** Обновляет отображаемое имя и описание кисти (видно игроку). */
	public static void refresh(ItemStack stack) {
		int level = getLevel(stack);
		int used = getUsed(stack);
		int capacity = StrikeBalance.capacity(level);
		int chance = StrikeBalance.chancePercent(level);
		int radius = getRadius(stack);
		int side = StrikeBalance.radiusSide(radius);

		CompoundTag tag = stack.getOrCreateTag();
		CompoundTag display = tag.getCompound("display");
		display.putString("Name", "{\"text\":\"" + StrikeText.brushName() + "\",\"italic\":false}");

		ListTag lore = new ListTag();
		lore.add(StringTag.valueOf("{\"text\":\"§7Уровень: §f" + level + " §8| §7шанс: §a" + chance
				+ "%\",\"italic\":false}"));
		lore.add(StringTag.valueOf("{\"text\":\"§7Удалено блоков: §f" + used + "§7/§f" + capacity
				+ "\",\"italic\":false}"));
		if (StrikeBalance.radiusAvailable(level)) {
			lore.add(StringTag.valueOf("{\"text\":\"§7Радиус: §d" + radius + " §8(§d" + side + "×" + side
					+ "×" + side + "§8) §7ПКМ — изменить\",\"italic\":false}"));
		} else {
			lore.add(StringTag.valueOf("{\"text\":\"§8Радиус откроется на "
					+ StrikeConfig.radiusUnlockLevel + " уровне\",\"italic\":false}"));
		}
		display.put("Lore", lore);

		tag.put("display", display);
	}
}
