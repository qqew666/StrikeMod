package dev.strike;

import dev.strike.common.StrikeBalance;
import dev.strike.common.StrikeConfig;
import dev.strike.common.StrikeText;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Логика запроса на удаление блока: обводка блока, бросок шанса и результат.
 * <p>
 * Запрос живёт {@code requestDelaySeconds} секунд — всё это время блок (или
 * область радиуса) обведён рамкой из частиц. Потом бросается шанс: удалось —
 * блоки исчезают, нет — ничего не меняется. После запроса кисть перезаряжается.
 */
public final class StrikeRequest {

	/** Активные запросы игроков. */
	private static final Map<UUID, Pending> PENDING = new HashMap<>();

	/** Момент окончания перезарядки кисти (в мс). */
	private static final Map<UUID, Long> COOLDOWN = new HashMap<>();

	private StrikeRequest() {
	}

	/** Отложенный запрос: позиция, шанс, радиус и мир. */
	private static final class Pending {

		private final BlockPos pos;
		private final int chance;
		private final int radius;
		private final ServerLevel level;
		private final long createdAt;
		private boolean confirmed;

		Pending(BlockPos pos, int chance, int radius, ServerLevel level, long createdAt) {
			this.pos = pos;
			this.chance = chance;
			this.radius = radius;
			this.level = level;
			this.createdAt = createdAt;
		}
	}

	/** Обрабатывает клик по блоку кистью. */
	public static void handle(ServerPlayer player, ServerLevel level, BlockPos pos, BlockState state, ItemStack stack) {
		if (PENDING.containsKey(player.getUUID())) {
			player.displayClientMessage(Component.literal(StrikeText.pendingExists()), true);
			return;
		}

		long cooldown = cooldownLeft(player);
		if (cooldown > 0) {
			player.displayClientMessage(Component.literal(StrikeText.cooldown(cooldown / 1000.0)), true);
			return;
		}

		int brushLevel = StrikeBrush.getLevel(stack);
		int used = StrikeBrush.getUsed(stack);
		int capacity = StrikeBalance.capacity(brushLevel);

		if (used >= capacity) {
			player.displayClientMessage(Component.literal(StrikeText.quota(capacity)), true);
			return;
		}

		if (isProtected(state)) {
			player.displayClientMessage(Component.literal(StrikeText.protectedBlock()), true);
			return;
		}

		int chance = StrikeBalance.chancePercent(brushLevel);
		int radius = StrikeBrush.getRadius(stack);

		player.displayClientMessage(Component.literal(
				StrikeText.request(pos.getX(), pos.getY(), pos.getZ(), chance, capacity - used, radius)), true);

		level.sendParticles(ParticleTypes.CRIT, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
				10, 0.3, 0.3, 0.3, 0.0);
		level.playSound((net.minecraft.world.entity.Entity) null, pos, SoundEvents.UI_BUTTON_CLICK.value(),
				SoundSource.BLOCKS, 0.7f, 1.5f);

		PENDING.put(player.getUUID(), new Pending(pos.immutable(), chance, radius, level, System.currentTimeMillis()));

		if (StrikeConfig.requireConfirmation) {
			player.displayClientMessage(Component.literal(StrikeText.confirmHint()), false);
		}
	}

