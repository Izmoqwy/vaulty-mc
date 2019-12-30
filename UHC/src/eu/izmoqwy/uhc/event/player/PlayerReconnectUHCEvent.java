/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.event.player;

import eu.izmoqwy.uhc.event.UHCEvent;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class PlayerReconnectUHCEvent extends UHCEvent {

	private Player player;

	public PlayerReconnectUHCEvent(Player player) {
		this.player = player;
	}

}
