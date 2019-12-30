/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.game;

import eu.izmoqwy.uhc.scenario.GameType;
import lombok.AllArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@AllArgsConstructor
public class Couple {

	private OfflinePlayer player1, player2;

	public boolean contains(UUID uuid) {
		return player1.getUniqueId().equals(uuid) || player2.getUniqueId().equals(uuid);
	}

	public OfflinePlayer getOther(OfflinePlayer comparedTo) {
		if (player1.getUniqueId().equals(comparedTo.getUniqueId()))
			return player2;
		else if (player2.getUniqueId().equals(comparedTo.getUniqueId()))
			return player1;
		return null;
	}

	public boolean isOnline() {
		return GameType.isOnline(player1) && GameType.isOnline(player2);
	}

	public void execute(Consumer<Player> consumer) {
		if (!isOnline())
			return;

		consumer.accept(player1.getPlayer());
		consumer.accept(player2.getPlayer());
	}

	public void execute(BiConsumer<Player, Player> consumer) {
		if (!isOnline())
			return;

		Player onlinePlayer1 = player1.getPlayer(), onlinePlayer2 = player2.getPlayer();
		consumer.accept(onlinePlayer1, onlinePlayer2);
		consumer.accept(onlinePlayer2, onlinePlayer1);
	}

	public void exchange(OfflinePlayer player, OfflinePlayer with) {
		if (player1.getUniqueId().equals(player.getUniqueId()))
			player1 = with;
		else if (player2.getUniqueId().equals(player.getUniqueId()))
			player2 = with;
	}
}
