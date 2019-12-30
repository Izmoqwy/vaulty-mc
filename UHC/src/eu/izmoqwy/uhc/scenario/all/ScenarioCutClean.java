/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.scenario.all;

import com.google.common.collect.Lists;
import eu.izmoqwy.uhc.event.entity.EntityKilledUHCEvent;
import eu.izmoqwy.uhc.event.registration.UHCEventHandler;
import eu.izmoqwy.uhc.event.registration.UHCEventPriority;
import eu.izmoqwy.uhc.event.registration.UHCListener;
import eu.izmoqwy.uhc.event.world.WorldBlockBreakUHCEvent;
import eu.izmoqwy.uhc.scenario.Scenario;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScenarioCutClean extends Scenario implements UHCListener {

	private final Map<Material, Map.Entry<Material, Integer>> blocks = new HashMap<Material, Map.Entry<Material, Integer>>() {{
		put(Material.COAL_ORE, new SimpleEntry<>(Material.TORCH, 2));
		put(Material.IRON_ORE, new SimpleEntry<>(Material.IRON_INGOT, 4));
		put(Material.GOLD_ORE, new SimpleEntry<>(Material.GOLD_INGOT, 5));

		put(Material.SAND, new SimpleEntry<>(Material.GLASS, 0));
		put(Material.POTATO, new SimpleEntry<>(Material.POTATO_ITEM, 0));
		put(Material.CARROT, new SimpleEntry<>(Material.POTATO_ITEM, 0));
		put(Material.WHEAT, new SimpleEntry<>(Material.BREAD, 0));
	}};

	private final Map<Material, Material> drops = new HashMap<Material, Material>() {{
		put(Material.MUTTON, Material.COOKED_MUTTON);
		put(Material.PORK, Material.GRILLED_PORK);
		put(Material.RAW_BEEF, Material.COOKED_BEEF);
		put(Material.RABBIT, Material.COOKED_RABBIT);
		put(Material.RAW_CHICKEN, Material.COOKED_CHICKEN);
	}};

	public ScenarioCutClean() {
		super("CutClean", "Cuit les minerais/loots", Material.IRON_INGOT);
	}

	@UHCEventHandler(priority = UHCEventPriority.SCENARIO, ignoreCancelled = true)
	public void onBlockBreak(WorldBlockBreakUHCEvent event) {
		if (blocks.containsKey(event.getBlock().getType())) {
			Map.Entry<Material, Integer> entry = blocks.get(event.getBlock().getType());
			event.setCustomDrop(new ItemStack(entry.getKey()));
			event.setExpToDrop(entry.getValue());
		}
	}

	@UHCEventHandler(priority = UHCEventPriority.SCENARIO, ignoreCancelled = true)
	public void onEntityDeath(EntityKilledUHCEvent event) {
		List<ItemStack> finalDrops = Lists.newArrayList();
		for (ItemStack drop : event.getBukkitEvent().getDrops().toArray(new ItemStack[0])) {
			if (drops.containsKey(drop.getType()))
				finalDrops.add(new ItemStack(drops.get(drop.getType()), drop.getAmount()));
			else
				finalDrops.add(drop);
		}
		event.getBukkitEvent().getDrops().clear();
		event.getBukkitEvent().getDrops().addAll(finalDrops);
	}

}
