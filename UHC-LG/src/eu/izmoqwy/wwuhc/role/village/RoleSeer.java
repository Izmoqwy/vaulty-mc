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

public class RoleSeer extends Role {
	public RoleSeer() {
		super("Voyante", createDescription(
				"Vous disposez de 4 obsidiennes et 4 bibliothèques",
				"Au beau matin vous avez le pouvoir d'espionner le rôle d'un joueur. Son rôle sera divulguer dans le chat publique mais vous seul connaîtrez la personne auquel il appartient.",
				"Faîtes '/lg voir <joueur>' pour voir le rôle d'un joueur."
		), new MaterialData(Material.SPIDER_EYE), RoleSide.VILLAGE);
		this.itemStacks = new ItemStack[]{new ItemStack(Material.OBSIDIAN, 4), new ItemStack(Material.BOOKSHELF, 4)};
	}
}
