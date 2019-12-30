/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty.utils;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

public class ItemUtil {

	/*
	Methode perso pour donner des items a un joueur sans passer par le systeme normal
	 */
	public static List<ItemStack> give(Player player, ItemStack... items) {
		PlayerInventory inventory = player.getInventory();
		final int inventorySize = 36;
		List<ItemStack> remaining = Lists.newArrayList();

		all:
		for (ItemStack item : items) {
			int total = item.getAmount(), toGive = total;
			final int max = item.getType() != Material.POTION ? item.getMaxStackSize() : Math.max(16, item.getMaxStackSize());

			// Partials
			for (int i = 0; i < inventorySize; i++) {
				ItemStack content = inventory.getItem(i);
				if (content != null && content.getAmount() < max && content.isSimilar(item)) {
					int _amount = Math.min(max - content.getAmount(), toGive);
					content.setAmount(content.getAmount() + _amount);
					toGive -= _amount;
					if (toGive == 0)
						continue all;
				}
			}

			// Empties
			for (int i = 0; i < inventorySize; i++) {
				ItemStack content = inventory.getItem(i);
				if (content == null) {
					int _amount = Math.min(toGive, max);
					item = item.clone();
					item.setAmount(_amount);
					inventory.setItem(i, item);
					toGive -= _amount;
					if (toGive == 0)
						continue all;
				}
			}

			item = item.clone();
			item.setAmount(toGive);
			remaining.add(item);
		}

		return remaining;
	}

	public static boolean giveOrDrop(Player player, ItemStack... items) {
		List<ItemStack> leftover = give(player, items);
		if (leftover.isEmpty())
			return false;

		dropAtPlayer(player, items);
		return true;
	}

	public static void dropAtPlayer(Player player, ItemStack... items) {
		for (ItemStack item : items) {
			player.getWorld().dropItem(player.getLocation(), item);
		}
	}

}
