package dev.strike;

import dev.strike.common.StrikeText;
import net.minecraft.core.nbt.CompoundTag;
import net.minecraft.core.nbt.ListTag;
import net.minecraft.core.nbt.StringTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Книга-гайд по моду. В Minecraft 1.20.1 страницы книги хранятся в NBT.
 */
public final class StrikeBook {

	private StrikeBook() {
	}

	/** Создаёт книгу-гайд со всеми командами и правилами мода. */
	public static ItemStack createGuideBook() {
		ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);

		CompoundTag tag = stack.getOrCreateTag();
		tag.putString("title", StrikeText.bookTitle());
		tag.putString("author", StrikeText.bookAuthor());

		ListTag pages = new ListTag();
		for (String page : StrikeText.bookPages()) {
			pages.add(StringTag.valueOf(page));
		}
		tag.put("pages", pages);

		CompoundTag display = new CompoundTag();
		display.putString("Name", "{\"text\":\"" + StrikeText.bookName() + "\",\"italic\":false}");
		tag.put("display", display);

		return stack;
	}

	/** Является ли предмет нашей книгой-гайдом (по содержимому страниц). */
	public static boolean isGuideBook(ItemStack stack) {
		if (stack == null || stack.isEmpty() || !stack.is(Items.WRITTEN_BOOK)) {
			return false;
		}
		CompoundTag tag = stack.getTag();
		if (tag == null) {
			return false;
		}
		ListTag pages = tag.getList("pages", 8);
		for (int i = 0; i < pages.size(); i++) {
			String page = pages.getString(i);
			if (page.contains("Strike")) {
				return true;
			}
		}
		return false;
	}
}
