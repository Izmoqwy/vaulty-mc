/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.role.werewolves;

import eu.izmoqwy.wwuhc.role.Role;
import eu.izmoqwy.wwuhc.role.RoleSide;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffectType;

public class RoleGhostWolf extends Role {

	public RoleGhostWolf() {
		super("Loup Fantôme", createDescription(
				"Vous disposez de l'effet Invisibility, Night Vision et Strength I durant la nuit. Vous gagnerez deux coeurs d'absorptions et l'effet Speed I pendant une minute lors d'un meurtre."
		), new MaterialData(Material.WEB), RoleSide.WEREWOLF);
		this.nightlyEffects = new PotionEffectType[]{PotionEffectType.INVISIBILITY, PotionEffectType.INCREASE_DAMAGE, PotionEffectType.NIGHT_VISION};
	}
}
