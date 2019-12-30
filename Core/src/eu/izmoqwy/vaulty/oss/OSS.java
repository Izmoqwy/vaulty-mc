/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty.oss;

import com.google.common.collect.Maps;
import eu.izmoqwy.vaulty.VaultyCore;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OSS {

	@Getter
	private static final ServerSlice mainServer = new ServerSlice("Main");

	@Getter
	private static Map<Player, ServerSlice> players = Maps.newHashMap();

	public static ServerSlice getServer(Player player) {
		return players.getOrDefault(player, mainServer);
	}

	public static void send(Player player, ServerSlice server) {
		ServerSlice currentServer = getServer(player);
		if (currentServer != server) {
			if (currentServer.getOnlinePlayers().contains(player)) {
				currentServer.onQuit(player, false);
				players.put(player, server);
				server.onJoin(player, false);
				if (VaultyCore.DEBUG)
					dump();
			}
		}
	}

	public static List<ServerSlice> getServers() {
		return players.values().stream().distinct().collect(Collectors.toList());
	}

	protected static void dump() {
		List<ServerSlice> servers = getServers();

		System.out.println("------------ État des faux serveurs ------------");
		if (players.size() != Bukkit.getOnlinePlayers().size())
			System.err.println("Joueurs: " + players.size() + "/" + Bukkit.getOnlinePlayers().size());
		else
			System.out.println("Joueurs: " + players.size());
		System.out.println("Serveurs (" + servers.size() + "):");
		servers.forEach(server -> {
			System.out.println("» " + server.getName() + (server.isDefault() ? " (par défaut):" : ":"));
			int playersSize = server.getOnlinePlayers().size();
			dumpIndent(1, "Joueurs: " + playersSize);
			server.getOnlinePlayers().forEach(player -> {
				dumpIndent(1, "> " + player.getName() + ":");
				int showed = 0, showedTo = 0;
				int hidden = 0, hiddenFrom = 0;
				for (Player other : Bukkit.getOnlinePlayers()) {
					if (player.equals(other))
						continue;

					if (player.canSee(other))
						showed++;
					else
						hidden++;
					if (other.canSee(player))
						showedTo++;
					else
						hiddenFrom++;
				}
				if (showed == showedTo)
					dumpIndent(2, "Visibles: " + showed);
				else {
					dumpIndent(2, "Visibles: " + showed + " | " + showedTo, true);
				}

				if (hidden == hiddenFrom)
					dumpIndent(2, "Cachés: " + hidden);
				else {
					dumpIndent(2, "Cachés: " + hidden + " | " + hiddenFrom, true);
				}
			});
		});
		System.out.println("------------------------------------------------");
	}

	private static void dumpIndent(int size, String s, boolean error) {
		String finalMessage = new String(new char[size]).replace("\0", " ") + s;
		if (error)
			System.err.println(finalMessage);
		else
			System.out.println(finalMessage);
	}

	private static void dumpIndent(int size, String s) {
		dumpIndent(size, s, false);
	}

}
