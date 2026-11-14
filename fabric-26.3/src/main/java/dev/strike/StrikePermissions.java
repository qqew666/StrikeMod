package dev.strike;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.permissions.Permission;

/**
 * Проверка прав в новой системе разрешений (Minecraft 1.21.9+).
 */
public final class StrikePermissions {

	private StrikePermissions() {
	}

	/** Есть ли у источника права оператора (уровень 2). */
	public static boolean isOperator(CommandSourceStack source) {
		return source.permissions().hasPermission(Permission.LEVEL_2);
	}
}
