/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.role.solo;

import eu.izmoqwy.wwuhc.role.RoleStealer;
import eu.izmoqwy.wwuhc.role.RoleSide;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffectType;

public class RoleThief extends RoleStealer {

	public RoleThief() {
		super("Voleur", createDescription(
				"Une fois que vous avez fait un meurtre, vous récupérez le rôle de la personne tuée. Vous devrez donc gagner avec votre futur camp. Lorsque vous tuer quelqu'un vous récupérerez également son infection ou son couple.",
				"Pour vous aidez à tuer quelqu'un vous disposez de l'effet Resistance I jusqu'au premier meurtre."
		), new MaterialData(Material.SOUL_SAND), RoleSide.SOLO);

		this.dailyEffects = new PotionEffectType[]{PotionEffectType.DAMAGE_RESISTANCE};
		this.nightlyEffects = new PotionEffectType[]{PotionEffectType.DAMAGE_RESISTANCE};
	}

}
