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
public class PlayerDamagePlayerUHCEvent extends CancellableEvent {

	private Player damager, victim;
	private EntityDamageEvent.DamageCause cause;

	private double damage;
	private boolean fatal;

	public PlayerDamagePlayerUHCEvent(Player damager, Player victim, EntityDamageEvent.DamageCause cause, double damage) {
		super(false);
		this.damager = damager;
		this.victim = victim;
		this.cause = cause;
		setDamage(damage);
	}

	public void setDamage(double damage) {
		this.damage = damage;
		this.fatal = victim.isDead() || (victim.getHealth() - damage) <= 0;
	}

}
