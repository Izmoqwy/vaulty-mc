/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaultyhub.commands;

import eu.izmoqwy.vaulty.VaultyCommand;
import eu.izmoqwy.vaulty.VaultyCore;
import eu.izmoqwy.vaulty.oss.OSS;
import eu.izmoqwy.vaultyhub.VaultyHub;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LobbyCommand extends VaultyCommand {
	public LobbyCommand() {
		super("lobby", true);
	}

	@Override
	public void executeCommand(CommandSender commandSender, String[] args) {
		Player player = (Player) commandSender;
		if (!OSS.getServer(player).isDefault()) {
			OSS.send(player, OSS.getMainServer());
		}

		VaultyHub.teleport(player);
		VaultyHub.giveInventory(player);
		player.sendMessage(VaultyCore.PREFIX + "§7Vous avez été téléporté au spawn du lobby.");
	}
}
