package dev.strike;

import dev.strike.common.StrikeBalance;
import dev.strike.common.StrikeText;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
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

	private StrikeBrush() {
	}

	/** Создаёт новую кисть указанного уровня. */
	public static ItemStack create(int level) {
		ItemStack stack = new ItemStack(Items.BRUSH);
		CompoundTag root = new CompoundTag();
		root.putBoolean(KEY_BRUSH, true);
		root.putInt(KEY_LEVEL, StrikeBalance.clampLevel(level));
		root.putInt(KEY_USED, 0);
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
	public static void addUsed(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		CompoundTag root = tag.getCompound(ROOT);
		root.putInt(KEY_USED, root.getInt(KEY_USED) + 1);
		tag.put(ROOT, root);
		refresh(stack);
	}

	private static CompoundTag root(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		return tag == null ? new CompoundTag() : tag.getCompound(ROOT);
	}

	/** Обновляет отображаемое имя и описание кисти (видно игроку). */
	public static void refresh(ItemStack stack) {
		int level = getLevel(stack);
		int used = getUsed(stack);
		int capacity = StrikeBalance.capacity(level);
		int chance = StrikeBalance.chancePercent(level);
		int remaining = Math.max(0, capacity - used);

		CompoundTag tag = stack.getOrCreateTag();
		CompoundTag display = tag.getCompound("display");
		display.putString("Name", "{\"text\":\"" + StrikeText.brushName() + "\",\"italic\":false}");

		ListTag lore = new ListTag();
		lore.add(StringTag.valueOf("{\"text\":\"§7Уровень: §f" + level + " §8| §7шанс: §a" + chance
				+ "%\",\"italic\":false}"));
		lore.add(StringTag.valueOf("{\"text\":\"§7Удалено блоков: §f" + used + "§7/§f" + capacity
				+ "\",\"italic\":false}"));
		display.put("Lore", lore);

		tag.put("display", display);
	}
}
