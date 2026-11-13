package dev.strike;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Ставит в начале игры сундук с книжкой-гайдом рядом со спавном.
 */
public final class StrikeWorld {

	private static final String CHEST_NAME = "§6Сундук гайда Strike";
	private static final int RADIUS = 4;

	private StrikeWorld() {
	}

	/** Ставит сундук с гайдом (если его ещё нет). */
	public static void placeGuideChest(MinecraftServer server) {
		ServerLevel level = server.overworld();
		if (level == null) {
			return;
		}

		BlockPos spawn = level.getSharedSpawnPos();
		if (spawn == null) {
			return;
		}

		if (findExistingChest(level, spawn) != null) {
			return;
		}

		BlockPos spot = findSpot(level, spawn);
		if (spot == null) {
			return;
		}

		level.setBlockAndUpdate(spot, Blocks.CHEST.defaultBlockState());

		BlockEntity blockEntity = level.getBlockEntity(spot);
		if (blockEntity instanceof ChestBlockEntity chest) {
			chest.setItem(0, StrikeBook.createGuideBook());
			chest.setCustomName(Component.literal(CHEST_NAME));
		}

		StrikeMod.LOGGER.info("Strike: сундук с гайдом поставлен в {}", spot);
	}

	/** Ищет уже поставленный сундук с гайдом. */
	private static BlockPos findExistingChest(ServerLevel level, BlockPos spawn) {
		for (int dx = -RADIUS; dx <= RADIUS; dx++) {
			for (int dz = -RADIUS; dz <= RADIUS; dz++) {
				BlockPos pos = new BlockPos(spawn.getX() + dx, spawn.getY(), spawn.getZ() + dz);
				BlockState state = level.getBlockState(pos);
				if (!state.is(Blocks.CHEST)) {
					continue;
				}
				BlockEntity blockEntity = level.getBlockEntity(pos);
				if (blockEntity instanceof ChestBlockEntity chest) {
					Component name = chest.getCustomName();
					if (name != null && name.getString().contains("Strike")) {
						return pos;
					}
				}
			}
		}
		return null;
	}

	/** Ищет свободное место на поверхности рядом со спавном. */
	private static BlockPos findSpot(ServerLevel level, BlockPos spawn) {
		for (int dx = -RADIUS; dx <= RADIUS; dx++) {
			for (int dz = -RADIUS; dz <= RADIUS; dz++) {
				int x = spawn.getX() + dx;
				int z = spawn.getZ() + dz;
				BlockPos pos = new BlockPos(x, 0, z);
				pos = new BlockPos(x, level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos), z);
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
