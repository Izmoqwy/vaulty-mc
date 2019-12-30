/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.event.player;

import eu.izmoqwy.uhc.event.CancellableEvent;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

@Getter
public class PlayerAnyDamageUHCEvent extends CancellableEvent {

	private Player player;
	private EntityDamageEvent bukkitEvent;

	public PlayerAnyDamageUHCEvent(EntityDamageEvent bukkitEvent) {
		super(false);
		this.player = (Player) bukkitEvent.getEntity();
		this.bukkitEvent = bukkitEvent;
	}
}
