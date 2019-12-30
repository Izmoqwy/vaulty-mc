/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.event.player;

import eu.izmoqwy.uhc.event.CancellableEvent;
import lombok.Getter;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.UUID;

@Getter
public class GhostKilledUHCEvent extends CancellableEvent {

	private UUID victim;
	private EntityDamageEvent bukkitEvent;

	public GhostKilledUHCEvent(UUID victim, EntityDamageEvent bukkitEvent) {
		super(false);
		this.victim = victim;
		this.bukkitEvent = bukkitEvent;
	}

}
