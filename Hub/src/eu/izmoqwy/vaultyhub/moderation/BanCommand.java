/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaultyhub.moderation;

import eu.izmoqwy.vaulty.rank.Rank;
import eu.izmoqwy.vaulty.rank.VaultyRank;
import eu.izmoqwy.vaulty.utils.TextUtil;
import eu.izmoqwy.vaulty.utils.TimeUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.text.ParseException;

public class BanCommand extends ModerationCommand {
	public BanCommand() {
		super("ban", Rank.MODERATOR);
	}

	@Override
	public void execute(CommandSender commandSender, boolean console, String[] args) {
		OfflinePlayer target = getOfflineTarget(args, 0, "Joueur à bannir");
		checkArgument(console || VaultyRank.get(target).isBelow(VaultyRank.get((OfflinePlayer) commandSender)), "Ce joueur est trop haut gradé.");

		missingArg(args, 1, "Temps du bannissement");

		long millis;
		try {
			millis = TimeUtil.millisTime(args[1]);
		}
		catch (ParseException e) {
			send(commandSender, "§cDurée de temps invalide !");
			return;
		}

		if (millis == -1)
			send(commandSender, "§cUnité de temps invalide !");

		millis += System.currentTimeMillis();

		Moderation.getModeration().ban(commandSender, target, (args.length > 2 ? TextUtil.getFinalArg(args, 2) : null), millis);

		if (target.isOnline()) {
			target.getPlayer().kickPlayer(Moderation.getModeration().getBanMessage(target));
		}
		send(commandSender, "§aJoueur banni avec succès.");
	}
}
