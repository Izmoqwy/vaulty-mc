/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.scenario;

import eu.izmoqwy.uhc.VaultyUHC;
import eu.izmoqwy.uhc.event.player.GhostKilledUHCEvent;
import eu.izmoqwy.uhc.event.player.PlayerDeathUHCEvent;
import eu.izmoqwy.uhc.event.registration.UHCEventHandler;
import eu.izmoqwy.uhc.event.registration.UHCEventPriority;
import eu.izmoqwy.uhc.event.registration.UHCListener;
import eu.izmoqwy.uhc.game.GameComposer;
import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.uhc.game.UHCGame;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TypeClassic extends GameType implements UHCListener {

	private UHCGame game;

	public TypeClassic() {
		super("Classique", "Aucune spécialité de jeu", Material.GOLDEN_APPLE, new GameComposer(GameComposer.defaultComposer), GameComposer.class);
	}

	public void startGame(UHCGame game) {
		this.game = game;
	}

	public void stopGame() {
		this.game = null;
		defaultComposer = new GameComposer(GameComposer.defaultComposer);
	}

	@UHCEventHandler(priority = UHCEventPriority.GAME_TYPE)
	public void onDeath(PlayerDeathUHCEvent event) {
		game.broadcast("§e" + event.getBukkitEvent().getDeathMessage());
		event.getBukkitEvent().setDeathMessage(null);
		new BukkitRunnable() {
			@Override
			public void run() {
				if (game == null)
					return;

				game.eliminatePlayer(event.getPlayer());
				checkForWin();
			}
		}.runTaskLater(VaultyUHC.getInstance(), 2);
	}

	@UHCEventHandler(priority = UHCEventPriority.GAME_TYPE)
	public void onGhostKill(GhostKilledUHCEvent event) {
		game.broadcast("§e" + Bukkit.getOfflinePlayer(event.getVictim()).getName() + " a été tué hors ligne.");
		new BukkitRunnable() {
			@Override
			public void run() {
				checkForWin();
			}
		}.runTaskLater(VaultyUHC.getInstance(), 2);
	}

	@Override
	public void checkForWin() {
		if (game == null)
			return;

		if (game.getOnlinePlayers().size() <= 1) {
			Player winner = game.getOnlinePlayers().isEmpty() ? game.getLastEliminated() : game.getOnlinePlayers().get(0);
			if (winner == null) {
				game.broadcast(null);
				game.broadcast("§cLa partie n'a pas trouvée de gagnant !");
				game.broadcast(null);
				return;
			}

			game.broadcast(null);
			game.broadcast("§dBravo à §5" + winner.getName() + "§d qui remporte la partie.");
			game.broadcast(null);

			GameManager.get.end();
		}
	}
}
