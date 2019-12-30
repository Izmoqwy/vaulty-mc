/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.role.village;

import eu.izmoqwy.vaulty.builder.ItemBuilder;
import eu.izmoqwy.wwuhc.role.Role;
import eu.izmoqwy.wwuhc.role.RoleSide;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class RoleHunter extends Role {
	public RoleHunter() {
		super("Chasseur", createDescription(
				"Vous disposez d'un arc, de 32 flèches, de livres Power III et Punch I. Lorsque vous mourrez, vous pouvez tirer sur la personne de votre choix, celle-ci perdra 5 coeurs. Votre tire s'affichera dans le chat publique."
		), new MaterialData(Material.BOW), RoleSide.VILLAGE);
		this.itemStacks = new ItemStack[]{
				new ItemStack(Material.BOW),
				new ItemStack(Material.ARROW, 32),
				new ItemBuilder(Enchantment.ARROW_DAMAGE, 3).toItemStack(),
				new ItemBuilder(Enchantment.ARROW_KNOCKBACK, 1).toItemStack()
		};
	}
}
