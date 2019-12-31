/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaultyhub;

import eu.izmoqwy.vaulty.builder.ItemBuilder;
import eu.izmoqwy.vaulty.nms.NMS;
import eu.izmoqwy.vaulty.oss.OSS;
import eu.izmoqwy.vaulty.oss.ServerListener;
import eu.izmoqwy.vaulty.utils.PlayerUtil;
import eu.izmoqwy.vaulty.utils.ServerUtil;
import eu.izmoqwy.vaultyhub.commands.HelpOpCommand;
import eu.izmoqwy.vaultyhub.commands.LobbyCommand;
import eu.izmoqwy.vaultyhub.commands.RankCommand;
import eu.izmoqwy.vaultyhub.moderation.*;
import eu.izmoqwy.vaultyhub.world.VoidWorldGenerator;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class VaultyHub extends JavaPlugin {

	private static Location spawn;

	@Override
	public void onEnable() {
		World world = Bukkit.createWorld(new WorldCreator("Hub")
				.generator(new VoidWorldGenerator()).generateStructures(false)
				.type(WorldType.FLAT).environment(World.Environment.NORMAL)
		);
		spawn = new Location(world, -8.5, 67.1, -0.5);

		ServerUtil.registerListeners(this, new HubListener());
		ServerUtil.registerCommands(new RankCommand(), new LobbyCommand(), new HelpOpCommand());
		ServerUtil.registerCommands(new KickCommand(), new BanCommand(), new UnBanCommand(), new MuteCommand(), new UnMuteCommand());

		for (Player player : Bukkit.getOnlinePlayers()) {
			teleport(player);
			giveInventory(player);
		}

		OSS.getMainServer().setListener(new ServerListener() {
			@Override
			public void onJoin(Player player) {
				if (player.getWorld() != world) {
					teleport(player);
					giveInventory(player);
				}
				HubRankDisplayer.get.addPlayer(player);
			}

			@Override
			public void onQuit(Player player) {
				HubRankDisplayer.get.removePlayer(player);
			}
		});
		OSS.getMainServer().getOnlinePlayers().forEach(HubRankDisplayer.get::addPlayer);
		updateTabList(false);
	}

	@Override
	public void onDisable() {
		OSS.getMainServer().getOnlinePlayers().forEach(HubRankDisplayer.get::removePlayer);
	}

	public static void updateTabList(boolean disconnect) {
		String header = "\n§a§lVaulty§e\ndiscord.gg/YVTgbhm\n",
				footer = "\n§fJoueurs en ligne: §b" + (disconnect ? Bukkit.getOnlinePlayers().size() - 1 : Bukkit.getOnlinePlayers().size()) + "\n";
		NMS.packets.sendTabList(header, footer);
	}

	protected static final ItemStack icon_currentGames = new ItemBuilder(Material.SLIME_BALL).name("§6§lProchainement").appendLore("§7Arrive prochainement").toItemStack(),
			icon_join = new ItemBuilder(Material.ENDER_PORTAL_FRAME).name("§e§lRejoindre une partie").appendLore("§7Rejoindre une partie en attente").quickEnchant().toItemStack(),
			icon_host = new ItemBuilder(Material.NETHER_STAR).name("§6§lLancer une partie").appendLore("§7Lancer une partie (HOST)").toItemStack();

	public static void giveInventory(Player player) {
		player.setGameMode(GameMode.ADVENTURE);
		PlayerUtil.reset(player);

		PlayerInventory inventory = player.getInventory();
		inventory.setItem(3, icon_currentGames);
		inventory.setItem(4, icon_join);
		inventory.setItem(5, icon_host);
	}

	public static void teleport(Player player) {
		player.teleport(spawn);
	}

}
