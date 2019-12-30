/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.role.village;

import eu.izmoqwy.wwuhc.role.Role;
import eu.izmoqwy.wwuhc.role.RoleSide;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

public class RoleTeacher extends Role {
	public RoleTeacher() {
		super("Institutrice", createDescription(
				"En fin de nuit, avant chaque jour de vote, vous pouvez choisir jusqu'à deux personnes, celles-ci ne pourront plus voter. Mais si vous choisissez, vous perdez également votre droit de vote."
		), new MaterialData(Material.BOOK_AND_QUILL), RoleSide.VILLAGE);
	}
}
