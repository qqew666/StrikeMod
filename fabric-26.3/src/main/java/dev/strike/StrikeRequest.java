package dev.strike;

import dev.strike.common.StrikeBalance;
import dev.strike.common.StrikeConfig;
import dev.strike.common.StrikeText;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Логика запроса на удаление блока: показ запроса, бросок шанса и результат.
 */
public final class StrikeRequest {

	/** Запрос, ожидающий подтверждения (если включён {@code requireConfirmation}). */
	private static final Map<UUID, Pending> PENDING = new HashMap<>();

	private StrikeRequest() {
	}

	/** Отложенный запрос: позиция, шанс и мир, в котором сделан запрос. */
	private record Pending(BlockPos pos, int chance, ServerLevel level) {
	}

	/** Обрабатывает клик по блоку кистью. */
	public static void handle(ServerPlayer player, ServerLevel level, BlockPos pos, BlockState state, ItemStack stack) {
		int brushLevel = StrikeBrush.getLevel(stack);
		int used = StrikeBrush.getUsed(stack);
		int capacity = StrikeBalance.capacity(brushLevel);

		if (used >= capacity) {
			player.sendSystemMessage(Component.literal(StrikeText.quota(capacity)), true);
			return;
		}

		if (isProtected(state)) {
			player.sendSystemMessage(Component.literal(StrikeText.protectedBlock()), true);
			return;
		}

		int chance = StrikeBalance.chancePercent(brushLevel);

		player.sendSystemMessage(Component.literal(
				StrikeText.request(pos.getX(), pos.getY(), pos.getZ(), chance, capacity - used)), true);

		level.sendParticles(ParticleTypes.CRIT, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
				10, 0.3, 0.3, 0.3, 0.0);
		level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK, SoundSource.BLOCKS, 0.7f, 1.5f);

		if (StrikeConfig.requireConfirmation) {
			PENDING.put(player.getUUID(), new Pending(pos.immutable(), chance, level));
			player.sendSystemMessage(Component.literal(StrikeText.confirmHint()), false);
		} else {
			resolve(player, level, pos, stack, chance);
		}
	}

	/** Подтверждает отложенный запрос. */
	public static int confirm(ServerPlayer player) {
		Pending pending = PENDING.remove(player.getUUID());
		if (pending == null) {
			player.sendSystemMessage(Component.literal(StrikeText.noPendingRequest()), true);
			return 0;
		}

		ItemStack stack = StrikeCommands.findBrush(player);
		if (stack == null) {
			player.sendSystemMessage(Component.literal(StrikeText.noBrush()), true);
			return 0;
		}

		ServerLevel level = pending.level();
		BlockState state = level.getBlockState(pending.pos());
		if (state.isAir()) {
			player.sendSystemMessage(Component.literal(StrikeText.noPendingRequest()), true);
			return 0;
		}

		player.sendSystemMessage(Component.literal(StrikeText.requestConfirmed()), true);
		resolve(player, level, pending.pos(), stack, pending.chance());
		return 1;
	}

	/** Отменяет отложенный запрос. */
	public static int cancel(ServerPlayer player) {
		if (PENDING.remove(player.getUUID()) == null) {
			player.sendSystemMessage(Component.literal(StrikeText.noPendingRequest()), true);
			return 0;
		}
		player.sendSystemMessage(Component.literal("§7Запрос отменён."), true);
		return 1;
	}

	/** Бросает шанс и применяет результат. */
	private static void resolve(ServerPlayer player, ServerLevel level, BlockPos pos, ItemStack stack, int chance) {
		boolean success = RandomSource.create().nextFloat() * 100.0f < chance;

		if (success) {
			level.destroyBlock(pos, false);
			level.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
					12, 0.35, 0.35, 0.35, 0.0);
			level.playSound(null, pos, SoundEvents.NOTE_BLOCK_PLING, SoundSource.BLOCKS, 0.9f, 1.4f);
			StrikeBrush.addUsed(stack);
			player.sendSystemMessage(Component.literal(StrikeText.success()), true);
		} else {
			level.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
					10, 0.25, 0.25, 0.25, 0.01);
			level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BASS, SoundSource.BLOCKS, 0.8f, 0.7f);
			player.sendSystemMessage(Component.literal(StrikeText.failure()), true);
		}
	}

	/** Блоки, которые кистью удалять нельзя. */
	public static boolean isProtected(BlockState state) {
		return StrikeConfig.protectedBlocks.contains(
				BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString());
	}
}
