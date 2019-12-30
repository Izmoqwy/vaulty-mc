/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.event.entity;

import eu.izmoqwy.uhc.event.UHCEvent;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDeathEvent;

@Getter
public class EntityKilledUHCEvent extends UHCEvent {

	private Entity entity;
	private EntityDeathEvent bukkitEvent;

	public EntityKilledUHCEvent(EntityDeathEvent bukkitEvent) {
		this.entity = bukkitEvent.getEntity();
		this.bukkitEvent = bukkitEvent;
	}
}
