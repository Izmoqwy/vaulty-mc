/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty.scoreboard;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class VaultyScoreboard {

	private static Scoreboard emptyScoreboard;

	private final Scoreboard scoreboard;
	private final Objective objective;

	@Getter
	private List<Player> playerList = Lists.newArrayList();
	private Team[] teams = new Team[15];

	public VaultyScoreboard(String scoreboardId, String title) {
		ScoreboardManager scoreboardManager = Bukkit.getServer().getScoreboardManager();
		scoreboard = scoreboardManager.getNewScoreboard();
		objective = scoreboard.registerNewObjective(scoreboardId, "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		if (title != null)
			objective.setDisplayName(title);
	}

	public void reset(int size) {
		teams = new Team[size];
	}

	public void setTitle(String title) {
		objective.setDisplayName(title);
	}

	public void addPlayer(Player player) {
		if (!playerList.contains(player))
			playerList.add(player);
		player.setScoreboard(scoreboard);
	}

	public void removePlayer(Player player) {
		if (playerList.contains(player)) {
			if (emptyScoreboard == null) {
				emptyScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
			}
			player.setScoreboard(emptyScoreboard);
			playerList.remove(player);
		}
	}

	public void setLine(int line, String value) {
		if (line > teams.length || line < 0)
			return;

		if (value.length() > 16) {
			String initialFirstPart = value.substring(0, 16), initialSecondPart = value.substring(16);
			String firstPart = null, secondPart = null;

			int lastColorCharIndex = initialFirstPart.lastIndexOf('§');
			if (lastColorCharIndex + 1 == initialFirstPart.length()) {
				firstPart = initialFirstPart.substring(0, lastColorCharIndex);
				secondPart = '§' + initialSecondPart;
			}
			else if (lastColorCharIndex != -1) {
				ChatColor lastColor = ChatColor.getByChar(initialFirstPart.charAt(lastColorCharIndex + 1));
				if (lastColor != null) {
					secondPart = lastColor + initialSecondPart;
				}
			}
			setLine(line, firstPart != null ? firstPart : initialFirstPart, secondPart != null ? secondPart : initialSecondPart);
		}
		else {
			setLine(line, value, null);
		}
	}

	public void setScore(int line, String value, int score) {
		setLine(line, value);
		setScore(line, score);
	}

	public void setScore(int line, int score) {
		objective.getScore(ChatColor.BLACK + "" + ChatColor.values()[line] + "" + ChatColor.RESET).setScore(score);
	}

	public void setLine(int line, String part1, String part2) {
		if (line > teams.length || line < 0)
			return;

		Team team = getOrCreate(line);
		if (team.getEntries().isEmpty()) {
			String key = ChatColor.BLACK + "" + ChatColor.values()[line] + "" + ChatColor.RESET;
			team.addEntry(key);
			objective.getScore(key).setScore(teams.length - line);
		}

		team.setPrefix(part1 != null ? (part1.length() > 16 ? part1.substring(0, 16) : part1) : "");
		team.setSuffix(part2 != null ? (part2.length() > 16 ? part2.substring(0, 16) : part2) : "");
	}

	public String getLine(int line) {
		if (line >= teams.length || line < 0)
			return null;

		if (teams[line] != null)
			return teams[line].getPrefix() + teams[line].getSuffix();
		return null;
	}

	private Team getOrCreate(int line) {
		if (teams[line] == null)
			teams[line] = scoreboard.registerNewTeam("text-" + scoreboard.getTeams().size());
		return teams[line];
	}

	public void destroy() {
		for (Player player : playerList.toArray(new Player[0])) {
			if (player.getScoreboard() == scoreboard) {
				removePlayer(player);
			}
		}
		playerList = null;
		teams = null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof VaultyScoreboard)) return false;
		VaultyScoreboard that = (VaultyScoreboard) o;
		return Objects.equals(scoreboard, that.scoreboard) &&
				Objects.equals(objective, that.objective) &&
				Objects.equals(playerList, that.playerList) &&
				Arrays.equals(teams, that.teams);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(scoreboard, objective, playerList);
		result = 31 * result + Arrays.hashCode(teams);
		return result;
	}
}
