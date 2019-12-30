/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty.oss;

import eu.izmoqwy.vaulty.VaultyCore;
import eu.izmoqwy.vaulty.rank.Rank;
import eu.izmoqwy.vaulty.rank.VaultyRank;
import eu.izmoqwy.vaulty.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;

import static eu.izmoqwy.vaulty.oss.OSS.*;

public class OSSBukkitListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		ServerSlice playerServer = getServer(player);
		playerServer.onJoin(player, true);
		getPlayers().put(player, playerServer);

		Player[] toHide = getPlayers().entrySet().stream().filter(entry -> entry.getKey() != player && entry.getValue() != playerServer).map(Map.Entry::getKey).toArray(Player[]::new);
		PlayerUtil.hidePlayersMutually(player, toHide);
		if (VaultyCore.DEBUG)
			dump();
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		getServer(player).onQuit(player, true);
		getPlayers().remove(player);
		if (VaultyCore.DEBUG)
			Bukkit.getScheduler().runTaskLater(VaultyCore.getInstance(), OSS::dump, 1);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onAsyncChat(AsyncPlayerChatEvent event) {
		event.setCancelled(true);

		Rank rank = VaultyRank.get(event.getPlayer());
		String formatted = rank.getFullName() + event.getPlayer().getName() + " §8➾ " + rank.getColor() + event.getMessage();
		getServer(event.getPlayer()).getOnlinePlayers().forEach(player -> player.sendMessage(formatted));
		VaultyCore.getInstance().getLogger().info("[Chat] (" + getServer(event.getPlayer()).getName() + ") " + event.getPlayer().getName() + ": " + event.getMessage());
	}

}
