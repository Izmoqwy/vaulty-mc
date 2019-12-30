/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.commands;

import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.uhc.game.GameState;
import eu.izmoqwy.vaulty.VaultyCommand;
import eu.izmoqwy.vaulty.utils.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ComposerCommand extends VaultyCommand {

	public ComposerCommand() {
		super("composer", true);

		helpCommands.put("check", "Vérifier la composition de la partie");
		helpCommands.put("edit", "Ouvrir les paramètres");
		helpCommands.put("finish", "Valider la composition (lancer la partie)");
		helpCommands.put("cancel", "Annuler le lancement de la partie");
	}

	@Override
	public void executeCommand(CommandSender commandSender, String[] args) {
		Player player = (Player) commandSender;
		GameManager gameManager = GameManager.get;

		if (gameManager.getGameState() == GameState.COMPOSING && gameManager.isCurrentHost(player)) {
			if (match(args, 0, "finish", "start")) {
				if (gameManager.invalidComposition()) {
					send(player, "§cComposition incorrecte, faites '/composer check' pour plus d'informations !");
					return;
				}

				send(player, "§aComposition validée.");
				gameManager.startGame();
			}
			else if (match(args, 0, "check")) {
				List<String> stacktrace = gameManager.getCompositionStackTrace();
				if (stacktrace == null) {
					send(player, "§aLa composition est valide.");
					return;
				}

				send(player, null);
				send(player, "§cVoici les problèmes de votre composition:");
				for (String s : stacktrace) {
					player.sendMessage("§4§l➾ §6" + s);
				}
				send(player, null);
			}
			else if (match(args, 0, "cancel")) {
				send(player, "§cLa partie n'est pas en cours de lancement.");
			}
			else if (match(args, 0, "name", "setname")) {
				String title = TextUtil.getFinalArg(args, 1);
				if (title == null) {
					send(player, "§cArgument manquant: Vous devez entrer un nom pour la partie.");
					return;
				}
				if (title.length() > 24) {
					send(player, "§cLe nom de la partie ne doit pas dépasser 24 caractères, couleurs incluses.");
					return;
				}

				title = ChatColor.translateAlternateColorCodes('&', title);
				if (!title.startsWith("§")) {
					title = "§e§l" + title;
				}
				gameManager.getCurrentComposer().setGameTitle(title);
				gameManager.getWaitingRoom().getScoreboard().setTitle(title);
				send(player, "§aVous avez mis à jour le nom de la partie.");
			}
			else if (match(args, 0, "edit", "settings", "inv")) {
				gameManager.getWaitingRoom().getComposerGUI().open(player);
			}
			else if (match(args, 0, "help", "?")) {
				sendHelp(player);
			}
			else {
				send(player, "§cArgument manquant ou invalide, faites '/composer help' pour obtenir de l'aide.");
			}
		}
		else if (gameManager.isCurrentHost(player)) {
			if (gameManager.getGameState() == GameState.STARTING) {
				if (match(args, 0, "cancel")) {
					gameManager.cancelStarting();
					send(player, "§2Lancement de la partie annulé.");
					return;
				}
			}
			send(player, "§cVous ne pouvez plus configurer la partie une fois lancée.");
		}
		else {
			send(player, "§cVous n'avez pas besoin de cette commande.");
		}
	}

}
