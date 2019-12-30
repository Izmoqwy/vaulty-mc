/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty.rank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
@AllArgsConstructor
public enum Rank {
	OWNER(810, "Fondateur", ChatColor.DARK_RED),
	ADMIN(113, "Admin", ChatColor.RED),
	DEVELOPER(771, "Développeur", ChatColor.YELLOW),
	MODERATOR(685, "Modérateur", ChatColor.DARK_GREEN),
	HELPER(351, "Helper", ChatColor.AQUA),
	HOST(790, "Host", ChatColor.DARK_PURPLE),
	DEFAULT(671, "Joueur", ChatColor.GRAY);

	private int id;
	private String name;
	private ChatColor color;

	public String getFullName() {
		return color + (name.length() <= 13 ? name : name.substring(0, 13)) + " ";
	}

	public boolean isEqualsOrAbove(Rank other) {
		return ordinal() <= other.ordinal();
	}

	public boolean isBelow(Rank other) {
		return ordinal() > other.ordinal();
	}

	public static Rank getById(int id) {
		for (Rank rank : Rank.values()) {
			if (rank.getId() == id)
				return rank;
		}
		return DEFAULT;
	}
}
