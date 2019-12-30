/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.role.village;

import eu.izmoqwy.wwuhc.role.Role;
import eu.izmoqwy.wwuhc.role.RoleSide;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffectType;

public class RoleLittleGirl extends Role {

	public RoleLittleGirl() {
		super("Petite Fille", createDescription(
				"Vous disposez de l'effet Invisibility pour vous cacher dans la nuit mais également Weakness I."
		), new MaterialData(Material.GOLDEN_CARROT), RoleSide.VILLAGE);
		this.nightlyEffects = new PotionEffectType[]{PotionEffectType.WEAKNESS, PotionEffectType.INVISIBILITY};
	}
}
