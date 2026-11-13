package dev.strike;

import dev.strike.common.StrikeBalance;
import dev.strike.common.StrikeText;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.NbtComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ListComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Кисть Strike. В Minecraft 1.21+ данные и описание предмета хранятся в компонентах.
 * <p>
 * Кисть — обычная ванильная кисть ({@code minecraft:brush}) с особой меткой,
 * поэтому её нельзя получить из креатива: у предметов в креативе нет меток.
 */
public final class StrikeBrush {

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
		stack.set(DataComponents.CUSTOM_DATA, NbtComponent.of(root));

		refresh(stack);
		return stack;
	}

	/** Является ли предмет кистью Strike. */
	public static boolean isBrush(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return false;
		}
		return root(stack).getBoolean(KEY_BRUSH);
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
		CompoundTag root = root(stack);
		root.putInt(KEY_LEVEL, StrikeBalance.clampLevel(level));
		root.putInt(KEY_USED, 0);
		save(stack, root);
		refresh(stack);
	}

	/** Увеличивает счётчик удалённых блоков. */
	public static void addUsed(ItemStack stack) {
		CompoundTag root = root(stack);
		root.putInt(KEY_USED, root.getInt(KEY_USED) + 1);
		save(stack, root);
		refresh(stack);
	}

	private static CompoundTag root(ItemStack stack) {
		NbtComponent data = stack.get(DataComponents.CUSTOM_DATA);
		return data == null ? new CompoundTag() : data.copyNbt();
	}

	private static void save(ItemStack stack, CompoundTag root) {
		stack.set(DataComponents.CUSTOM_DATA, NbtComponent.of(root));
	}

	/** Обновляет отображаемое имя и описание кисти (видно игроку). */
	public static void refresh(ItemStack stack) {
		int level = getLevel(stack);
		int used = getUsed(stack);
		int capacity = StrikeBalance.capacity(level);
		int chance = StrikeBalance.chancePercent(level);

		stack.set(DataComponents.CUSTOM_NAME, Component.literal(StrikeText.brushName()));

		ListComponent lore = ListComponent.create();
		lore.add(Component.literal("§7Уровень: §f" + level + " §8| §7шанс: §a" + chance + "%"));
		lore.add(Component.literal("§7Удалено блоков: §f" + used + "§7/§f" + capacity));
		stack.set(DataComponents.LORE, lore);
	}
}
