/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;

// euh plus tard ?
@AllArgsConstructor
public enum Color {

	SUCCESS(ChatColor.GREEN),
	WARNING(ChatColor.GOLD),
	ERROR(ChatColor.RED),

	INFO(ChatColor.DARK_AQUA),
	SECONDARY(ChatColor.DARK_GREEN);

	private ChatColor color;
}
