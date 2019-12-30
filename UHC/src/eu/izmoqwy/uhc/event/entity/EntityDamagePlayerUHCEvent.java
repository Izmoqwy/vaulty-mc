/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.event.entity;

import eu.izmoqwy.uhc.event.CancellableEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@Getter
public class EntityDamagePlayerUHCEvent extends CancellableEvent {

	private Entity damager;
	private Player player;
	private EntityDamageByEntityEvent bukkitEvent;

	@Setter
	private double damage;

	public EntityDamagePlayerUHCEvent(EntityDamageByEntityEvent bukkitEvent) {
		super(bukkitEvent.isCancelled());
		this.damager = bukkitEvent.getDamager();
		this.player = (Player) bukkitEvent.getEntity();
		this.bukkitEvent = bukkitEvent;
		this.damage = bukkitEvent.getDamage();
	}
}
