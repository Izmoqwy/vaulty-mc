/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.event.player;

import eu.izmoqwy.uhc.event.UHCEvent;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

@Getter
public class PlayerDeathUHCEvent extends UHCEvent {

	private Player player;
	private PlayerDeathEvent bukkitEvent;

	public PlayerDeathUHCEvent(PlayerDeathEvent bukkitEvent) {
		this.bukkitEvent = bukkitEvent;
		this.player = bukkitEvent.getEntity();
	}
}
