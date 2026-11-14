package dev.strike;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Ставит в начале игры сундук с книжкой-гайдом рядом с игроком.
 */
public final class StrikeWorld {

	private static final int RADIUS = 4;

	private StrikeWorld() {
	}

	/**
	 * Ставит сундук с гайдом рядом с игроком (если рядом такого сундука ещё нет).
	 */
	public static boolean placeGuideChest(ServerLevel level, ServerPlayer player) {
		if (level == null || player == null) {
			return false;
		}

		BlockPos origin = player.blockPosition();

		// Ищем уже поставленный сундук с гайдом рядом
		if (findExistingChest(level, origin) != null) {
			return true;
		}

		BlockPos spot = findSpot(level, origin);
		if (spot == null) {
			return false;
		}

		level.setBlockAndUpdate(spot, Blocks.CHEST.defaultBlockState());

		BlockEntity blockEntity = level.getBlockEntity(spot);
		if (blockEntity instanceof ChestBlockEntity chest) {
			chest.setItem(0, StrikeBook.createGuideBook());
			chest.setCustomName(net.minecraft.network.chat.Component.literal("§6Сундук гайда Strike"));
		}

		StrikeMod.LOGGER.info("Strike: сундук с гайдом поставлен в {}", spot);
		return true;
	}

	/** Ищет уже поставленный сундук с гайдом. */
	private static BlockPos findExistingChest(ServerLevel level, BlockPos origin) {
		for (int dx = -RADIUS * 2; dx <= RADIUS * 2; dx++) {
			for (int dz = -RADIUS * 2; dz <= RADIUS * 2; dz++) {
				BlockPos pos = new BlockPos(origin.getX() + dx, origin.getY(), origin.getZ() + dz);
				BlockState state = level.getBlockState(pos);
				if (!state.is(Blocks.CHEST)) {
					continue;
				}
				BlockEntity blockEntity = level.getBlockEntity(pos);
				if (blockEntity instanceof ChestBlockEntity chest) {
					for (int slot = 0; slot < chest.getContainerSize(); slot++) {
						ItemStack item = chest.getItem(slot);
						if (StrikeBook.isGuideBook(item)) {
							return pos;
						}
					}
				}
			}
		}
		return null;
	}

	/** Ищет свободное место на поверхности рядом с игроком. */
	private static BlockPos findSpot(ServerLevel level, BlockPos origin) {
		for (int dx = -RADIUS; dx <= RADIUS; dx++) {
			for (int dz = -RADIUS; dz <= RADIUS; dz++) {
				int x = origin.getX() + dx;
				int z = origin.getZ() + dz;
				int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
				BlockPos pos = new BlockPos(x, y, z);
				BlockState here = level.getBlockState(pos);
				BlockState below = level.getBlockState(pos.below());
				if (here.isAir() && !below.isAir() && below.getFluidState().isEmpty()) {
					return pos;
				}
			}
		}
		return null;
	}
}
