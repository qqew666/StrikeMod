package dev.strike;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

/**
 * Обработка клика по блоку кистью Strike.
 */
public final class StrikeEvents {

	private StrikeEvents() {
	}

	public static void register() {
		AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
			if (!(world instanceof ServerLevel level) || !(player instanceof ServerPlayer serverPlayer)) {
				return InteractionResult.PASS;
			}

			// Мод работает только на мультиплеере
			if (!StrikeMod.isMultiplayer(serverPlayer)) {
				return InteractionResult.PASS;
			}

			ItemStack stack = player.getMainHandItem();
			if (!StrikeBrush.isBrush(stack)) {
				return InteractionResult.PASS;
			}

			StrikeRequest.handle(serverPlayer, level, pos, world.getBlockState(pos), stack);

			// SUCCESS — отменяем обычное разрушение блока
			return InteractionResult.SUCCESS;
		});
	}
}
