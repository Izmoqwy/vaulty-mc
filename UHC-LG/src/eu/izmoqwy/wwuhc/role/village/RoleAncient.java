/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.role.village;

import eu.izmoqwy.uhc.config.GUIConfigurable;
import eu.izmoqwy.uhc.config.GUISetting;
import eu.izmoqwy.vaulty.utils.PlayerUtil;
import eu.izmoqwy.wwuhc.role.Role;
import eu.izmoqwy.wwuhc.role.RoleSide;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffectType;

@Getter
public class RoleAncient extends Role implements GUIConfigurable {

	@Accessors(fluent = true)
	private boolean canRevive = true;

	@GUISetting(name = "Perte de resistance", icon = Material.IRON_TRAPDOOR)
	private boolean looseResistanceOnRevive = true;

	public RoleAncient() {
		super("Ancien", createDescription(
				"Vous disposez d'une vie supplémentaire (si vous mourrez vous serrez ressuscité) et de l'effet Resistance I."
		), new MaterialData(Material.CHAINMAIL_CHESTPLATE), RoleSide.VILLAGE);
		this.dailyEffects = new PotionEffectType[]{PotionEffectType.DAMAGE_RESISTANCE};
		this.nightlyEffects = new PotionEffectType[]{PotionEffectType.DAMAGE_RESISTANCE};
	}

	public void revive(Player player, boolean keep) {
		this.canRevive = false;
		if (looseResistanceOnRevive && !keep) {
			this.dailyEffects = null;
			this.nightlyEffects = null;
			PlayerUtil.clearEffect(player, PotionEffectType.DAMAGE_RESISTANCE);
		}
	}

}
