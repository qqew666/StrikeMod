package dev.strike;

import dev.strike.common.StrikeText;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
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
}