	/** Тик сервера: обводит активные запросы и разруливает их результат. */
	public static void tick(MinecraftServer server) {
		if (PENDING.isEmpty()) {
			return;
		}

		long now = System.currentTimeMillis();

		Iterator<Map.Entry<UUID, Pending>> iterator = PENDING.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<UUID, Pending> entry = iterator.next();
			Pending pending = entry.getValue();

			drawOutline(pending.level, pending.pos, pending.radius);

			if (StrikeConfig.requireConfirmation && !pending.confirmed) {
				continue;
			}

			if (now - pending.createdAt < StrikeBalance.requestDelayMillis()) {
				continue;
			}

			iterator.remove();

			ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
			if (player == null) {
				continue;
			}

			resolve(player, pending);
		}
	}

	/** Подтверждает отложенный запрос. */
	public static int confirm(ServerPlayer player) {
		Pending pending = PENDING.get(player.getUUID());
		if (pending == null) {
			player.displayClientMessage(Component.literal(StrikeText.noPendingRequest()), true);
			return 0;
		}

		pending.confirmed = true;
		player.displayClientMessage(Component.literal(StrikeText.requestConfirmed()), true);
		return 1;
	}

	/** Отменяет отложенный запрос. */
	public static int cancel(ServerPlayer player) {
		if (PENDING.remove(player.getUUID()) == null) {
			player.displayClientMessage(Component.literal(StrikeText.noPendingRequest()), true);
			return 0;
		}
		player.displayClientMessage(Component.literal("§7Запрос отменён."), true);
		return 1;
	}

	/** Бросает шанс и применяет результат. */
	private static void resolve(ServerPlayer player, Pending pending) {
		ItemStack stack = StrikeCommands.findBrush(player);
		if (stack == null) {
			player.displayClientMessage(Component.literal(StrikeText.noBrush()), true);
			return;
		}

		ServerLevel level = pending.level;
		BlockPos pos = pending.pos;
		boolean success = RandomSource.create().nextFloat() * 100.0f < pending.chance;

		if (success) {
			int limit = Math.max(0, StrikeBalance.capacity(StrikeBrush.getLevel(stack)) - StrikeBrush.getUsed(stack));
			int removed = removeArea(level, pos, pending.radius, limit);

			level.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
					12, 0.35, 0.35, 0.35, 0.0);
			level.playSound((net.minecraft.world.entity.Entity) null, pos, SoundEvents.NOTE_BLOCK_PLING.value(),
					SoundSource.BLOCKS, 0.9f, 1.4f);
			StrikeBrush.addUsed(stack, removed);
			player.displayClientMessage(Component.literal(StrikeText.success(removed)), true);
		} else {
			level.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
					10, 0.25, 0.25, 0.25, 0.01);
			level.playSound((net.minecraft.world.entity.Entity) null, pos, SoundEvents.NOTE_BLOCK_BASS.value(),
					SoundSource.BLOCKS, 0.8f, 0.7f);
			player.displayClientMessage(Component.literal(StrikeText.failure()), true);
		}

		COOLDOWN.put(player.getUUID(), System.currentTimeMillis() + StrikeBalance.cooldownMillis());
	}

	/** Удаляет блоки в области радиуса (не больше, чем осталось по лимиту). */
	private static int removeArea(ServerLevel level, BlockPos center, int radius, int limit) {
		if (limit <= 0) {
			return 0;
		}

		int half = radius - 1;
		int removed = 0;

		for (int dx = -half; dx <= half && removed < limit; dx++) {
			for (int dy = -half; dy <= half && removed < limit; dy++) {
				for (int dz = -half; dz <= half && removed < limit; dz++) {
					BlockPos target = center.offset(dx, dy, dz);
					BlockState state = level.getBlockState(target);
					if (state.isAir() || isProtected(state)) {
						continue;
					}
					level.destroyBlock(target, false);
					removed++;
				}
			}
		}

		return removed;
	}

	/** Обводит область запроса рамкой из частиц. */
	private static void drawOutline(ServerLevel level, BlockPos center, int radius) {
		int half = radius - 1;
		double x0 = center.getX() - half - 0.02;
		double y0 = center.getY() - half - 0.02;
		double z0 = center.getZ() - half - 0.02;
		double x1 = center.getX() + half + 1.02;
		double y1 = center.getY() + half + 1.02;
		double z1 = center.getZ() + half + 1.02;

		int steps = (int) Math.round(x1 - x0);

		for (int i = 0; i <= steps; i++) {
			double t = i;

			point(level, x0 + t, y0, z0);
			point(level, x0 + t, y1, z0);
			point(level, x0 + t, y0, z1);
			point(level, x0 + t, y1, z1);

			point(level, x0, y0 + t, z0);
			point(level, x1, y0 + t, z0);
			point(level, x0, y0 + t, z1);
			point(level, x1, y0 + t, z1);

			point(level, x0, y0, z0 + t);
			point(level, x1, y0, z0 + t);
			point(level, x0, y1, z0 + t);
			point(level, x1, y1, z0 + t);
		}
	}

	/** Одна частица в точке. */
	private static void point(ServerLevel level, double x, double y, double z) {
		level.sendParticles(ParticleTypes.END_ROD, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
	}

	/** Сколько миллисекунд осталось до готовности кисти. */
	public static long cooldownLeft(ServerPlayer player) {
		Long until = COOLDOWN.get(player.getUUID());
		if (until == null) {
			return 0L;
		}
		return Math.max(0L, until - System.currentTimeMillis());
	}

	/** Очищает все запросы и перезарядки (например, при остановке сервера). */
	public static void reset() {
		PENDING.clear();
		COOLDOWN.clear();
	}

	/** Блоки, которые кистью удалять нельзя. */
	public static boolean isProtected(BlockState state) {
		return StrikeConfig.protectedBlocks.contains(
				BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString());
	}
}
