/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty.utils;

import eu.izmoqwy.vaulty.VaultyCommand;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ServerUtil {

	/*
	Plugin
	 */

	public static void registerListeners(JavaPlugin from, Listener... listeners) {
		for (Listener listener : listeners) {
			Bukkit.getPluginManager().registerEvents(listener, from);
		}
	}

	public static void registerCommands(VaultyCommand... commands) {
		for (VaultyCommand command : commands) {
			PluginCommand bukkitCommand = Bukkit.getPluginCommand(command.getName());
			if (bukkitCommand != null) {
				bukkitCommand.setExecutor(command);
			}
			else {
				Bukkit.getLogger().warning("Vaulty: La commande « " + command.getName() + " » n'est pas enregistrée correctement !");
			}
		}
	}

	/*
	Monde
	 */
	public static void removeWorld(String worldName) throws IOException {
		World world = Bukkit.getWorld(worldName);
		if (world != null) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.getWorld().equals(world)) {
					player.kickPlayer("§cLe monde dans lequel vous êtes va être supprimé.");
				}
			}
			Bukkit.unloadWorld(world, true);
		}
		File worldDir = new File(Bukkit.getWorldContainer(), worldName);
		if (worldDir.exists() && worldDir.isDirectory()) {
			FileUtils.deleteDirectory(worldDir);
		}
	}

}
