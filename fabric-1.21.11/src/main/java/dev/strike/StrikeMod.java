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

	/** Текущий сервер (null, если сервера нет). */
	private static MinecraftServer server;

	@Override
	public void onInitialize() {
		StrikeConfig.load(FabricLoader.getInstance().getConfigDir());
		StrikeEvents.register();
		StrikeCommands.register();

		// Запоминаем сервер и ставим сундук с книгой-гайдом рядом со спавном
		ServerLifecycleEvents.SERVER_STARTED.register(started -> {
			server = started;
			StrikeWorld.placeGuideChest(started);
		});
		ServerLifecycleEvents.SERVER_STOPPED.register(stopped -> server = null);

		// Подсказка при заходе на сервер
		ServerPlayConnectionEvents.JOIN.register((handler, sender, started) -> {
			ServerPlayer player = handler.getPlayer();
			player.displayClientMessage(Component.literal(
					"§a§lStrike §7— мод активен. Введите §e/strike§7, чтобы получить кисть. "
							+ "Книга-гайд лежит в сундуке рядом со спавном."), false);
		});

		LOGGER.info("Strike mod initialized (Minecraft 1.21.11)");
	}

	/** Мод активен ли сейчас (в одиночной игре — нет). */
	public static boolean isMultiplayer() {
		MinecraftServer current = server;
		if (current == null) {
			return false;
		}
		return !(StrikeConfig.multiplayerOnly && current.isSingleplayer());
	}
}
