/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaultyhub.commands;

import eu.izmoqwy.vaulty.VaultyCommand;
import eu.izmoqwy.vaulty.oss.OSS;
import eu.izmoqwy.vaulty.rank.Rank;
import eu.izmoqwy.vaulty.rank.VaultyRank;
import eu.izmoqwy.vaultyhub.HubRankDisplayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class RankCommand extends VaultyCommand {
	public RankCommand() {
		super("rank", false);

		helpCommands.put("set <joueur> <grade>", "Définir le grade d'un joueur");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void executeCommand(CommandSender commandSender, String[] args) {
		Rank senderRank = commandSender instanceof Player ? VaultyRank.get((OfflinePlayer) commandSender) : null;
		if (senderRank != null && !senderRank.isEqualsOrAbove(Rank.DEVELOPER)) {
			send(commandSender, "§cVous n'êtes pas autorisé a utiliser cette commande.");
			return;
		}

		if (match(args, 0, "set")) {
			if (args.length <= 2) {
				send(commandSender, "§cVeuillez préciser un joueur et son nouveau rôle !");
				return;
			}

			OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
			if (target.hasPlayedBefore() || target.isOnline()) {
				String rankName = args[2];
				for (Rank rank : Rank.values()) {
					if (rank.getName().equalsIgnoreCase(rankName)) {
						if (senderRank == null || rank.isBelow(senderRank)) {
							if (senderRank != null && VaultyRank.get(target).isEqualsOrAbove(senderRank)) {
								send(commandSender, "§cCe joueur possède un grade supérieur ou égal au votre !");
								return;
							}

							try {
								Rank previous = VaultyRank.get(target);
								VaultyRank.set(target, rank);
								if (target.isOnline() && OSS.getServer(target.getPlayer()).isDefault()) {
									HubRankDisplayer.get.updatePlayer(target.getPlayer(), previous);
								}
								send(commandSender, "§aGrade pour §6§l" + target.getName() + "§a défini à " + rank.getFullName() + "§a avec succès.");
							}
							catch (IOException e) {
								e.printStackTrace();
								send(commandSender, "§4Impossible de définir le grade, regardez la console.");
							}
						}
						else {
							send(commandSender, "§cVous ne pouvez pas attribuer un grade supérieur ou égal au votre.");
						}
						return;
					}
				}
				send(commandSender, "§cCe grade n'éxiste pas !");
			}
			else {
				send(commandSender, "§cCe joueur n'éxiste pas !");
			}
		}
		else if (match(args, 0, "help", "?")) {
			sendHelp(commandSender);
		}
		else {
			send(commandSender, "§cArgument manquant ou incorrect, faîtes '/rank help' pour plus d'informations.");
		}

	}
}
