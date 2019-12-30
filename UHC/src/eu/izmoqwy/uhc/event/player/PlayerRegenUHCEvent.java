/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.event.player;

import eu.izmoqwy.uhc.event.CancellableEvent;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;

@Getter
public class PlayerRegenUHCEvent extends CancellableEvent {

	private Player player;
	private EntityRegainHealthEvent bukkitEvent;

	public PlayerRegenUHCEvent(EntityRegainHealthEvent bukkitEvent) {
		super(bukkitEvent.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED || bukkitEvent.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN);
		this.player = (Player) bukkitEvent.getEntity();
		this.bukkitEvent = bukkitEvent;
	}
}
