/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.role.village;

import eu.izmoqwy.uhc.config.GUIConfigurable;
import eu.izmoqwy.uhc.config.GUISetting;
import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.wwuhc.WerewolfUHC;
import eu.izmoqwy.wwuhc.game.WWGameType;
import eu.izmoqwy.wwuhc.role.Role;
import eu.izmoqwy.wwuhc.role.RoleSide;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RoleWildChild extends Role implements GUIConfigurable {

	@Getter
	private UUID master;
	@Getter
	private boolean transformed;

	private BukkitRunnable timeOutTask;

	@SuppressWarnings("deprecation")
	public RoleWildChild() {
		super("Enfant Sauvage", createDescription(
				"Vous devez choisir une personne qui sera votre maître. Une fois celui-ci mort, vous vous verrez transformer en loup-garou. Vous devrez donc gagner avec ceux-ci.",
				"Faîtes '/lg choisir <joueur>' pour choisir votre maître."
		), new MaterialData(Material.RED_ROSE, (byte) 1), RoleSide.VILLAGE);
	}

	@GUISetting(name = "Temps maximal", icon = Material.WATCH, duration = true,
			min = 60, max = 10 * 60)
	private int timeOut = 30;

	@Override
	public void onRoleAttribute(OfflinePlayer player) {
		timeOutTask = new BukkitRunnable() {
			@Override
			public void run() {
				if (master != null)
					return;

				WWGameType werewolf = (WWGameType) GameManager.get.getCurrentComposer().getGameType();
				if (!RoleWildChild.this.equals(werewolf.getRole(player)))
					return;

				List<UUID> possibleMaster = werewolf.getRoleMap().keySet().stream().filter(uuid -> !uuid.equals(player.getUniqueId())).collect(Collectors.toList());
				Collections.shuffle(possibleMaster);

				OfflinePlayer chosen = Bukkit.getOfflinePlayer(possibleMaster.get(0));
				RoleWildChild.this.master = chosen.getUniqueId();
				if (player.isOnline())
					player.getPlayer().sendMessage(werewolf.getPrefix() + "§cVous avez mis trop de temps pour choisir un maître. Votre maître est donc §6§l" + chosen.getName() + "§6.");
			}
		};
		timeOutTask.runTaskLater(WerewolfUHC.getInstance(), 20 * timeOut);
	}

	public void setMaster(UUID master) {
		this.master = master;
		if (timeOutTask != null)
			timeOutTask.cancel();
	}

	public void transform() {
		if (transformed)
			return;

		this.transformed = true;
		this.roleSide = RoleSide.WEREWOLF;
		this.nightlyEffects = new PotionEffectType[]{PotionEffectType.INCREASE_DAMAGE, PotionEffectType.NIGHT_VISION};
	}

	@Override
	public String getName() {
		if (transformed)
			return super.getName() + " (Transformé)";
		return super.getName();
	}
}
