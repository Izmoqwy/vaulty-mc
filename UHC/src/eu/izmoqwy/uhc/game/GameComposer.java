/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.game;

import com.google.common.collect.Lists;
import eu.izmoqwy.uhc.config.GUIConfigurable;
import eu.izmoqwy.uhc.config.GUISetting;
import eu.izmoqwy.uhc.scenario.GameType;
import eu.izmoqwy.uhc.scenario.Scenario;
import eu.izmoqwy.uhc.scenario.all.ScenarioCutClean;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.UUID;

@Getter
@Setter
public class GameComposer implements GUIConfigurable {

	public static final GameComposer defaultComposer;

	static {
		defaultComposer = new GameComposer();
		defaultComposer.gameTitle = "§e§lUHC";
		defaultComposer.startingInventoryContents = new ItemStack[]{new ItemStack(Material.COOKED_BEEF, 16)};

		defaultComposer.maxPlayers = 20;
		defaultComposer.chatMessages = true;
		defaultComposer.netherEnabled = true;
		defaultComposer.endEnabled = false;
		defaultComposer.hideCoordinates = false;
		defaultComposer.eternalDay = true;

		defaultComposer.invincibilityStopsAt = 30;
		defaultComposer.pvpStartsAt = 60 * 15;

		defaultComposer.borderInitialSize = 750;
		defaultComposer.borderFinalSize = 50;
		defaultComposer.borderShrinksAt = 60 * 30;
		defaultComposer.borderSpeed = 1.5f;

		defaultComposer.scenarios = Lists.newArrayList(
				new ScenarioCutClean()
		);
	}

	public GameComposer() {
	}

	public GameComposer(GameComposer other) {
		gameHost = other.gameHost;
		gameTitle = other.gameTitle;
		gameType = other.gameType;

		maxPlayers = other.maxPlayers;
		chatMessages = other.chatMessages;
		netherEnabled = other.netherEnabled;
		endEnabled = other.endEnabled;
		hideCoordinates = other.hideCoordinates;
		eternalDay = other.eternalDay;
		startingInventoryContents = other.startingInventoryContents;
		startingInventoryArmorContents = other.startingInventoryArmorContents;

		invincibilityStopsAt = other.invincibilityStopsAt;
		pvpStartsAt = other.pvpStartsAt;

		borderInitialSize = other.borderInitialSize;
		borderFinalSize = other.borderFinalSize;
		borderShrinksAt = other.borderShrinksAt;
		borderSpeed = other.borderSpeed;

		scenarios = other.scenarios;
		composing = other.composing;
	}

	protected boolean composing = true;

	private GameType gameType;
	private UUID gameHost;

	private String gameTitle;
	private ItemStack[] startingInventoryContents, startingInventoryArmorContents;

	@GUISetting(name = "Joueurs", icon = Material.SKULL_ITEM, iconData = 3,
			min = 5)
	private int maxPlayers;
	@GUISetting(name = "Messages dans le chat", icon = Material.PAPER)
	private boolean chatMessages;
	@GUISetting(name = "Cacher les coordonnées", icon = Material.EYE_OF_ENDER)
	private boolean hideCoordinates;
	@GUISetting(name = "Jour éternel", icon = Material.GLOWSTONE_DUST)
	private boolean eternalDay;

	@GUISetting(group = "world", name = "Nether", icon = Material.NETHERRACK)
	private boolean netherEnabled;
	@GUISetting(group = "world", name = "End", icon = Material.ENDER_STONE)
	private boolean endEnabled;

	@GUISetting(group = "timer", name = "Durée sans dégâts", icon = Material.CHAINMAIL_CHESTPLATE, duration = true,
			min = 15, max = 60 * 30, step = 15)
	private int invincibilityStopsAt;
	@GUISetting(group = "timer", name = "Activation du PvP", icon = Material.IRON_SWORD, duration = true,
			min = 30, max = 60 * 120, step = 30)
	private int pvpStartsAt;

	@GUISetting(group = "border", name = "Mouvement de bordure", icon = Material.BEDROCK, duration = true,
			min = 60, max = 60 * 180, step = 60)
	private int borderShrinksAt;
	@GUISetting(group = "border", name = "Taille initiale", icon = Material.EMPTY_MAP,
			min = 50, max = 2175, step = 25)
	private int borderInitialSize;
	@GUISetting(group = "border", name = "Taille finale", icon = Material.EMPTY_MAP,
			min = 50, max = 1250, step = 25)
	private int borderFinalSize;
	@GUISetting(group = "border", name = "Vitesse (blocs/s)", icon = Material.SUGAR,
			minf = 0.2f, maxf = 5, stepf = 0.1f)
	private float borderSpeed;

	@Setter(AccessLevel.NONE)
	private ArrayList<Scenario> scenarios;

	public GameComposer copy() {
		return new GameComposer(this);
	}
}
