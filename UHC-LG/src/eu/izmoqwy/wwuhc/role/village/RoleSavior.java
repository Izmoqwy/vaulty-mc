/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.role.village;

import eu.izmoqwy.wwuhc.role.Role;
import eu.izmoqwy.wwuhc.role.RoleSide;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

public class RoleSavior extends Role {
	public RoleSavior() {
		super("Salvateur", createDescription(
				"Chaque matin, vous pouvez donner Resistance I ainsi que l'annulation des dégâts de chute à n'importe quel joueur y compris vous. Cette personne gardera cette protection le jour et la nuit qui suivent. Vous ne pouvez pas protéger la même personne deux jours de suite.",
				"Faîtes '/lg proteger <joueur>' protéger un joueur."
		), new MaterialData(Material.IRON_BLOCK), RoleSide.VILLAGE);
	}
}
