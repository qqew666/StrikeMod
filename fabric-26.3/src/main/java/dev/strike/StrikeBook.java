package dev.strike;

import dev.strike.common.StrikeText;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;

import java.util.ArrayList;
import java.util.List;

/**
 * Книга-гайд по моду. В Minecraft 1.21.5+ содержимое книги хранится в компоненте.
 */
public final class StrikeBook {

	private StrikeBook() {
	}

	/** Создаёт книгу-гайд со всеми командами и правилами мода. */
	public static ItemStack createGuideBook() {
		ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);

		List<Component> pages = new ArrayList<>();
		for (String page : StrikeText.bookPages()) {
			pages.add(Component.literal(page));
		}

		WrittenBookContent content = new WrittenBookContent(
				Component.literal(StrikeText.bookTitle()),
				StrikeText.bookAuthor(),
				0,
				pages,
				true);

		stack.set(DataComponents.WRITTEN_BOOK, content);
		stack.set(DataComponents.CUSTOM_NAME, Component.literal(StrikeText.bookName()));

		return stack;
	}
}
