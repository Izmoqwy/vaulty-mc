/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.scenario.all;

import com.google.common.collect.Maps;
import eu.izmoqwy.uhc.VaultyUHC;
import eu.izmoqwy.uhc.config.GUIConfigurable;
import eu.izmoqwy.uhc.config.GUISetting;
import eu.izmoqwy.uhc.event.registration.UHCEventHandler;
import eu.izmoqwy.uhc.event.registration.UHCEventPriority;
import eu.izmoqwy.uhc.event.registration.UHCListener;
import eu.izmoqwy.uhc.event.world.WorldBlockBreakUHCEvent;
import eu.izmoqwy.uhc.scenario.Scenario;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class ScenarioDiamondLimit extends Scenario implements UHCListener, GUIConfigurable {

	public ScenarioDiamondLimit() {
		super("DiamondLimit", "Limite de diamants minés", Material.DIAMOND);
	}

	@GUISetting(name = "Limite", icon = Material.DIAMOND_PICKAXE)
	private int limit = 17;

	@GUISetting(name = "Remplacer par de l'or", icon = Material.GOLD_INGOT)
	private boolean replace = true;

	@Setter(AccessLevel.NONE)
	private Map<UUID, Short> players = Maps.newHashMap();

	@UHCEventHandler(priority = UHCEventPriority.SCENARIO, ignoreCancelled = true)
	public void onBlockBreakScenario(WorldBlockBreakUHCEvent event) {
		short mined;
		if (event.getBlock().getType() == Material.DIAMOND_ORE && (mined = players.getOrDefault(event.getPlayer().getUniqueId(), (short) 0)) >= limit) {
			if (mined == limit) {
				event.getPlayer().sendMessage(VaultyUHC.PREFIX + "§6Vous avez atteint votre limite de diamants !");
			}
			if (replace)
				event.setCustomDrop(new ItemStack(Material.GOLD_INGOT));
			else
				event.setCancelled(true);
		}
	}


	@UHCEventHandler(priority = UHCEventPriority.AFTER, ignoreCancelled = true)
	public void onBlockBreakAfter(WorldBlockBreakUHCEvent event) {
		if (event.getBlock().getType() == Material.DIAMOND_ORE) {
			UUID id = event.getPlayer().getUniqueId();
			players.put(id, (short) (players.getOrDefault(id, (short) 0) + 1));
		}
	}
}
