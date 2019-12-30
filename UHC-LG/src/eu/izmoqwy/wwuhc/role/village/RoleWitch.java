/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.role.village;

import eu.izmoqwy.wwuhc.role.Role;
import eu.izmoqwy.wwuhc.role.RoleSide;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class RoleWitch extends Role {
	public RoleWitch() {
		super("Sorcière", createDescription(
				"Vous disposez d'un potion de vie qui vous permet de ressusciter une personne lorsqu'elle meurt une fois dans la partie.",
				"Vous pouvez vous ressusciter vous-même."
		), new MaterialData(Material.POTION), RoleSide.VILLAGE);
		this.itemStacks = new ItemStack[]{
				new ItemStack(Material.POTION, 3, (short) 16453),
				new ItemStack(Material.POTION, 3, (short) 16460),
				new ItemStack(Material.POTION, 1, (short) 16385)
		};
	}
}
