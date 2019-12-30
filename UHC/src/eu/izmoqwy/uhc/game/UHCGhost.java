/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.game;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public class UHCGhost {

	private LivingEntity ghostEntity;

	private ItemStack[] armorContents, contents;
	private int level, totalExperience;
	private float exp;

	public static UHCGhost from(LivingEntity ghostEntity, Player player) {
		return new UHCGhost(ghostEntity, player.getInventory().getArmorContents(), player.getInventory().getContents(), player.getLevel(), player.getTotalExperience(), player.getExp());
	}

	public void apply(Player player) {
		player.getInventory().setArmorContents(armorContents);
		player.getInventory().setContents(contents);
		player.setLevel(level);
		player.setTotalExperience(totalExperience);
		player.setExp(exp);
	}

	public void drop(Location location) {
		Preconditions.checkNotNull(location);

		World world = location.getWorld();
		Preconditions.checkNotNull(world);

		for (ItemStack armorItem : getArmorContents()) {
			if (armorItem != null && armorItem.getType() != Material.AIR)
				world.dropItemNaturally(location, armorItem);
		}
		for (ItemStack item : getContents()) {
			if (item != null && item.getType() != Material.AIR)
				world.dropItemNaturally(location, item);
		}
	}

}
