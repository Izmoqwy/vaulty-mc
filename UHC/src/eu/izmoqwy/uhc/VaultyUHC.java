/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc;

import eu.izmoqwy.uhc.commands.ComposerCommand;
import eu.izmoqwy.uhc.commands.HostCommand;
import eu.izmoqwy.uhc.commands.PregenConsoleCommand;
import eu.izmoqwy.uhc.commands.UHCCommand;
import eu.izmoqwy.uhc.event.UHCBukkitListener;
import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.uhc.scenario.TypeClassic;
import eu.izmoqwy.uhc.scenario.all.ScenarioCutClean;
import eu.izmoqwy.uhc.scenario.all.ScenarioDiamondLimit;
import eu.izmoqwy.uhc.scenario.all.ScenarioFinalHeal;
import eu.izmoqwy.uhc.scenario.all.ScenarioFriendlyTnt;
import eu.izmoqwy.uhc.world.UHCWorldManager;
import eu.izmoqwy.vaulty.oss.ServerListener;
import eu.izmoqwy.vaulty.oss.ServerSlice;
import eu.izmoqwy.vaulty.utils.ServerUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class VaultyUHC extends JavaPlugin {

	public static final String PREFIX = "§e§lUHC §8» ";

	@Getter
	private static VaultyUHC instance;
	@Getter
	private static ServerSlice uhcServer = new ServerSlice("UHC");

	@Getter
	private UHCBukkitListener uhcBukkitListener;

	@Override
	public void onEnable() {
		if (getServer().getMotd() == null || !getServer().getMotd().equals("vaulty")) {
			Bukkit.shutdown();
			return;
		}

		VaultyUHC.instance = this;

		ServerUtil.registerListeners(this,
				uhcBukkitListener = new UHCBukkitListener()
		);
		uhcServer.setListener(new ServerListener() {
			@Override
			public void onJoin(Player player) {
				uhcBukkitListener.onServerJoin(player);
			}

			@Override
			public void onQuit(Player player) {
				uhcBukkitListener.onServerQuit(player);
			}
		});

		ServerUtil.registerCommands(
				new ComposerCommand(),
				new UHCCommand(),
				new HostCommand(),
				new PregenConsoleCommand()
		);

		GameManager.registerGameType(TypeClassic.class, new TypeClassic());
		GameManager.registerScenarios(
				new ScenarioCutClean(),
				new ScenarioDiamondLimit(),
				new ScenarioFinalHeal(),
				new ScenarioFriendlyTnt());

		//noinspection unused
		UHCWorldManager get = UHCWorldManager.get;
	}

	@Override
	public void onDisable() {
		Bukkit.getOnlinePlayers().forEach(player -> player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard()));
		GameManager.getAvailableGameTypes().clear();
		GameManager.getAvailableScenarios().clear();
		UHCWorldManager.get.getPreGenerator().cancelIfAny();
	}

}
