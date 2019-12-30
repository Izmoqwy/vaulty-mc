/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaultyhub.moderation;

import eu.izmoqwy.vaulty.VaultyCommand;
import eu.izmoqwy.vaulty.rank.Rank;
import eu.izmoqwy.vaulty.rank.VaultyRank;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class ModerationCommand extends VaultyCommand {
	private Rank neededRank;

	public ModerationCommand(String name, Rank neededRank) {
		super(name, false);
		this.neededRank = neededRank;
	}

	@Override
	public final void executeCommand(CommandSender commandSender, String[] args) {
		boolean console = !(commandSender instanceof Player);
		checkArgument(console || VaultyRank.get((OfflinePlayer) commandSender).isEqualsOrAbove(neededRank), "Vous n'avez pas la permission requise.");
		execute(commandSender, console, args);
	}

	public abstract void execute(CommandSender commandSender, boolean console, String[] args);
}
