/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty.utils;

import com.google.common.base.Preconditions;
import eu.izmoqwy.vaulty.nms.NMS;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerUtil {

	@SuppressWarnings("deprecation")
	public static OfflinePlayer getOfflinePlayer(String name) {
		Preconditions.checkNotNull(name);

		Player onlinePlayer = Bukkit.getPlayerExact(name);
		if (onlinePlayer != null)
			return onlinePlayer;

		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
		if (offlinePlayer != null && offlinePlayer.hasPlayedBefore())
			return offlinePlayer;
		
		return null;
	}

	/*
	Visibilité
	 */
	public static void hidePlayersMutually(Player target, Player... players) {
		Preconditions.checkNotNull(target);

		for (Player player : players) {
			player.hidePlayer(target);
			target.hidePlayer(player);
		}
	}

	public static void showPlayersMutually(Player target, Player... players) {
		Preconditions.checkNotNull(target);

		for (Player player : players) {
			player.showPlayer(target);
			target.showPlayer(player);
		}
	}

	/*
	Effets de potion
	 */

	public static void giveEffect(Player player, PotionEffectType effectType, short level, short seconds) {
		giveEffect(player, effectType, level, seconds, false);
	}

	public static void giveEffect(Player player, PotionEffectType effectType, short level, short seconds, boolean hidden) {
		Preconditions.checkNotNull(player);
		Preconditions.checkNotNull(effectType);

		player.addPotionEffect(new PotionEffect(effectType, seconds * 20, level, false, !hidden));
	}

	public static void clearEffect(Player player, PotionEffectType effectType) {
		Preconditions.checkNotNull(player);

		if (player.hasPotionEffect(effectType)) {
			player.removePotionEffect(effectType);
		}
	}

	public static void clearEffects(Player player) {
		Preconditions.checkNotNull(player);

		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
	}

	/*
	Title
	 */

	public static void sendTitle(Player player, String title, String subTitle, int ticks) {
		Preconditions.checkNotNull(player);

		NMS.packets.sendTitle(player, title, subTitle);
		NMS.packets.sendTimings(player, ticks, 5, 5);
	}

	/*
	Inventaire
	 */

	public static void dropFullPlayerInventory(PlayerInventory inventory, Location location) {
		Preconditions.checkNotNull(inventory);
		Preconditions.checkNotNull(location);

		World world = location.getWorld();
		Preconditions.checkNotNull(world);

		for (ItemStack armorItem : inventory.getArmorContents()) {
			if (armorItem != null && armorItem.getType() != Material.AIR)
				world.dropItemNaturally(location, armorItem);
		}
		for (ItemStack item : inventory.getContents()) {
			if (item != null && item.getType() != Material.AIR)
				world.dropItemNaturally(location, item);
		}
	}

	public static void clearInventory(Player player) {
		Preconditions.checkNotNull(player);

		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);
	}

	public static void reset(Player player) {
		partialReset(player);
		clearEffects(player);
		clearInventory(player);
	}

	public static void partialReset(Player player) {
		player.setLevel(0);
		player.setExp(0);
		player.setTotalExperience(0);
		player.setMaxHealth(20);
		player.setHealth(player.getMaxHealth());
		player.setFoodLevel(20);
		player.setSaturation(30);
	}
}
