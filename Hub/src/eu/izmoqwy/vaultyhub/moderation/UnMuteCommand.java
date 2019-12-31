/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaultyhub.moderation;

import eu.izmoqwy.vaulty.rank.Rank;
import eu.izmoqwy.vaulty.rank.VaultyRank;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class UnMuteCommand extends ModerationCommand {
	public UnMuteCommand() {
		super("unmute", Rank.HELPER);
	}

	@Override
	public void execute(CommandSender commandSender, boolean console, String[] args) {
		OfflinePlayer target = getOfflineTarget(args, 0, "Joueur à qui rendre la parole");
		checkArgument(console || VaultyRank.get(target).isBelow(VaultyRank.get((OfflinePlayer) commandSender)), "Ce joueur est trop haut gradé.");
		Moderation.getModeration().mute(commandSender, target, "unmute", System.currentTimeMillis());
		send(commandSender, "§aJoueur dé-mute avec succès.");
	}
}
