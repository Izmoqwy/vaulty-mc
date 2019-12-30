/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.role.village;

import eu.izmoqwy.wwuhc.role.RoleStealer;
import eu.izmoqwy.wwuhc.role.RoleSide;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

public class RoleDevotedServant extends RoleStealer {

	public RoleDevotedServant() {
		super("Servante Dévouée", createDescription(
				"Chaque fois qu'une personne meurt, vous aurez 10 secondes pour décidez de prendre ou non le rôle de la personne qui vient de mourir. Vous deviendrez donc le rôle de la personne morte ainsi que son infection. Vous gagnerez avec votre futur camp ou avec le village si vous ne volez personne"
		), new MaterialData(Material.APPLE), RoleSide.VILLAGE);
	}
}
