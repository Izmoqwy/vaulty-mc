/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaultyhub.commands;

import eu.izmoqwy.vaulty.VaultyCommand;
import eu.izmoqwy.vaulty.rank.Rank;
import eu.izmoqwy.vaulty.rank.VaultyRank;
import eu.izmoqwy.vaulty.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpOpCommand extends VaultyCommand {
	public HelpOpCommand() {
		super("helpop", true);
	}

	@Override
	public void executeCommand(CommandSender commandSender, String[] args) {
		Player player = (Player) commandSender;

		Rank rank = VaultyRank.get(player);
		checkArgument(rank.isBelow(Rank.HELPER), "Vous faîtes parti du staff lui-même.");

		Bukkit.getLogger().info("[Aide staff] " + player.getName() + ": " + TextUtil.getFinalArg(args, 0));
		String message = "§7[Aide staff] " + rank.getFullName() + player.getName() + "§8: §f" + TextUtil.getFinalArg(args, 0);
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (VaultyRank.get(online).isBelow(Rank.HELPER))
				continue;
			online.sendMessage(message);
		}
		player.sendMessage(message);
	}
}
