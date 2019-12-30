/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty.oss;

import org.bukkit.entity.Player;

public interface ServerListener {

	default void onJoin(Player player) {}
	default void onQuit(Player player) {}

}
