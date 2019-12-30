/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc;

import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.vaulty.utils.ServerUtil;
import eu.izmoqwy.wwuhc.commands.WerewolfCommand;
import eu.izmoqwy.wwuhc.game.WWGameType;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class WerewolfUHC extends JavaPlugin {

	public static final String PREFIX = "§6§lLG §f§l‖ ";

	@Getter
	private static WerewolfUHC instance;

	@Override
	public void onEnable() {
		instance = this;
		GameManager.registerGameType(new WWGameType());

		ServerUtil.registerCommands(new WerewolfCommand());
	}

	@Override
	public void onDisable() {
		GameManager.unregisterGameType(WWGameType.class);
	}
}
