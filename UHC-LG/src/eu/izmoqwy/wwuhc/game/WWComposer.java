/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.game;

import com.google.common.collect.Lists;
import eu.izmoqwy.uhc.config.GUISetting;
import eu.izmoqwy.uhc.game.GameComposer;
import eu.izmoqwy.uhc.scenario.all.ScenarioDiamondLimit;
import eu.izmoqwy.wwuhc.role.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.ArrayList;

@Getter
@Setter
public class WWComposer extends GameComposer {

	public static final WWComposer defaultComposer;

	static {
		defaultComposer = new WWComposer(GameComposer.defaultComposer);
		defaultComposer.setGameTitle("§6§lLG UHC");
		defaultComposer.setChatMessages(false);
		defaultComposer.setHideCoordinates(true);
		defaultComposer.setEternalDay(false);
		defaultComposer.setNetherEnabled(false);
		defaultComposer.setEndEnabled(false);
		defaultComposer.setDayCycleLength(10 * 20);
		defaultComposer.setRoleAnnouncement(60 * 20);
		defaultComposer.setCoupleChoice(60 * 25);
		defaultComposer.setVoteFirstDay(3);
		defaultComposer.setVoteLength(450);

		ScenarioDiamondLimit diamondLimit = new ScenarioDiamondLimit();
		diamondLimit.setLimit(17);
		diamondLimit.setReplace(false);
		defaultComposer.getScenarios().add(diamondLimit);

		defaultComposer.roles = Lists.newArrayList();
	}

	public WWComposer(GameComposer gameComposer) {
		super(gameComposer);
	}

	public WWComposer(WWComposer other) {
		super(other);
		this.dayCycleLength = other.dayCycleLength;
		this.roleAnnouncement = other.roleAnnouncement;
		this.coupleChoice = other.coupleChoice;
		this.voteFirstDay = other.voteFirstDay;
		this.voteLength = other.voteLength;

		this.roles = Lists.newArrayList(other.roles);
	}

	@GUISetting(group = "timer", name = "Cycle jour/nuit", icon = Material.WATCH, duration = true,
			min = 60, max = 60 * 15, step = 30)
	private int dayCycleLength;

	@GUISetting(group = "timer", name = "Annonce des rôles", icon = Material.NAME_TAG, duration = true,
			min = 60, max = 60 * 40, step = 30)
	private int roleAnnouncement;

	@GUISetting(group = "timer", name = "Choix du couple", icon = Material.COMPASS, duration = true,
			min = 0, max = 60 * 60, step = 30)
	private int coupleChoice;

	@GUISetting(name = "Premier jour de vote", icon = Material.PAPER,
			max = 5)
	private int voteFirstDay;

	@GUISetting(group = "timer", name = "Durée du vote", icon = Material.REDSTONE, duration = true,
			min = 30, max = 60 * 10, step = 15)
	private int voteLength;

	@Setter(AccessLevel.NONE)
	private ArrayList<Role> roles;

	@Override
	public WWComposer copy() {
		return new WWComposer(this);
	}
}
