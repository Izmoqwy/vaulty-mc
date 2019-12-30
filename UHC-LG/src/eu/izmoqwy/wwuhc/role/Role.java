/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.role;

import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.vaulty.utils.ItemUtil;
import eu.izmoqwy.vaulty.utils.PlayerUtil;
import eu.izmoqwy.wwuhc.game.WWGameType;
import eu.izmoqwy.wwuhc.role.werewolves.RoleInfected;
import eu.izmoqwy.wwuhc.tasks.DayCycle;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
public abstract class Role {

	private final String name;
	private final List<String> description;
	private final MaterialData icon;

	protected RoleSide roleSide;
	private final boolean multiple;

	@Setter
	private boolean needed = true;

	protected PotionEffectType[] dailyEffects, nightlyEffects;
	protected ItemStack[] itemStacks;

	public Role(String name, List<String> description, MaterialData icon, RoleSide roleSide, boolean multiple) {
		this.name = name;
		this.description = description;
		this.icon = icon;
		this.roleSide = roleSide;
		this.multiple = multiple;
	}

	public Role(String name, List<String> description, MaterialData icon, RoleSide roleSide) {
		this(name, description, icon, roleSide, false);
	}

	protected static List<String> createDescription(String... lines) {
		return Arrays.asList(lines);
	}

	public void onRoleAttribute(OfflinePlayer player) {
	}

	public void onRoleReceive(Player player) {
		if (itemStacks != null && itemStacks.length > 0 && ItemUtil.giveOrDrop(player, itemStacks)) {
			player.sendMessage("§cVotre inventaire était plein, certains objets donnés par votre rôle ont été jetés par terre !");
		}
	}

	public void onDayCycle(Player player, DayCycle current, DayCycle previous) {
		PotionEffectType[] currentEffects = current == DayCycle.DAY ? dailyEffects : nightlyEffects,
				previousEffects = current == DayCycle.DAY ? nightlyEffects : dailyEffects;

		if (previousEffects != null) {
			for (PotionEffectType previousEffect : previousEffects) {
				PlayerUtil.clearEffect(player, previousEffect);
			}
		}
		WWGameType werewolf = (WWGameType) GameManager.get.getCurrentComposer().getGameType();
		if (werewolf.getProtectedPlayer() == player.getUniqueId())
			PlayerUtil.giveEffect(player, PotionEffectType.DAMAGE_RESISTANCE, (short) 0, (short) 20000, true);
		else if (werewolf.getLastProtectedPlayer() == player.getUniqueId())
			PlayerUtil.clearEffect(player, PotionEffectType.DAMAGE_RESISTANCE);
		if (currentEffects != null) {
			for (PotionEffectType currentEffect : currentEffects) {
				PlayerUtil.giveEffect(player, currentEffect, (short) 0, (short) 20000, true);
			}
		}
	}

	protected void onDayCycleMiddle(Player player, DayCycle current, DayCycle previous, Role role) {
		PotionEffectType[] currentEffects = current == DayCycle.DAY ? dailyEffects : nightlyEffects,
				previousEffects = current == DayCycle.DAY ? nightlyEffects : dailyEffects;

		if (previousEffects != null) {
			for (PotionEffectType previousEffect : previousEffects) {
				PlayerUtil.clearEffect(player, previousEffect);
			}
		}
		role.onDayCycle(player, current, previous);
		if (currentEffects != null) {
			for (PotionEffectType currentEffect : currentEffects) {
				PlayerUtil.giveEffect(player, currentEffect, (short) 0, (short) 20000, true);
			}
		}
	}

	public String getColor() {
		return roleSide == RoleSide.VILLAGE ? "§a" : (roleSide == RoleSide.WEREWOLF ? "§c" : "§b");
	}

	public boolean isInfected() {
		return getClass().equals(RoleInfected.class);
	}

	public Role getRootRole() {
		return this;
	}

	public boolean isApplicable(Class<? extends Role> aClass) {
		return aClass.equals(getRootRole().getClass());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Role)) return false;
		Role role = (Role) o;
		return multiple == role.multiple &&
				Objects.equals(name, role.name) &&
				Objects.equals(description, role.description) &&
				Objects.equals(icon, role.icon) &&
				roleSide == role.roleSide &&
				Arrays.equals(dailyEffects, role.dailyEffects) &&
				Arrays.equals(nightlyEffects, role.nightlyEffects) &&
				Arrays.equals(itemStacks, role.itemStacks);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(name, description, icon, roleSide, multiple);
		result = 31 * result + Arrays.hashCode(dailyEffects);
		result = 31 * result + Arrays.hashCode(nightlyEffects);
		result = 31 * result + Arrays.hashCode(itemStacks);
		return result;
	}

	@Override
	public String toString() {
		return "Role{" +
				"name='" + name + '\'' +
				", roleSide=" + roleSide +
				", multiple=" + multiple +
				", needed=" + needed +
				'}';
	}
}
