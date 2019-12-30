/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaultyhub.moderation;

import eu.izmoqwy.vaulty.rank.Rank;
import eu.izmoqwy.vaulty.rank.VaultyRank;
import eu.izmoqwy.vaulty.utils.TextUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand extends ModerationCommand {
	public KickCommand() {
		super("kick", Rank.HELPER);
	}

	@Override
	public void execute(CommandSender commandSender, boolean console, String[] args) {
		Player target = getTarget(args, 0, "Joueur à expulser");
		checkArgument(console || VaultyRank.get(target).isBelow(VaultyRank.get((OfflinePlayer) commandSender)), "Ce joueur est trop haut gradé.");

		target.kickPlayer("§cVous avez été expulsé.\n\n§fRaison: " + (args.length > 1 ? "§b" + TextUtil.getFinalArg(args, 1) : "§cNon spécifié"));
		send(commandSender, "§aJoueur expulsé avec succès.");
	}
}
