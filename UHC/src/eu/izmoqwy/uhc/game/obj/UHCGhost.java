/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.game.obj;

import com.google.common.base.Preconditions;
import eu.izmoqwy.vaulty.utils.PlayerUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class UHCGhost {

	private LivingEntity ghostEntity;

	private ItemStack[] armorContents, contents;
	private int level, totalExperience;
	private float exp;
	private Collection<PotionEffect> potionEffectCollection;

	public static UHCGhost from(LivingEntity ghostEntity, Player player) {
		return new UHCGhost(ghostEntity, player.getInventory().getArmorContents(), player.getInventory().getContents(),
				player.getLevel(), player.getTotalExperience(), player.getExp(), player.getActivePotionEffects());
	}

	public void apply(Player player) {
		player.getInventory().setArmorContents(armorContents);
		player.getInventory().setContents(contents);
		player.setLevel(level);
		player.setTotalExperience(totalExperience);
		player.setExp(exp);

		PlayerUtil.clearEffects(player);
		for (PotionEffect potionEffect : potionEffectCollection) {
			player.addPotionEffect(potionEffect);
		}
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
