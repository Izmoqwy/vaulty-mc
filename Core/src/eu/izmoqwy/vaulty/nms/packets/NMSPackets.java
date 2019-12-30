/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty.nms.packets;

import org.bukkit.entity.Player;

public interface NMSPackets {

	void sendTitle(Player player, String title, String subTitle);

	void sendTimings(Player player, int ticks, int fadeIn, int fadeOut);

	void forceUpdateReducedDebugInfo(Player player, boolean value);

	void sendTabList(String header, String footer);

	void sendActionBar(Player player, String text);

	void openAnvil(Player player, String name);
}
