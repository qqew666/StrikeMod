package dev.strike;

import dev.strike.common.StrikeText;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;

import java.util.ArrayList;
import java.util.List;

/**
 * Книга-гайд по моду. В Minecraft 1.21+ содержимое книги хранится в компоненте.
 */
public final class StrikeBook {

	private StrikeBook() {
	}

	/** Создаёт книгу-гайд со всеми командами и правилами мода. */
	public static ItemStack createGuideBook() {
		ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);

		List<Filterable<Component>> pages = new ArrayList<>();
		for (String page : StrikeText.bookPages()) {
			pages.add(Filterable.passThrough(Component.literal(page)));
		}

		WrittenBookContent content = new WrittenBookContent(
				Filterable.passThrough(StrikeText.bookTitle()),
				StrikeText.bookAuthor(),
				0,
				pages,
				true);

		stack.set(DataComponents.WRITTEN_BOOK_CONTENT, content);
		stack.set(DataComponents.CUSTOM_NAME, Component.literal(StrikeText.bookName()));

		return stack;
	}

	/** Является ли предмет нашей книгой-гайдом (по содержимому страниц). */
	public static boolean isGuideBook(ItemStack stack) {
		if (stack == null || stack.isEmpty() || !stack.is(Items.WRITTEN_BOOK)) {
			return false;
		}
		WrittenBookContent content = stack.get(DataComponents.WRITTEN_BOOK_CONTENT);
		if (content == null) {
			return false;
		}
		for (Component page : content.getPages(true)) {
			if (page.getString().contains("Strike")) {
				return true;
			}
		}
		return false;
	}
}
