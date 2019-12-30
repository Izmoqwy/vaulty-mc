/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.role.village;

import eu.izmoqwy.wwuhc.role.Role;
import eu.izmoqwy.wwuhc.role.RoleSide;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

public class RoleVillager extends Role {

	public RoleVillager() {
		super("Simple Villageois", createDescription(
				"Vous n'avez pas de pouvoir particulier."
		), new MaterialData(Material.HAY_BLOCK), RoleSide.VILLAGE, true);
	}

}
