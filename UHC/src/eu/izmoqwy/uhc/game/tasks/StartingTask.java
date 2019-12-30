/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.game.tasks;

import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.vaulty.utils.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class StartingTask extends BukkitRunnable {

	private final GameManager gameManager;

	public StartingTask(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	private short timeLeft = 10;

	@Override
	public void run() {
		if (timeLeft <= 0) {
			gameManager.getWaitingRoom().broadcast("§aLa partie débute, téléportation des joueurs...");
			gameManager.teleport();
			cancel();
			return;
		}

		if (timeLeft <= 3 || timeLeft % 10 == 0)
			gameManager.getWaitingRoom().broadcast("§eTéléportation dans §6" + timeLeft + " secondes§e.");
		if (timeLeft <= 15)
			gameManager.getWaitingRoom().getAllPlayers().forEach(this::updatePlayer);

		this.timeLeft--;
	}

	private void updatePlayer(Player player) {
		if (timeLeft <= 5 || timeLeft % 5 == 0)
			PlayerUtil.sendTitle(player, (timeLeft <= 3 ? "§c" : (timeLeft <= 5 ? "§6" : "§e")) + timeLeft, null, 10);
		player.setLevel(timeLeft);
		player.setExp(0);
	}
}
