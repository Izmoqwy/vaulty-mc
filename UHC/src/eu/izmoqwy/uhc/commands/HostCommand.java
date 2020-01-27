/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.commands;

import eu.izmoqwy.uhc.VaultyUHC;
import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.uhc.game.GameState;
import eu.izmoqwy.uhc.game.obj.Whitelist;
import eu.izmoqwy.vaulty.VaultyCommand;
import eu.izmoqwy.vaulty.VaultyCore;
import eu.izmoqwy.vaulty.oss.OSS;
import eu.izmoqwy.vaulty.rank.Rank;
import eu.izmoqwy.vaulty.rank.VaultyRank;
import eu.izmoqwy.vaulty.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class HostCommand extends VaultyCommand {
	public HostCommand() {
		super("host", true);
		setPrefix(VaultyUHC.PREFIX);

		helpCommands.put("say", "Envoyer un message d'annonce");
		helpCommands.put("kick", "Expulser un joueur de la partie");
		helpCommands.put("heal", "Soigner un joueur");
		helpCommands.put("kill", "Tuer un joueur");
	}

	@Override
	public void executeCommand(CommandSender commandSender, String[] args) {
		Player player = (Player) commandSender;
		GameManager gameManager = GameManager.get;

		checkArgument(gameManager.isCurrentHost(player) || VaultyRank.get(player).isEqualsOrAbove(Rank.MODERATOR), "Vous n'avez pas besoin de cette commande.");

		if (match(args, 0, "heal")) {
			checkArgument(gameManager.getGameState() == GameState.PLAYING, "La partie doit être en cours pour faire ça.");
			if (args.length > 1 && args[1].equalsIgnoreCase("all")) {
				gameManager.getCurrentGame().getOnlinePlayers().forEach(online -> online.setHealth(online.getMaxHealth()));
				send(player, "§aTous les joueurs ont été soignés.");
				return;
			}
			Player target = getTarget(args, 1, "Joueur à soigner");
			target.setHealth(target.getMaxHealth());
			send(player, "§aLe joueur a été soigné.");
			return;
		}
		else if (match(args, 0, "kill")) {
			checkArgument(gameManager.getGameState() == GameState.PLAYING, "La partie doit être en cours pour faire ça.");
			Player target = getTarget(args, 1, "Joueur à tuer");
			target.damage(target.getMaxHealth() + .1f);
			send(player, "§aLe joueur a été tué.");
			return;
		}
		else if (match(args, 0, "kick")) {
			Player target = getTarget(args, 1, "Joueur à expulser");
			checkArgument(!gameManager.isCurrentHost(target) && VaultyRank.get(target).isBelow(Rank.MODERATOR), "Impossible d'expulser ce joueur");

			OSS.send(target, OSS.getMainServer());
			target.sendMessage(VaultyCore.PREFIX + "§6Vous avez été expulsé de la partie.");
			gameManager.getKicked().add(target.getUniqueId());
			send(player, "§aLe joueur a été expulsé.");
			return;
		}

		checkArgument(gameManager.isCurrentHost(player), "Vos pouvoirs de modérateur se limitent au heal, au kill et au kick pour la commande host.");

		if (match(args, 0, "bc", "say")) {
			missingArg(args, 1, "Message à envoyer");
			String message = "§b§lMessage ‖ §f" + TextUtil.getFinalArg(args, 1);
			if (gameManager.getCurrentGame() != null) {
				gameManager.getCurrentGame().broadcast(null, false);
				gameManager.getCurrentGame().broadcast(message, false);
				gameManager.getCurrentGame().broadcast(null, false);
			}
			else if (gameManager.getWaitingRoom() != null) {
				gameManager.getWaitingRoom().broadcast(" ", false);
				gameManager.getWaitingRoom().broadcast(message, false);
				gameManager.getWaitingRoom().broadcast(" ", false);
			}
		}
		else if (match(args, 0, "whitelist", "wl")) {
			Whitelist whitelist = gameManager.getWhitelist();
			if (match(args, 1, "add")) {
				OfflinePlayer target = getOfflineTarget(args, 2, "Joueur à ajouter");
				checkArgument(!whitelist.getAllowed().contains(target.getUniqueId()), "§7Ce joueur est déjà dans la whitelist.");
				whitelist.getAllowed().add(target.getUniqueId());
				send(player, "§dLe joueur §5" + target.getName() + "§d a ajouté à la whitelist.");
			}
			else if (match(args, 1, "remove")) {
				OfflinePlayer target = getOfflineTarget(args, 2, "Joueur à supprimer");
				checkArgument(!target.getUniqueId().equals(player.getUniqueId()), "Vous ne pouvez pas vous retirer de la whitelist.");
				checkArgument(whitelist.getAllowed().remove(target.getUniqueId()), "Ce joueur n'est pas dans la whitelist.");
				send(player, "§dLe joueur §5" + target.getName() + "§d a supprimé de la whitelist.");
			}
			else if (match(args, 1, "list")) {
				send(player, "§dJoueurs dans la whitelist: §5" + whitelist.getAllowed().stream().map(uuid -> Bukkit.getOfflinePlayer(uuid).getName()).collect(Collectors.joining("§d, §5")));
			}
			else if (match(args, 1, "toggle")) {
				whitelist.setEnabled(!whitelist.isEnabled());
				if (whitelist.isEnabled())
					send(player, "§dWhitelist activée.");
				else
					send(player, "§5Whitelist désactivée.");

				if (gameManager.getWaitingRoom() != null)
					gameManager.getWaitingRoom().updatePlayerCount();
			}
			else {
				send(player, "§c/host whitelist <add|remove|list|toggle>");
			}
		}
		else if (match(args, 0, "help", "?")) {
			sendHelp(player);
		}
		else {
			send(player, "§cArgument manquant on incorrect. Faîtes '/host help' pour plus d'informations.");
		}
	}

	@Override
	protected Player getTarget(String[] args, int index, String needed) {
		Player target = super.getTarget(args, index, needed);
		GameManager gameManager = GameManager.get;
		checkArgument(
				(gameManager.getCurrentGame() != null && gameManager.getCurrentGame().getOnlinePlayers().contains(target))
						|| (gameManager.getWaitingRoom() != null && gameManager.getWaitingRoom().getAllPlayers().contains(target)),
				"Ce joueur n'est pas dans votre partie.");
		return target;
	}
}
