/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.role.solo;

import eu.izmoqwy.vaulty.builder.ItemBuilder;
import eu.izmoqwy.wwuhc.role.Role;
import eu.izmoqwy.wwuhc.role.RoleSide;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffectType;

public class RoleMurderer extends Role {
	public RoleMurderer() {
		super("Assassin", createDescription(
				"Vous disposez de l'effet Strength I durant le jour, d'un livre Sharpness III, d'un livre Protection III et d'un livre Power III.",
				"Vous gagnerez également l'effet Speed I et deux coeurs d'absorption lors d'un kill"
		), new MaterialData(Material.DIAMOND_SWORD), RoleSide.SOLO);
		this.dailyEffects = new PotionEffectType[]{PotionEffectType.INCREASE_DAMAGE};
		this.itemStacks = new ItemStack[]{
				new ItemBuilder(Enchantment.DAMAGE_ALL, 3).toItemStack(),
				new ItemBuilder(Enchantment.PROTECTION_ENVIRONMENTAL, 3).toItemStack(),
				new ItemBuilder(Enchantment.ARROW_DAMAGE, 3).toItemStack()};
	}
}
