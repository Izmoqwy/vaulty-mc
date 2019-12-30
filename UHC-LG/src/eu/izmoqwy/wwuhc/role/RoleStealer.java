/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.role;

import eu.izmoqwy.vaulty.utils.PlayerUtil;
import eu.izmoqwy.wwuhc.tasks.DayCycle;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public abstract class RoleStealer extends Role {

	@Getter
	private Role stolen;

	public RoleStealer(String name, List<String> description, MaterialData icon, RoleSide roleSide) {
		super(name, description, icon, roleSide);
	}

	@Override
	public void onDayCycle(Player player, DayCycle current, DayCycle previous) {
		if (stolen != null) {
			PlayerUtil.clearEffect(player, PotionEffectType.DAMAGE_RESISTANCE);
			stolen.onDayCycle(player, current, previous);
			return;
		}
		super.onDayCycle(player, current, previous);
	}

	public void steal(Role role) {
		if (stolen != null)
			return;

		stolen = role;
		this.dailyEffects = null;
		this.nightlyEffects = null;
		this.roleSide = role.getRoleSide();
	}

	@Override
	public String getName() {
		if (stolen != null) {
			return super.getName() + " - " + stolen.getName();
		}
		return super.getName();
	}

	@Override
	public RoleSide getRoleSide() {
		if (stolen != null)
			return stolen.getRoleSide();
		return super.getRoleSide();
	}

	@Override
	public boolean isInfected() {
		if (stolen != null)
			return stolen.isInfected() || super.isInfected();
		return super.isInfected();
	}

	@Override
	public Role getRootRole() {
		if (stolen != null)
			return stolen.getRootRole();
		return super.getRootRole();
	}

}
