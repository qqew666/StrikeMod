package dev.strike;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.PermissionSet;

/**
 * Проверка прав в новой системе разрешений (Minecraft 1.21.9+).
 */
public final class StrikePermissions {

	private StrikePermissions() {
	}

	/** Есть ли у источника права гейм-мастера (уровень оператора 2 и выше). */
	public static boolean isOperator(CommandSourceStack source) {
		PermissionSet permissions = source.permissions();

		// Права оператора: гейм-мастер (2), админ (3), владелец (4)
		return LevelBasedPermissionSet.GAMEMASTER.equals(permissions)
				|| LevelBasedPermissionSet.ADMIN.equals(permissions)
				|| LevelBasedPermissionSet.OWNER.equals(permissions)
				|| LevelBasedPermissionSet.ALL.equals(permissions);
	}
}
