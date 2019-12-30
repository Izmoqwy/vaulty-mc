/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.scenario.all;

import eu.izmoqwy.uhc.event.entity.EntityDamagePlayerUHCEvent;
import eu.izmoqwy.uhc.event.registration.UHCEventHandler;
import eu.izmoqwy.uhc.event.registration.UHCEventPriority;
import eu.izmoqwy.uhc.event.registration.UHCListener;
import eu.izmoqwy.uhc.scenario.Scenario;
import org.bukkit.Material;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageEvent;

public class ScenarioFriendlyTnt extends Scenario implements UHCListener {

	public ScenarioFriendlyTnt() {
		super("FriendlyTnT", "Immunité aux dégâts de la TnT", Material.TNT);
	}

	@UHCEventHandler(priority = UHCEventPriority.SCENARIO, ignoreCancelled = true)
	public void onTnTDamage(EntityDamagePlayerUHCEvent event) {
		if (event.getBukkitEvent().getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION && event.getDamager() instanceof TNTPrimed) {
			event.setCancelled(true);
		}
	}
}
