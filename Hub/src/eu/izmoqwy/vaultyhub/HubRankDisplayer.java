/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaultyhub;

import com.google.common.collect.Maps;
import eu.izmoqwy.vaulty.rank.Rank;
import eu.izmoqwy.vaulty.rank.VaultyRank;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Map;

@Getter
public class HubRankDisplayer {

	public static final HubRankDisplayer get = new HubRankDisplayer();

	private Scoreboard scoreboard;
	private Map<Rank, Team> teams = Maps.newHashMap();

	private HubRankDisplayer() {
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		for (Rank rank : Rank.values()) {
			Team team = scoreboard.registerNewTeam(rank.ordinal() + " " + rank.getName());
			team.setPrefix(rank.getFullName());
			teams.put(rank, team);
		}
	}

	public void addPlayer(Player player) {
		Team team = teams.get(VaultyRank.get(player));
		if (!team.hasEntry(player.getName())) {
			team.addEntry(player.getName());
		}
		player.setScoreboard(scoreboard);
	}

	public void updatePlayer(Player player, Rank previous) {
		Team team = teams.get(previous);
		if (team.hasEntry(player.getName())) {
			team.removeEntry(player.getName());
		}
		addPlayer(player);
	}

	public void removePlayer(Player player) {
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		Team team = teams.get(VaultyRank.get(player));
		if (team.hasEntry(player.getName())) {
			team.removeEntry(player.getName());
		}
	}

}
