/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.role.village;

import eu.izmoqwy.wwuhc.role.Role;
import eu.izmoqwy.wwuhc.role.RoleSide;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

public class RoleAngel extends Role {
	public RoleAngel() {
		super("Ange", createDescription(
				"Vous gagnerez un coeur supplémentaire à chaque fois que deux personnes vote contre vous. Vous ne prenez pas les votes si un nombre paire de personnes votent contre vous et vous pouvez voter pour vous même."
		), new MaterialData(Material.FEATHER), RoleSide.VILLAGE);
	}
}
