package dev.strike;

import dev.strike.common.StrikeConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
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

	/** Гайд уже поставлен в этом запуске сервера. */
	private static boolean guidePlaced;

	@Override
	public void onInitialize() {
		StrikeConfig.load(FabricLoader.getInstance().getConfigDir());
		StrikeEvents.register();
		StrikeCommands.register();

		ServerLifecycleEvents.SERVER_STARTED.register(started -> {
			server = started;
			guidePlaced = false;
		});
		ServerLifecycleEvents.SERVER_STOPPED.register(stopped -> server = null);

		// При входе игрока: ставим сундук с книгой-гайдом и подсказываем команды
		ServerPlayConnectionEvents.JOIN.register((handler, sender, started) -> {
			ServerPlayer player = handler.getPlayer();

			if (!guidePlaced && StrikeWorld.placeGuideChest((ServerLevel) player.level(), player)) {
				guidePlaced = true;
			}

			player.sendSystemMessage(Component.literal(
					"§a§lStrike §7— мод активен. Введите §e/strike§7, чтобы получить кисть. "
							+ "Книга-гайд лежит в сундуке рядом с вами."), false);
		});

		LOGGER.info("Strike mod initialized (Minecraft 26.3)");
	}

	/** Мод активен ли сейчас (в одиночке — только если не задан multiplayerOnly). */
	public static boolean isMultiplayer() {
		MinecraftServer current = server;
		if (current == null) {
			return false;
		}
		return !(StrikeConfig.multiplayerOnly && current.isSingleplayer());
	}
}
