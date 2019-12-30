/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.role.werewolves;

import eu.izmoqwy.wwuhc.role.Role;
import eu.izmoqwy.wwuhc.role.RoleSide;
import eu.izmoqwy.wwuhc.tasks.DayCycle;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

@Getter
public class RoleInfected extends Role {

	private Role from;

	public RoleInfected(Role from) {
		super(from.getName() + " (Infecté)", null, null, RoleSide.WEREWOLF, false);
		this.from = from;
		this.nightlyEffects = new PotionEffectType[]{PotionEffectType.INCREASE_DAMAGE, PotionEffectType.NIGHT_VISION};
	}

	@Override
	public void onDayCycle(Player player, DayCycle current, DayCycle previous) {
		super.onDayCycleMiddle(player, current, previous, from);
	}

	@Override
	public Role getRootRole() {
		return from;
	}
}
