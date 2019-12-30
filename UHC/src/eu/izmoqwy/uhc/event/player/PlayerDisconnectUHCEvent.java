/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.event.player;

import eu.izmoqwy.uhc.event.UHCEvent;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class PlayerDisconnectUHCEvent extends UHCEvent {

	private Player player;

	public PlayerDisconnectUHCEvent(Player player) {
		this.player = player;
	}

}
