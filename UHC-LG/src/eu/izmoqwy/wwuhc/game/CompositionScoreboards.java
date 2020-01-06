/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.game;

import com.google.common.collect.Maps;
import eu.izmoqwy.uhc.game.tasks.GameLoop;
import eu.izmoqwy.vaulty.scoreboard.VaultyScoreboard;
import eu.izmoqwy.wwuhc.role.Role;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CompositionScoreboards {

	private Map<VaultyScoreboard, Map<Integer, Class<? extends Role>>> scoreboards;

	protected CompositionScoreboards(Collection<Role> roleList) {
		scoreboards = Maps.newHashMap();

		List<Role> distinctRoles = roleList.stream().distinct().collect(Collectors.toList());
		int neededScoreboards = (int) Math.ceil(distinctRoles.size() / 15d);
		for (int i = 0; i < neededScoreboards; i++) {
			VaultyScoreboard scoreboard = new VaultyScoreboard("composition-" + i, "Composition " + (i + 1) + "/" + neededScoreboards);
			Map<Integer, Class<? extends Role>> roles = Maps.newHashMap();
			for (int j = 0; j < Math.min(15, distinctRoles.size() - (i * 15)); j++) {
				Role role = distinctRoles.get(j + i * 15).getRootRole();
				roles.put(j, role.getClass());
				scoreboard.setScore(j, role.getName(), count(roleList, role.getClass()));
			}
			scoreboards.put(scoreboard, roles);
		}
	}

	public void applyOnGameLoop(GameLoop gameLoop) {
		gameLoop.getAlternateScoreboard().addAll(scoreboards.keySet());
	}

	public void update(Collection<Role> roleList) {
		if (scoreboards == null)
			return;

		scoreboards.forEach(((scoreboard, roles) -> roles.forEach((line, role) -> {
			if (scoreboard == null)
				return;

			int count = count(roleList, role);
			if (count == 0 && scoreboard.getLine(line) != null && !scoreboard.getLine(line).startsWith("§7"))
				scoreboard.setScore(line, "§7§m" + scoreboard.getLine(line), count);
			scoreboard.setScore(line, count);
		})));
	}

	private int count(Collection<Role> roleList, Class<? extends Role> roleClass) {
		return (int) roleList.stream().filter(role -> role.getIDKHowToNameThis().getClass().equals(roleClass)).count();
	}

}
