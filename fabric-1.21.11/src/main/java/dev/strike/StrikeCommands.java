package dev.strike;

import dev.strike.common.StrikeBalance;
import dev.strike.common.StrikeConfig;
import dev.strike.common.StrikeText;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Команды мода: {@code /strike}, {@code /strike upgrade}, {@code /strike info},
 * {@code /strike guide}, {@code /strike confirm}, {@code /strike cancel}, {@code /strike give}.
 */
public final class StrikeCommands {

	private StrikeCommands() {
	}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(Commands.literal("strike")
					.requires(source -> true)
					.executes(context -> main(context.getSource()))
					.then(Commands.literal("upgrade")
							.executes(context -> upgrade(context.getSource())))
					.then(Commands.literal("info")
							.executes(context -> info(context.getSource())))
					.then(Commands.literal("guide")
							.executes(context -> guide(context.getSource())))
					.then(Commands.literal("confirm")
							.executes(context -> StrikeRequest.confirm(context.getSource().getPlayerOrException())))
					.then(Commands.literal("cancel")
							.executes(context -> StrikeRequest.cancel(context.getSource().getPlayerOrException())))
					.then(Commands.literal("help")
							.executes(context -> help(context.getSource())))
					.then(Commands.literal("give")
							.requires(source -> StrikePermissions.isOperator(source))
							.then(Commands.argument("player", EntityArgument.player())
									.executes(context -> give(context.getSource(),
											(ServerPlayer) EntityArgument.getPlayer(context, "player"))))));
		});
	}

	/** {@code /strike} — выдать кисть или показать её состояние. */
	private static int main(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();
		if (!StrikeMod.isMultiplayer()) {
			source.sendFailure(Component.literal(StrikeText.multiplayerOnly()));
			return 0;
		}

		ItemStack brush = findBrush(player);
		if (brush == null) {
			player.getInventory().add(StrikeBrush.create(1));
			brush = findBrush(player);
			source.sendSuccess(() -> Component.literal(StrikeText.brushGiven()), false);
		} else {
			StrikeBrush.refresh(brush);
		}

		final ItemStack current = brush;
		if (current != null) {
			final int level = StrikeBrush.getLevel(current);
			source.sendSuccess(() -> Component.literal(StrikeText.info(
					level,
					StrikeBalance.chancePercent(level),
					StrikeBrush.getUsed(current),
					StrikeBalance.capacity(level),
					StrikeBrush.getRadius(current))), false);
		}
		return 1;
	}

	/** {@code /strike upgrade} — улучшить кисть. */
	private static int upgrade(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();
		if (!StrikeMod.isMultiplayer()) {
			source.sendFailure(Component.literal(StrikeText.multiplayerOnly()));
			return 0;
		}

		ItemStack brush = findBrush(player);
		if (brush == null) {
			source.sendFailure(Component.literal(StrikeText.noBrush()));
			return 0;
		}

		int level = StrikeBrush.getLevel(brush);
		if (!StrikeBalance.canUpgrade(level)) {
			source.sendSuccess(() -> Component.literal(StrikeText.maxLevel(level)), false);
			return 0;
		}

		int newLevel = level + 1;
		StrikeConfig.Material material = StrikeBalance.upgradeMaterial(newLevel);
		if (material == null) {
			source.sendSuccess(() -> Component.literal(StrikeText.maxLevel(level)), false);
			return 0;
		}

		Item materialItem = materialItem(material.item);
		if (materialItem == null) {
			source.sendFailure(Component.literal("§c✘ Неизвестный материал прокачки: §f" + material.item));
			return 0;
		}

		int need = material.count;
		int have = countItem(player, materialItem);

		if (have < need) {
			source.sendFailure(Component.literal(
					StrikeText.needMaterial(materialItem.getName().getString(), need, have)));
			return 0;
		}
		int xpCost = StrikeBalance.upgradeXpCost(newLevel);
		if (player.experienceLevel < xpCost) {
			source.sendFailure(Component.literal(StrikeText.needXp(xpCost)));
			return 0;
		}

		removeItem(player, materialItem, need);
		if (xpCost > 0) {
			player.giveExperienceLevels(-xpCost);
		}

		StrikeBrush.setLevel(brush, newLevel);

		source.sendSuccess(() -> Component.literal(StrikeText.upgraded(
				newLevel,
				StrikeBalance.chancePercent(newLevel),
				StrikeBalance.capacity(newLevel))), true);
		return 1;
	}

	/** {@code /strike info} — состояние кисти. */
	private static int info(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();
		ItemStack brush = findBrush(player);
		if (brush == null) {
			source.sendSuccess(() -> Component.literal(StrikeText.noBrush()), false);
			return 0;
		}
		StrikeBrush.refresh(brush);
		int level = StrikeBrush.getLevel(brush);
		source.sendSuccess(() -> Component.literal(StrikeText.info(
				level,
				StrikeBalance.chancePercent(level),
				StrikeBrush.getUsed(brush),
				StrikeBalance.capacity(level),
				StrikeBrush.getRadius(brush))), false);
		return 1;
	}

	/** {@code /strike guide} — выдать книгу-гайд. */
	private static int guide(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();
		player.getInventory().add(StrikeBook.createGuideBook());
		source.sendSuccess(() -> Component.literal(StrikeText.guideGiven()), true);
		return 1;
	}

	/** {@code /strike help} — список команд. */
	private static int help(CommandSourceStack source) {
		source.sendSuccess(() -> Component.literal(StrikeText.help()), false);
		return 1;
	}

	/** {@code /strike give <player>} — выдать кисть другому игроку. */
	private static int give(CommandSourceStack source, ServerPlayer target) {
		if (!StrikeMod.isMultiplayer()) {
			source.sendFailure(Component.literal(StrikeText.multiplayerOnly()));
			return 0;
		}
		target.getInventory().add(StrikeBrush.create(1));
		source.sendSuccess(() -> Component.literal(StrikeText.givenTo(target.getName().getString())), true);
		target.displayClientMessage(Component.literal(StrikeText.brushGiven()), true);
		return 1;
	}

	/** Ищет предмет по id (например {@code minecraft:dirt}). */
	private static Item materialItem(String id) {
		for (Item item : BuiltInRegistries.ITEM) {
			if (BuiltInRegistries.ITEM.getKey(item).toString().equals(id)) {
				return item;
			}
		}
		return null;
	}

	/** Ищет кисть Strike в инвентаре игрока. */
	public static ItemStack findBrush(ServerPlayer player) {
		Container inventory = player.getInventory();
		for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
			ItemStack stack = inventory.getItem(slot);
			if (StrikeBrush.isBrush(stack)) {
				return stack;
			}
		}
		return null;
	}

	/** Сколько таких-то предметов у игрока. */
	private static int countItem(ServerPlayer player, Item item) {
		Container inventory = player.getInventory();
		int total = 0;
		for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
			ItemStack stack = inventory.getItem(slot);
			if (!stack.isEmpty() && stack.getItem() == item) {
				total += stack.getCount();
			}
		}
		return total;
	}

	/** Забирает у игрока нужное количество предметов. */
	private static void removeItem(ServerPlayer player, Item item, int count) {
		Container inventory = player.getInventory();
		for (int slot = 0; slot < inventory.getContainerSize() && count > 0; slot++) {
			ItemStack stack = inventory.getItem(slot);
			if (stack.isEmpty() || stack.getItem() != item) {
				continue;
			}
			int take = Math.min(count, stack.getCount());
			count -= take;
			if (stack.getCount() - take <= 0) {
				inventory.setItem(slot, ItemStack.EMPTY);
			} else {
				stack.shrink(take);
			}
		}
	}
}
