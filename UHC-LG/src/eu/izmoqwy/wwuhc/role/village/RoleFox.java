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

public class RoleFox extends Role {
	public RoleFox() {
		super("Renard", createDescription(
				"Vous disposez de l'effet Speed I et de la capacité de flairer trois joueurs par partie. Vous serrez si le joueur en question ou un de ses voisins se trouvant à moins de 10 blocs est loup-garou.",
				"Faîtes '/lg flairer <joueur>' pour flairer le camp d'un joueur."
		), new MaterialData(Material.STICK), RoleSide.VILLAGE);
		this.dailyEffects = new PotionEffectType[]{PotionEffectType.SPEED};
		this.nightlyEffects = new PotionEffectType[]{PotionEffectType.SPEED};
	}
}
