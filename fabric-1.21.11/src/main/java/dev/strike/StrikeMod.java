package dev.strike;

import dev.strike.common.StrikeConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Strike — кисть с шансом удаления блоков. Работает только на мультиплеере.
 */
public class StrikeMod implements ModInitializer {

	public static final String MOD_ID = "strike";
	public static final Logger LOGGER = LoggerFactory.getLogger("Strike");

	@Override
	public void onInitialize() {
		StrikeConfig.load(FabricLoader.getInstance().getConfigDir());
		StrikeEvents.register();
		StrikeCommands.register();

		// В начале игры ставим сундук с книгой-гайдом рядом со спавном
		ServerLifecycleEvents.SERVER_STARTED.register(StrikeWorld::placeGuideChest);

		// Подсказка при заходе на сервер
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayer player = handler.getPlayer();
			player.displayClientMessage(Component.literal(
					"§a§lStrike §7— мод активен. Введите §e/strike§7, чтобы получить кисть. "
							+ "Книга-гайд лежит в сундуке рядом со спавном."), false);
		});

		LOGGER.info("Strike mod initialized (Minecraft 1.21.11)");
	}

	/** Мод активен ли для этого игрока (в одиночной игре — нет). */
	public static boolean isMultiplayer(ServerPlayer player) {
		MinecraftServer server = player.getServer();
		if (server == null) {
			return false;
		}
		return !(StrikeConfig.multiplayerOnly && server.isSingleplayer());
	}
}
