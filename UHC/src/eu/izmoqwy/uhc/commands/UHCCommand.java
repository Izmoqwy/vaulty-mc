/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.commands;

import eu.izmoqwy.uhc.VaultyUHC;
import eu.izmoqwy.uhc.game.GameActor;
import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.uhc.game.GameState;
import eu.izmoqwy.uhc.game.UHCGame;
import eu.izmoqwy.uhc.gui.composer.HostGUI;
import eu.izmoqwy.uhc.gui.WorldPreGeneratorGUI;
import eu.izmoqwy.uhc.scenario.GameType;
import eu.izmoqwy.uhc.world.UHCWorldManager;
import eu.izmoqwy.uhc.world.UHCWorldPreGenerator;
import eu.izmoqwy.vaulty.VaultyCommand;
import eu.izmoqwy.vaulty.oss.OSS;
import eu.izmoqwy.vaulty.rank.Rank;
import eu.izmoqwy.vaulty.rank.VaultyRank;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UHCCommand extends VaultyCommand {

	private final HostGUI hostGUI = new HostGUI(this);
	private WorldPreGeneratorGUI preGeneratorGUI;

	public UHCCommand() {
		super("uhc", true);

		helpCommands.put("host", "Créer une partie (HOST)");
		helpCommands.put("join", "Rejoindre la partie en cours de lancement");

		try {
			preGeneratorGUI = new WorldPreGeneratorGUI();
		}
		catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void executeCommand(CommandSender commandSender, String[] args) {
		Player player = (Player) commandSender;
		GameManager gameManager = GameManager.get;

		if (match(args, 0, "join")) {
			if (gameManager.getCurrentComposer() != null) {
				if (VaultyUHC.getInstance().getUhcBukkitListener().mayJoin(player)) {
					OSS.send(player, VaultyUHC.getUhcServer());
					return;
				}

				if (gameManager.getKicked().contains(player.getUniqueId())) {
					send(player, "§cAccès à la partie refusé: §fVous avez été expulsé de la partie");
					return;
				}

				if (gameManager.getWhitelist() != null && gameManager.getWhitelist().isEnabled() && !gameManager.getWhitelist().getAllowed().contains(player.getUniqueId())) {
					send(player, "§cAccès à la partie refusé: §fVous n'êtes pas dans la whitelist");
					return;
				}

				if (gameManager.ableToJoin()) {
					if (gameManager.getWaitingRoom().getAllPlayers().contains(player)) {
						send(player, "§7Vous êtes déjà en salle d'attente.");
						return;
					}

					if (gameManager.getWaitingRoom().getPlayers().size() >= gameManager.getCurrentComposer().getMaxPlayers()) {
						send(player, "§cLa partie a atteint son maximum de joueurs.");
						return;
					}

					gameManager.getWaitingRoom().addPlayer(player, GameActor.PLAYER, true);
					send(player, "§aVous avez rejoint la salle d'attente.");
				}
				else if (gameManager.getGameState() == GameState.PLAYING) {
					UHCGame game = gameManager.getCurrentGame();
					if (game.getOnlinePlayers().contains(player)) {
						send(player, "§7Vous êtes déjà dans la partie.");
						return;
					}
					if (game.getOnlineSpectators().contains(player)) {
						send(player, "§7Vous êtes déjà spectateur de cette partie.");
						return;
					}

					game.spectate(player);
					send(player, "§aVous êtes désormais un spectateur de la partie.");
				}
				else {
					send(player, "§cImpossible de rejoindre la partie.");
				}
			}
			else {
				send(player, "§cAucune partie n'est en cours.");
			}
		}
		else if (match(args, 0, "host")) {
			if (canHost(player)) {
				if (VaultyRank.get(player).isEqualsOrAbove(Rank.HOST)) {
					hostGUI.open(player);
				}
				else {
					send(player, "§cVous n'avez pas la permission de lancer une partie.");
				}
			}
		}
		else if (match(args, 0, "pregen")) {
			checkArgument(VaultyRank.get(player).isEqualsOrAbove(Rank.DEVELOPER), "Vous n'avez pas la permission de faire cela !");
			checkArgument(!UHCWorldPreGenerator.running, "Un monde est déjà en pré-génération.");

			if (preGeneratorGUI != null)
				preGeneratorGUI.open(player);
		}
		else if (match(args, 0, "help", "?")) {
			sendHelp(player);
		}
		else {
			send(player, "§cArgument manquant ou invalide, faites '/uhc help' pour obtenir de l'aide.");
		}
	}

	private boolean canHost(Player player) {
		if (GameManager.get.getGameState() != null) {
			send(player, "§cUne partie est déjà en " + (GameManager.get.getGameState() == GameState.COMPOSING ? "composition" : "cours") + ".");
			return false;
		}
		return true;
	}

	public void createGame(Player player, GameType gameType) {
		if (!canHost(player))
			return;

		if (!UHCWorldManager.get.reset()) {
			send(player, "§cAucun monde n'est disponible !");
			return;
		}

		OSS.send(player, VaultyUHC.getUhcServer());
		GameManager gameManager = GameManager.get;
		gameManager.createGame(player, gameType);
		gameManager.getWaitingRoom().addPlayer(player, GameActor.PLAYER, true);
		gameManager.getWaitingRoom().updateDebugInfoState();
		send(player, "§aVotre partie est prête.");
	}
}
