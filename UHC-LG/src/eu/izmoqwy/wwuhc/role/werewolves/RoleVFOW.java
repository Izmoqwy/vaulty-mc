/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.role.werewolves;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

public class RoleVFOW extends RoleWerewolf {

	public RoleVFOW() {
		super("Infect Père des Loups", createDescription(
				"Une fois dans la partie, vous pouvez infecter une personne morte par la main d'un loup-garou, celle-ci deviendra un loup et devra gagner ainsi avec vous."
		), new MaterialData(Material.BONE));
	}
}
