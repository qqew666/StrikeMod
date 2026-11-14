package dev.strike;

import dev.strike.common.StrikeBalance;
import dev.strike.common.StrikeConfig;
import dev.strike.common.StrikeText;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

/**
 * Обработка кликов кистью Strike: ЛКМ — запрос на удаление, ПКМ — выбор радиуса.
 */
public final class StrikeEvents {

	private StrikeEvents() {
	}

	public static void register() {
		// ЛКМ по блоку — запрос на удаление
		AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
			if (!(world instanceof ServerLevel level) || !(player instanceof ServerPlayer serverPlayer)) {
				return InteractionResult.PASS;
			}

			if (!StrikeMod.isMultiplayer()) {
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

		// ПКМ по блоку кистью — переключение радиуса
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (!(world instanceof ServerLevel level) || !(player instanceof ServerPlayer serverPlayer)) {
				return InteractionResult.PASS;
			}

			if (!StrikeMod.isMultiplayer()) {
				return InteractionResult.PASS;
			}

			ItemStack stack = player.getMainHandItem();
			if (!StrikeBrush.isBrush(stack)) {
				return InteractionResult.PASS;
			}

			int brushLevel = StrikeBrush.getLevel(stack);

			if (!StrikeBalance.radiusAvailable(brushLevel)) {
				serverPlayer.sendSystemMessage(
						Component.literal(StrikeText.radiusLocked(StrikeConfig.radiusUnlockLevel)), true);
				return InteractionResult.SUCCESS;
			}

			int radius = StrikeBrush.cycleRadius(stack);
			serverPlayer.sendSystemMessage(
					Component.literal(StrikeText.radiusChanged(radius, StrikeBalance.radiusSide(radius))), true);
			level.playSound((net.minecraft.world.entity.Entity) null, hitResult.getBlockPos(),
					SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.6f, 1.2f);

			return InteractionResult.SUCCESS;
		});

		// Тик сервера — обводка активных запросов и разруливание результата
		ServerTickEvents.END_SERVER_TICK.register(StrikeRequest::tick);
	}
}
